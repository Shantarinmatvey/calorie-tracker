package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.CalorieEntry
import com.example.data.TrackerRepository
import com.example.data.WeightEntry
import com.example.data.Exercise
import com.example.data.TrainingPlan
import com.example.data.PlanWorkoutDayExercise
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class TrackerViewModel(private val repository: TrackerRepository) : ViewModel() {

    private val _currentDate = MutableStateFlow(getTodayDateStr())
    val currentDate: StateFlow<String> = _currentDate.asStateFlow()

    private val _selectedDate = MutableStateFlow(getTodayDateStr())
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    private val _calorieLimit = MutableStateFlow(2000)
    val calorieLimit: StateFlow<Int> = _calorieLimit.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val calorieEntries: StateFlow<List<CalorieEntry>> = _selectedDate
        .flatMapLatest { date ->
            repository.getCalorieEntries(date)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val weightEntries: StateFlow<List<WeightEntry>> = repository.getWeightEntries()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val exercises: StateFlow<List<Exercise>> = repository.allExercises
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val trainingPlans: StateFlow<List<TrainingPlan>> = repository.allTrainingPlans
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val activeTrainingPlan: StateFlow<TrainingPlan?> = repository.activeTrainingPlanFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val planAssignments: StateFlow<List<PlanWorkoutDayExercise>> = repository.allPlanAssignments
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val completedExercises: StateFlow<Set<Int>> = _selectedDate
        .flatMapLatest { date ->
            repository.getCompletedExercises(date)
                .map { list -> list.map { it.exerciseId }.toSet() }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptySet()
        )

    val duplicateWarningExerciseName = MutableStateFlow<String?>(null)

    fun clearDuplicateWarning() {
        duplicateWarningExerciseName.value = null
    }

    // Live aggregated sum of calories for each date string "yyyy-MM-dd"
    val allCalorieSums: StateFlow<Map<String, Int>> = repository.getAllCalorieEntries()
        .map { entries ->
            entries.groupBy { it.date }.mapValues { group -> group.value.sumOf { it.calories } }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    // Live aggregated list of set calorie limits for each date string "yyyy-MM-dd"
    val allCalorieLimits: StateFlow<Map<String, Int>> = repository.allLimits
        .map { limits ->
            limits.associate { it.date to it.calorieLimit }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    val latestWeight: Double
        get() = weightEntries.value.sortedByDescending { it.date + it.timestamp }.firstOrNull()?.weight ?: 70.0

    init {
        // Automatically check if the system date calendar crossed midnight
        viewModelScope.launch {
            while (true) {
                delay(10000) // Check every 10 seconds
                val today = getTodayDateStr()
                if (today != _currentDate.value) {
                    val wasLookingAtToday = _selectedDate.value == _currentDate.value
                    _currentDate.value = today
                    if (wasLookingAtToday) {
                        selectDate(today)
                    }
                }
            }
        }
        // Load limit of today
        selectDate(getTodayDateStr())

        // Reactive link: automatically sync latest weight to all bodyweight exercises
        viewModelScope.launch {
            combine(repository.getWeightEntries(), repository.allExercises) { weights, exercisesList ->
                Pair(weights, exercisesList)
            }.collect { (weights, exercisesList) ->
                val latest = weights.sortedByDescending { it.date + it.timestamp }.firstOrNull()?.weight ?: 70.0
                exercisesList.forEach { exercise ->
                    if (exercise.isBodyweight && exercise.workingWeight != latest) {
                        repository.insertExercise(exercise.copy(workingWeight = latest))
                    }
                }
            }
        }
    }

    private val repositoryLock = Any()

    fun selectDate(date: String) {
        _selectedDate.value = date
        viewModelScope.launch {
            _calorieLimit.value = repository.getLimitForDate(date)
        }
    }

    fun addCalorieEntry(calories: Int, dishName: String = "Прием пищи", proteins: Int = 0, fats: Int = 0, carbs: Int = 0) {
        viewModelScope.launch {
            val entry = CalorieEntry(
                date = _selectedDate.value,
                dishName = dishName,
                calories = calories,
                proteins = proteins,
                fats = fats,
                carbs = carbs
            )
            repository.insertCalorie(entry)
            // Refresh current limit in case view has changed
            _calorieLimit.value = repository.getLimitForDate(_selectedDate.value)
        }
    }

    fun deleteCalorieEntry(id: Int) {
        viewModelScope.launch {
            repository.deleteCalorie(id)
        }
    }

    fun updateCalorieLimit(limit: Int) {
        viewModelScope.launch {
            repository.saveLimitForDate(_selectedDate.value, limit)
            _calorieLimit.value = limit
        }
    }

    fun addWeightEntry(weight: Double, date: String = _selectedDate.value) {
        viewModelScope.launch {
            val entry = WeightEntry(
                date = date,
                weight = weight
            )
            repository.insertWeight(entry)
        }
    }

    fun deleteWeightEntry(entry: WeightEntry) {
        viewModelScope.launch {
            repository.deleteWeight(entry)
        }
    }

    fun addExercise(name: String, workingWeight: Double, isBodyweight: Boolean, sets: Int, repetitions: Int) {
        viewModelScope.launch {
            val finalWeight = if (isBodyweight) latestWeight else workingWeight
            repository.insertExercise(
                Exercise(
                    name = name,
                    workingWeight = finalWeight,
                    isBodyweight = isBodyweight,
                    sets = sets,
                    repetitions = repetitions
                )
            )
        }
    }

    fun updateExercise(id: Int, name: String, workingWeight: Double, isBodyweight: Boolean, sets: Int, repetitions: Int) {
        viewModelScope.launch {
            val finalWeight = if (isBodyweight) latestWeight else workingWeight
            repository.insertExercise(
                Exercise(
                    id = id,
                    name = name,
                    workingWeight = finalWeight,
                    isBodyweight = isBodyweight,
                    sets = sets,
                    repetitions = repetitions
                )
            )
        }
    }

    fun deleteExercise(id: Int) {
        viewModelScope.launch {
            repository.deleteExercise(id)
        }
    }

    fun addTrainingPlan(name: String) {
        viewModelScope.launch {
            val currentPlans = trainingPlans.value
            val isActive = currentPlans.isEmpty()
            val planId = repository.insertTrainingPlan(
                TrainingPlan(
                    name = name,
                    isActive = isActive
                )
            )
            if (isActive) {
                repository.activatePlan(planId.toInt())
            }
        }
    }

    fun setPlanActive(planId: Int) {
        viewModelScope.launch {
            repository.activatePlan(planId)
        }
    }

    fun deleteTrainingPlan(id: Int) {
        viewModelScope.launch {
            repository.deleteTrainingPlan(id)
        }
    }

    fun assignExerciseToDay(planId: Int, dayOfWeek: Int, exerciseId: Int) {
        val alreadyAssigned = planAssignments.value.any {
            it.planId == planId && it.dayOfWeek == dayOfWeek && it.exerciseId == exerciseId
        }
        if (alreadyAssigned) {
            val exerciseName = exercises.value.find { it.id == exerciseId }?.name ?: "Упражнение"
            duplicateWarningExerciseName.value = exerciseName
            return // Duplicate prevention!
        }
        viewModelScope.launch {
            val currentAssignmentsForDay = planAssignments.value.filter { it.planId == planId && it.dayOfWeek == dayOfWeek }
            val nextOrder = (currentAssignmentsForDay.maxOfOrNull { it.displayOrder } ?: -1) + 1
            repository.insertPlanAssignment(
                PlanWorkoutDayExercise(
                    planId = planId,
                    dayOfWeek = dayOfWeek,
                    exerciseId = exerciseId,
                    displayOrder = nextOrder
                )
            )
        }
    }

    fun removeExerciseFromDay(planId: Int, dayOfWeek: Int, exerciseId: Int) {
        viewModelScope.launch {
            repository.deletePlanAssignment(planId, dayOfWeek, exerciseId)
        }
    }

    fun updateAssignmentsOrder(planId: Int, dayOfWeek: Int, orderedExercises: List<Exercise>) {
        viewModelScope.launch {
            val current = planAssignments.value.filter { it.planId == planId && it.dayOfWeek == dayOfWeek }
            orderedExercises.forEachIndexed { index, exercise ->
                val matchingAssignment = current.find { it.exerciseId == exercise.id }
                if (matchingAssignment != null) {
                    repository.insertPlanAssignment(matchingAssignment.copy(displayOrder = index))
                }
            }
        }
    }

    fun renameTrainingPlan(id: Int, newName: String) {
        viewModelScope.launch {
            val plan = trainingPlans.value.find { it.id == id }
            if (plan != null) {
                repository.insertTrainingPlan(plan.copy(name = newName))
            }
        }
    }

    fun toggleExerciseCompleted(exerciseId: Int) {
        viewModelScope.launch {
            val date = _selectedDate.value
            val isCompleted = completedExercises.value.contains(exerciseId)
            if (isCompleted) {
                repository.deleteCompletedExercise(date, exerciseId)
            } else {
                repository.insertCompletedExercise(com.example.data.CompletedExercise(date = date, exerciseId = exerciseId))
            }
        }
    }

    fun getTodayDateStr(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return formatter.format(Calendar.getInstance().time)
    }

    // Resolves what the applicable calorie limit is for any given date
    fun getCalorieLimitForDateSynchronous(dateStr: String, limitsMap: Map<String, Int>): Int {
        if (limitsMap.containsKey(dateStr)) {
            return limitsMap[dateStr] ?: 2000
        }
        // Find the closest limit before dateStr
        val priorLimit = limitsMap.entries
            .filter { it.key <= dateStr }
            .maxByOrNull { it.key }
        return priorLimit?.value ?: 2000
    }

    // Helper to get formatted name of Month in Russian
    @Suppress("DEPRECATION")
    fun getFormattedSelectedDateForUI(): String {
        return try {
            val sdfInput = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val dateObj = sdfInput.parse(_selectedDate.value) ?: Date()
            val sdfOutput = SimpleDateFormat("d MMMM yyyy", Locale.forLanguageTag("ru"))
            sdfOutput.format(dateObj)
        } catch (e: Exception) {
            _selectedDate.value
        }
    }
}

class TrackerViewModelFactory(private val repository: TrackerRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TrackerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TrackerViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
