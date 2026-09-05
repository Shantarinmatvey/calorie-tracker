package com.example.data

import kotlinx.coroutines.flow.Flow

class TrackerRepository(private val dao: TrackerDao) {

    val allLimits: Flow<List<CalorieLimit>> = dao.getAllLimits()

    fun getCalorieEntries(date: String): Flow<List<CalorieEntry>> = dao.getCalorieEntriesForDate(date)

    fun getAllCalorieEntries(): Flow<List<CalorieEntry>> = dao.getAllCalorieEntries()

    suspend fun insertCalorie(entry: CalorieEntry) = dao.insertCalorieEntry(entry)

    suspend fun deleteCalorie(id: Int) = dao.deleteCalorieEntryById(id)

    fun getWeightEntries(): Flow<List<WeightEntry>> = dao.getAllWeightEntries()

    suspend fun insertWeight(entry: WeightEntry) = dao.insertWeightEntry(entry)

    suspend fun deleteWeight(entry: WeightEntry) = dao.deleteWeightEntry(entry)

    suspend fun getLimitForDate(date: String): Int {
        val limitEntity = dao.getLimitForDateOrEarlier(date)
        return limitEntity?.calorieLimit ?: 2000 // Default 2000 kcal
    }

    suspend fun saveLimitForDate(date: String, limit: Int) {
        dao.insertLimit(CalorieLimit(date, limit))
    }

    // Exercises
    val allExercises: Flow<List<Exercise>> = dao.getAllExercises()

    suspend fun insertExercise(exercise: Exercise) = dao.insertExercise(exercise)

    suspend fun deleteExercise(id: Int) {
        dao.deleteExerciseById(id)
        dao.deleteAssignmentsByExercise(id)
    }

    // Training Plans
    val allTrainingPlans: Flow<List<TrainingPlan>> = dao.getAllTrainingPlans()

    val activeTrainingPlanFlow: Flow<TrainingPlan?> = dao.getActiveTrainingPlanFlow()

    suspend fun getActiveTrainingPlan(): TrainingPlan? = dao.getActiveTrainingPlan()

    suspend fun insertTrainingPlan(plan: TrainingPlan): Long = dao.insertTrainingPlan(plan)

    suspend fun activatePlan(planId: Int) {
        dao.deactivateAllPlans()
        dao.activatePlan(planId)
    }

    suspend fun deleteTrainingPlan(id: Int) {
        dao.deleteTrainingPlanById(id)
        dao.deleteAssignmentsForPlan(id)
    }

    // Assignments
    val allPlanAssignments: Flow<List<PlanWorkoutDayExercise>> = dao.getAllPlanAssignments()

    suspend fun insertPlanAssignment(assignment: PlanWorkoutDayExercise) = dao.insertPlanAssignment(assignment)

    suspend fun deletePlanAssignment(planId: Int, dayOfWeek: Int, exerciseId: Int) =
        dao.deletePlanAssignment(planId, dayOfWeek, exerciseId)

    // Completed Exercises
    fun getCompletedExercises(date: String): Flow<List<CompletedExercise>> = dao.getCompletedExercisesForDate(date)

    suspend fun insertCompletedExercise(completed: CompletedExercise) = dao.insertCompletedExercise(completed)

    suspend fun deleteCompletedExercise(date: String, exerciseId: Int) = dao.deleteCompletedExercise(date, exerciseId)
}
