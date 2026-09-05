package com.example.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "calorie_entries")
data class CalorieEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String, // Format: "yyyy-MM-dd"
    val dishName: String = "Прием пищи",
    val calories: Int,
    val proteins: Int = 0,
    val fats: Int = 0,
    val carbs: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "weight_entries")
data class WeightEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String, // Format: "yyyy-MM-dd"
    val weight: Double,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "calorie_limits")
data class CalorieLimit(
    @PrimaryKey val date: String, // Format: "yyyy-MM-dd"
    val calorieLimit: Int
)

@Entity(tableName = "exercises")
data class Exercise(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val workingWeight: Double,
    val isBodyweight: Boolean = false,
    val sets: Int,
    val repetitions: Int
)

@Entity(tableName = "training_plans")
data class TrainingPlan(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val isActive: Boolean = false
)

@Entity(tableName = "plan_workout_day_exercises")
data class PlanWorkoutDayExercise(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val planId: Int,
    val dayOfWeek: Int, // 0 = Пн, 6 = Вс
    val exerciseId: Int,
    val displayOrder: Int = 0
)

@Entity(tableName = "completed_exercises")
data class CompletedExercise(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String, // Format: "yyyy-MM-dd"
    val exerciseId: Int
)

@Dao
interface TrackerDao {
    // Calorie entries
    @Query("SELECT * FROM calorie_entries WHERE date = :date ORDER BY timestamp DESC")
    fun getCalorieEntriesForDate(date: String): Flow<List<CalorieEntry>>

    @Query("SELECT * FROM calorie_entries ORDER BY timestamp DESC")
    fun getAllCalorieEntries(): Flow<List<CalorieEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalorieEntry(entry: CalorieEntry)

    @Query("DELETE FROM calorie_entries WHERE id = :id")
    suspend fun deleteCalorieEntryById(id: Int)

    // Weight entries
    @Query("SELECT * FROM weight_entries ORDER BY date ASC, timestamp ASC")
    fun getAllWeightEntries(): Flow<List<WeightEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeightEntry(entry: WeightEntry)

    @Delete
    suspend fun deleteWeightEntry(entry: WeightEntry)

    // Calorie limits
    @Query("SELECT * FROM calorie_limits WHERE date = :date")
    suspend fun getLimitForDate(date: String): CalorieLimit?

    @Query("SELECT * FROM calorie_limits WHERE date <= :date ORDER BY date DESC LIMIT 1")
    suspend fun getLimitForDateOrEarlier(date: String): CalorieLimit?

    @Query("SELECT * FROM calorie_limits ORDER BY date DESC")
    fun getAllLimits(): Flow<List<CalorieLimit>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLimit(limit: CalorieLimit)

    // Exercises
    @Query("SELECT * FROM exercises ORDER BY id DESC")
    fun getAllExercises(): Flow<List<Exercise>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: Exercise): Long

    @Query("DELETE FROM exercises WHERE id = :id")
    suspend fun deleteExerciseById(id: Int)

    // Training Plans
    @Query("SELECT * FROM training_plans ORDER BY id DESC")
    fun getAllTrainingPlans(): Flow<List<TrainingPlan>>

    @Query("SELECT * FROM training_plans WHERE isActive = 1 LIMIT 1")
    fun getActiveTrainingPlanFlow(): Flow<TrainingPlan?>

    @Query("SELECT * FROM training_plans WHERE isActive = 1 LIMIT 1")
    suspend fun getActiveTrainingPlan(): TrainingPlan?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrainingPlan(plan: TrainingPlan): Long

    @Query("UPDATE training_plans SET isActive = 0")
    suspend fun deactivateAllPlans()

    @Query("UPDATE training_plans SET isActive = 1 WHERE id = :planId")
    suspend fun activatePlan(planId: Int)

    @Query("DELETE FROM training_plans WHERE id = :id")
    suspend fun deleteTrainingPlanById(id: Int)

    // Assignments
    @Query("SELECT * FROM plan_workout_day_exercises ORDER BY displayOrder ASC, id ASC")
    fun getAllPlanAssignments(): Flow<List<PlanWorkoutDayExercise>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlanAssignment(assignment: PlanWorkoutDayExercise)

    @Query("DELETE FROM plan_workout_day_exercises WHERE planId = :planId AND dayOfWeek = :dayOfWeek AND exerciseId = :exerciseId")
    suspend fun deletePlanAssignment(planId: Int, dayOfWeek: Int, exerciseId: Int)

    @Query("DELETE FROM plan_workout_day_exercises WHERE planId = :planId")
    suspend fun deleteAssignmentsForPlan(planId: Int)

    @Query("DELETE FROM plan_workout_day_exercises WHERE exerciseId = :exerciseId")
    suspend fun deleteAssignmentsByExercise(exerciseId: Int)

    // Completed Exercises
    @Query("SELECT * FROM completed_exercises WHERE date = :date")
    fun getCompletedExercisesForDate(date: String): Flow<List<CompletedExercise>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletedExercise(completed: CompletedExercise)

    @Query("DELETE FROM completed_exercises WHERE date = :date AND exerciseId = :exerciseId")
    suspend fun deleteCompletedExercise(date: String, exerciseId: Int)

    @Query("DELETE FROM completed_exercises WHERE exerciseId = :exerciseId")
    suspend fun deleteCompletedExercisesByExercise(exerciseId: Int)
}

@Database(
    entities = [
        CalorieEntry::class,
        WeightEntry::class,
        CalorieLimit::class,
        Exercise::class,
        TrainingPlan::class,
        PlanWorkoutDayExercise::class,
        CompletedExercise::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trackerDao(): TrackerDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tracker_database"
                )
                .fallbackToDestructiveMigration(true)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
