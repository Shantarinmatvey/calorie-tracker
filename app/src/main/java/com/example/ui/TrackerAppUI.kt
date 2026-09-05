package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.CalorieEntry
import com.example.data.WeightEntry
import com.example.viewmodel.TrackerViewModel
import java.text.SimpleDateFormat
import java.util.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.provider.MediaStore
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch

// "Bold Typography & High Contrast" Color Scheme as specified in guidelines
val AccentBlue = Color(0xFF0061A4)
val LightBlueBg = Color(0xFFD1E4FF)
val DeepNavy = Color(0xFF001E2F)
val OnLightBlue = Color(0xFF001D36)
val CharcoalText = Color(0xFF191C1E)
val MutedText = Color(0xFF41484D)
val DividerColor = Color(0xFFE1E2EC)
val BorderGray = Color(0xFFC1C7CE)
val LightCardBg = Color(0xFFF3F4F9)
val FieldBg = Color(0xFFF1F0F4)
val SoftBackground = Color(0xFFFBFCFF)

// Feedback Alerts matching standard Material specification
val SafeGreen = Color(0xFF006C47)
val WarningRed = Color(0xFFBA1A1A)
val SoftRose = Color(0xFFFFDAD6)

@Composable
fun TrackerAppUI(viewModel: TrackerViewModel) {
    var activeTab by remember { mutableStateOf(0) }

    val currentDate by viewModel.currentDate.collectAsStateWithLifecycle()
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = SoftBackground,
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp,
                modifier = Modifier.navigationBarsPadding()
            ) {
                NavigationBarItem(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Дневник") },
                    label = { Text("Дневник", fontWeight = if (activeTab == 0) FontWeight.Bold else FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DeepNavy,
                        selectedTextColor = DeepNavy,
                        indicatorColor = LightBlueBg,
                        unselectedIconColor = MutedText,
                        unselectedTextColor = MutedText
                    ),
                    modifier = Modifier.testTag("tab_diary")
                )
                NavigationBarItem(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    icon = { Icon(Icons.Default.PlayArrow, contentDescription = "Тренировки") },
                    label = { Text("Тренировки", fontWeight = if (activeTab == 1) FontWeight.Bold else FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DeepNavy,
                        selectedTextColor = DeepNavy,
                        indicatorColor = LightBlueBg,
                        unselectedIconColor = MutedText,
                        unselectedTextColor = MutedText
                    ),
                    modifier = Modifier.testTag("tab_workouts")
                )
                NavigationBarItem(
                    selected = activeTab == 2,
                    onClick = { activeTab = 2 },
                    icon = { Icon(Icons.Default.DateRange, contentDescription = "Календарь") },
                    label = { Text("Календарь", fontWeight = if (activeTab == 2) FontWeight.Bold else FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DeepNavy,
                        selectedTextColor = DeepNavy,
                        indicatorColor = LightBlueBg,
                        unselectedIconColor = MutedText,
                        unselectedTextColor = MutedText
                    ),
                    modifier = Modifier.testTag("tab_calendar")
                )
                NavigationBarItem(
                    selected = activeTab == 3,
                    onClick = { activeTab = 3 },
                    icon = { Icon(Icons.Default.Favorite, contentDescription = "Вес") },
                    label = { Text("Вес", fontWeight = if (activeTab == 3) FontWeight.Bold else FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DeepNavy,
                        selectedTextColor = DeepNavy,
                        indicatorColor = LightBlueBg,
                        unselectedIconColor = MutedText,
                        unselectedTextColor = MutedText
                    ),
                    modifier = Modifier.testTag("tab_weight")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (activeTab) {
                0 -> DailyTrackerScreen(viewModel = viewModel)
                1 -> WorkoutsScreen(viewModel = viewModel)
                2 -> CalendarScreen(viewModel = viewModel)
                3 -> WeightTrackerScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun DailyTrackerScreen(viewModel: TrackerViewModel) {
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val calorieEntries by viewModel.calorieEntries.collectAsStateWithLifecycle()
    val calorieLimit by viewModel.calorieLimit.collectAsStateWithLifecycle()
    val activePlan by viewModel.activeTrainingPlan.collectAsStateWithLifecycle()
    val planAssignments by viewModel.planAssignments.collectAsStateWithLifecycle()
    val exercises by viewModel.exercises.collectAsStateWithLifecycle()
    val weightEntries by viewModel.weightEntries.collectAsStateWithLifecycle()
    val completedExercises by viewModel.completedExercises.collectAsStateWithLifecycle()

    val currentDayOfWeek = remember(selectedDate) {
        getDayOfWeekFromDateStr(selectedDate)
    }

    val selectedDayExercises = remember(activePlan, planAssignments, exercises, currentDayOfWeek) {
        if (activePlan == null || currentDayOfWeek < 0) emptyList()
        else {
            val assigned = planAssignments
                .filter { it.planId == activePlan!!.id && it.dayOfWeek == currentDayOfWeek }
                .sortedBy { it.displayOrder }
            assigned.mapNotNull { assignment ->
                exercises.find { it.id == assignment.exerciseId }
            }
        }
    }

    val todayStr = remember {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        formatter.format(Calendar.getInstance().time)
    }

    val latestWeight = remember(weightEntries) {
        weightEntries.sortedByDescending { it.date + it.timestamp }.firstOrNull()?.weight ?: 70.0
    }

    val totalConsumed = calorieEntries.sumOf { it.calories }
    val totalProteins = calorieEntries.sumOf { it.proteins }
    val totalFats = calorieEntries.sumOf { it.fats }
    val totalCarbs = calorieEntries.sumOf { it.carbs }
    val remaining = calorieLimit - totalConsumed
    val isLimitExceeded = remaining < 0

    var calorieInput by remember { mutableStateOf("") }
    var showLimitDialog by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var analysisState by remember { mutableStateOf<GeminiFoodHelper.ResultState>(GeminiFoodHelper.ResultState.Idle) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val bitmap = try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val options = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
                BitmapFactory.decodeStream(inputStream, null, options)
                inputStream?.close()

                // Calculate stable downscaling sample size to prevent OOMs on large images
                val maxLimit = 1024
                var sampleSize = 1
                if (options.outHeight > maxLimit || options.outWidth > maxLimit) {
                    val halfHeight = options.outHeight / 2
                    val halfWidth = options.outWidth / 2
                    while ((halfHeight / sampleSize) >= maxLimit && (halfWidth / sampleSize) >= maxLimit) {
                        sampleSize *= 2
                    }
                }

                val finalOptions = BitmapFactory.Options().apply {
                    this.inSampleSize = sampleSize
                    inPreferredConfig = Bitmap.Config.ARGB_8888
                }
                val finalInputStream = context.contentResolver.openInputStream(uri)
                val decoded = BitmapFactory.decodeStream(finalInputStream, null, finalOptions)
                finalInputStream?.close()
                decoded
            } catch (e: Exception) {
                null
            }
            if (bitmap != null) {
                analysisState = GeminiFoodHelper.ResultState.Loading
                coroutineScope.launch {
                    analysisState = GeminiFoodHelper.analyzeFoodImage(bitmap)
                }
            } else {
                analysisState = GeminiFoodHelper.ResultState.Error("Не удалось получить изображение.")
            }
        }
    }

    if (analysisState is GeminiFoodHelper.ResultState.Loading) {
        AlertDialog(
            onDismissRequest = {},
            confirmButton = {},
            dismissButton = {},
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CircularProgressIndicator(color = AccentBlue, strokeWidth = 3.dp)
                    Text(
                        text = "Анализ блюда...",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = DeepNavy
                    )
                }
            },
            text = {
                Text(
                    text = "ИИ Gemini изучает фотографию, распознает блюдо и рассчитывает примерные калории...",
                    fontSize = 14.sp,
                    color = CharcoalText,
                    fontWeight = FontWeight.Medium
                )
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(26.dp)
        )
    }

    if (analysisState is GeminiFoodHelper.ResultState.Success) {
        val successState = analysisState as GeminiFoodHelper.ResultState.Success
        AlertDialog(
            onDismissRequest = { analysisState = GeminiFoodHelper.ResultState.Idle },
            title = {
                Text(
                    text = "Распознано с ИИ ✨",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = DeepNavy
                )
            },
            text = {
                Column {
                    Text(
                        text = "Результат анализа:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MutedText,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = successState.dish,
                        fontSize = 16.sp,
                        color = DeepNavy,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Пищевая ценность (КБЖУ):",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MutedText,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .background(FieldBg, RoundedCornerShape(10.dp))
                                .padding(vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Ккал", fontSize = 9.sp, color = MutedText, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("${successState.calories}", fontSize = 12.sp, color = DeepNavy, fontWeight = FontWeight.Black)
                        }
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .background(FieldBg, RoundedCornerShape(10.dp))
                                .padding(vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Белки", fontSize = 9.sp, color = MutedText, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("${successState.proteins} г", fontSize = 12.sp, color = DeepNavy, fontWeight = FontWeight.Black)
                        }
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .background(FieldBg, RoundedCornerShape(10.dp))
                                .padding(vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Жиры", fontSize = 9.sp, color = MutedText, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("${successState.fats} г", fontSize = 12.sp, color = DeepNavy, fontWeight = FontWeight.Black)
                        }
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .background(FieldBg, RoundedCornerShape(10.dp))
                                .padding(vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Углев.", fontSize = 9.sp, color = MutedText, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("${successState.carbs} г", fontSize = 12.sp, color = DeepNavy, fontWeight = FontWeight.Black)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addCalorieEntry(
                            calories = successState.calories,
                            dishName = successState.dish,
                            proteins = successState.proteins,
                            fats = successState.fats,
                            carbs = successState.carbs
                        )
                        analysisState = GeminiFoodHelper.ResultState.Idle
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentBlue, contentColor = Color.White),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp, pressedElevation = 0.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Добавить", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { analysisState = GeminiFoodHelper.ResultState.Idle },
                    colors = ButtonDefaults.textButtonColors(contentColor = MutedText)
                ) {
                    Text("Отмена", fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(26.dp)
        )
    }

    if (analysisState is GeminiFoodHelper.ResultState.Error) {
        val errorState = analysisState as GeminiFoodHelper.ResultState.Error
        AlertDialog(
            onDismissRequest = { analysisState = GeminiFoodHelper.ResultState.Idle },
            title = {
                Text(
                    text = "Ошибка распознавания",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = WarningRed
                )
            },
            text = {
                Text(
                    text = errorState.message,
                    fontSize = 14.sp,
                    color = CharcoalText,
                    fontWeight = FontWeight.Medium
                )
            },
            confirmButton = {
                Button(
                    onClick = { analysisState = GeminiFoodHelper.ResultState.Idle },
                    colors = ButtonDefaults.buttonColors(containerColor = WarningRed, contentColor = Color.White),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp, pressedElevation = 0.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("ОК", fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(26.dp)
        )
    }

    if (showLimitDialog) {
        CalorieLimitDialog(
            currentLimit = calorieLimit,
            onDismiss = { showLimitDialog = false },
            onSave = { newLimit ->
                viewModel.updateCalorieLimit(newLimit)
                showLimitDialog = false
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(top = 24.dp, bottom = 32.dp)
    ) {
        // High-End Header Card with Bold Typography tracking
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "СЕГОДНЯ, ${viewModel.getFormattedSelectedDateForUI().uppercase(Locale.forLanguageTag("ru"))}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MutedText,
                        letterSpacing = 1.2.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Дневник питания",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = DeepNavy,
                        letterSpacing = (-0.5).sp
                    )
                }
                IconButton(
                    onClick = { showLimitDialog = true },
                    modifier = Modifier
                        .background(LightBlueBg, CircleShape)
                        .border(BorderStroke(1.dp, AccentBlue.copy(alpha = 0.2f)), CircleShape)
                        .size(44.dp)
                        .testTag("btn_edit_limit")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Изменить лимит",
                        tint = AccentBlue
                    )
                }
            }
        }

        // Beautiful Interactive Circular Progress Display with custom capsule badges
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(26.dp),
                border = BorderStroke(1.dp, DividerColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(200.dp)
                            .padding(12.dp)
                    ) {
                        // Drawing dynamic circle indicator
                        val progressFraction = if (calorieLimit > 0) {
                            (totalConsumed.toFloat() / calorieLimit.toFloat()).coerceAtMost(1f)
                        } else 0f

                        val sweepColor = if (isLimitExceeded) WarningRed else AccentBlue

                        Canvas(modifier = Modifier.fillMaxSize()) {
                            // Dark Background Arc circle
                            drawCircle(
                                color = DividerColor,
                                style = Stroke(width = 14.dp.toPx())
                            )
                            // Filled Progress Arc circle
                            drawArc(
                                color = sweepColor,
                                startAngle = -90f,
                                sweepAngle = progressFraction * 360f,
                                useCenter = false,
                                style = Stroke(width = 14.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }

                        // Inside Text info formatted with extra bold typography
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$totalConsumed",
                                fontSize = 46.sp,
                                fontWeight = FontWeight.Black,
                                color = DeepNavy,
                                letterSpacing = (-1).sp
                            )
                            Text(
                                text = "ккал съедено".uppercase(Locale.forLanguageTag("ru")),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MutedText,
                                letterSpacing = 1.sp
                            )
                            
                            Spacer(modifier = Modifier.height(10.dp))
                            
                            // High Contrast remaining status pill indicator matching HTML mockup
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(if (isLimitExceeded) SoftRose else LightBlueBg)
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (isLimitExceeded) 
                                        "Превышение ${kotlin.math.abs(remaining)}" 
                                    else 
                                        "Осталось $remaining",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isLimitExceeded) WarningRed else OnLightBlue
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    HorizontalDivider(color = DividerColor)

                    Spacer(modifier = Modifier.height(20.dp))

                    // Minimalist Trackers Grid for Calories & BZHU
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Calorie Tracker
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .background(LightBlueBg.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                                .padding(vertical = 12.dp, horizontal = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("🔥", fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Калории",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MutedText
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$totalConsumed",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = DeepNavy
                            )
                            Text(
                                text = "ккал",
                                fontSize = 10.sp,
                                color = MutedText,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        
                        // Proteins Tracker
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .background(FieldBg, RoundedCornerShape(16.dp))
                                .padding(vertical = 12.dp, horizontal = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("🥩", fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Белки",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MutedText
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$totalProteins г",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = DeepNavy
                            )
                            Text(
                                text = "грамм",
                                fontSize = 10.sp,
                                color = MutedText,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        
                        // Fats Tracker
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .background(FieldBg, RoundedCornerShape(16.dp))
                                .padding(vertical = 12.dp, horizontal = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("🥑", fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Жиры",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MutedText
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$totalFats г",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = DeepNavy
                            )
                            Text(
                                text = "грамм",
                                fontSize = 10.sp,
                                color = MutedText,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        
                        // Carbs Tracker
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .background(FieldBg, RoundedCornerShape(16.dp))
                                .padding(vertical = 12.dp, horizontal = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("🌾", fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Углеводы",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MutedText
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$totalCarbs г",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = DeepNavy
                            )
                            Text(
                                text = "грамм",
                                fontSize = 10.sp,
                                color = MutedText,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // Today's/Selected day's workout banner
        if (selectedDayExercises.isNotEmpty() && activePlan != null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = LightBlueBg.copy(alpha = 0.25f)),
                    shape = RoundedCornerShape(26.dp),
                    border = BorderStroke(1.5.dp, AccentBlue.copy(alpha = 0.3f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                        .testTag("workout_banner")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("💪", fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (selectedDate == todayStr) "Сегодняшняя тренировка" else "Тренировка на этот день",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = DeepNavy
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(AccentBlue.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                val weekdaysAbbr = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")
                                val dayAbbr = if (currentDayOfWeek in 0..6) weekdaysAbbr[currentDayOfWeek] else ""
                                Text(
                                    text = dayAbbr,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentBlue
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "План: ${activePlan?.name}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MutedText
                        )

                        HorizontalDivider(color = AccentBlue.copy(alpha = 0.15f), modifier = Modifier.padding(vertical = 12.dp))

                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            selectedDayExercises.forEach { exercise ->
                                val isCompleted = completedExercises.contains(exercise.id)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(if (isCompleted) SafeGreen.copy(alpha = 0.05f) else Color.White, RoundedCornerShape(14.dp))
                                        .border(
                                            BorderStroke(
                                                width = 1.dp,
                                                color = if (isCompleted) SafeGreen.copy(alpha = 0.4f) else DividerColor
                                            ),
                                            RoundedCornerShape(14.dp)
                                        )
                                        .clickable { viewModel.toggleExerciseCompleted(exercise.id) }
                                        .padding(horizontal = 14.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.Check,
                                            contentDescription = if (isCompleted) "Выполнено" else "Не выполнено",
                                            tint = if (isCompleted) SafeGreen else BorderGray.copy(alpha = 0.5f),
                                            modifier = Modifier.size(24.dp)
                                        )

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column {
                                            Text(
                                                text = exercise.name,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = if (isCompleted) MutedText else CharcoalText,
                                                style = if (isCompleted) TextStyle(textDecoration = TextDecoration.LineThrough) else TextStyle.Default
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = "${exercise.sets} подх. \u2022 ${exercise.repetitions} повт.",
                                                    fontSize = 12.sp,
                                                    color = MutedText,
                                                    fontWeight = FontWeight.Bold,
                                                    style = if (isCompleted) TextStyle(textDecoration = TextDecoration.LineThrough) else TextStyle.Default
                                                )
                                            }
                                        }
                                    }
                                    
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isCompleted) SafeGreen.copy(alpha = 0.1f) else FieldBg)
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        val displayWeightText = if (exercise.isBodyweight) {
                                            "Вес тела"
                                        } else {
                                            "${exercise.workingWeight} кг"
                                        }
                                        Text(
                                            text = displayWeightText,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (isCompleted) SafeGreen else DeepNavy
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Smart AI Food Recognition Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(26.dp),
                border = BorderStroke(1.dp, DividerColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "РАСПОЗНАВАНИЕ ЕДЫ ИИ",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MutedText,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Gemini AI ✨",
                            fontSize = 11.sp,
                            color = AccentBlue,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Text(
                        text = "Сфотографируйте ваше блюдо, и искусственный интеллект Gemini автоматически распознает его и определит калории.",
                        fontSize = 13.sp,
                        color = CharcoalText,
                        lineHeight = 18.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        textAlign = TextAlign.Start
                    )

                    Button(
                        onClick = {
                            imagePickerLauncher.launch("image/*")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentBlue, contentColor = Color.White),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 0.dp,
                            pressedElevation = 0.dp,
                            hoveredElevation = 0.dp,
                            focusedElevation = 0.dp
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("btn_take_photo")
                    ) {
                        Text("📸", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Сфотографировать блюдо",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }

        // Manual Calorie Input
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(26.dp),
                border = BorderStroke(1.dp, DividerColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "БЫСТРЫЙ ВВОД",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MutedText,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Лимит: $calorieLimit ккал",
                            fontSize = 11.sp,
                            color = AccentBlue,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = calorieInput,
                            onValueChange = { input ->
                                if (input.all { it.isDigit() } && input.length <= 5) {
                                    calorieInput = input
                                }
                            },
                            placeholder = { Text("0", color = MutedText, fontWeight = FontWeight.Bold) },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AccentBlue,
                                unfocusedBorderColor = Color.Transparent,
                                focusedContainerColor = FieldBg,
                                unfocusedContainerColor = FieldBg,
                                cursorColor = AccentBlue,
                                focusedTextColor = CharcoalText,
                                unfocusedTextColor = CharcoalText
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("inp_calories")
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Button(
                            onClick = {
                                val cal = calorieInput.toIntOrNull()
                                if (cal != null && cal > 0) {
                                    viewModel.addCalorieEntry(cal)
                                    calorieInput = ""
                                    focusManager.clearFocus()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentBlue, contentColor = Color.White),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp, pressedElevation = 0.dp, hoveredElevation = 0.dp, focusedElevation = 0.dp),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .height(56.dp)
                                .testTag("btn_add_calories")
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Добавить", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Preset buttons matching HTML style button grid exactly
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(100, 250, 500).forEach { amount ->
                            OutlinedButton(
                                onClick = {
                                    viewModel.addCalorieEntry(amount)
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MutedText
                                ),
                                border = BorderStroke(1.dp, BorderGray),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                                    .testTag("btn_plus_$amount")
                            ) {
                                Text("+$amount", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Today's Activity calorie logs List Header
        item {
            Text(
                text = "ЗАПИСИ НА СЕГОДНЯ",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MutedText,
                letterSpacing = 1.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 8.dp),
                textAlign = TextAlign.Start
            )
        }

        // Empty logs list visual placeholder of food
        if (calorieEntries.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            tint = MutedText,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Ещё нет записей. Введите данные выше!",
                            fontSize = 14.sp,
                            color = MutedText,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        } else {
            items(calorieEntries, key = { it.id }) { entry ->
                var isExpanded by remember { mutableStateOf(false) }
                
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .border(BorderStroke(1.dp, DividerColor), RoundedCornerShape(16.dp))
                        .clickable { isExpanded = !isExpanded }
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(LightBlueBg, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "🍛",
                                    fontSize = 18.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${entry.dishName} — ${entry.calories} ккал",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = DeepNavy
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    val timeFormat = try {
                                        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(entry.timestamp))
                                    } catch (e: Exception) {
                                        ""
                                    }
                                    if (timeFormat.isNotEmpty()) {
                                        Text(
                                            text = "Время: $timeFormat",
                                            fontSize = 11.sp,
                                            color = MutedText,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isExpanded) "▲ Скрыть КБЖУ" else "▼ Подробнее КБЖУ",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AccentBlue
                                    )
                                }
                            }
                        }

                        IconButton(
                            onClick = { viewModel.deleteCalorieEntry(entry.id) },
                            modifier = Modifier.testTag("btn_delete_calorie_${entry.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Удалить запись",
                                tint = WarningRed,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    
                    if (isExpanded) {
                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = DividerColor)
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Proteins
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(FieldBg, RoundedCornerShape(10.dp))
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Белки", fontSize = 10.sp, color = MutedText, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text("${entry.proteins} г", fontSize = 12.sp, color = DeepNavy, fontWeight = FontWeight.Black)
                                }
                            }
                            // Fats
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(FieldBg, RoundedCornerShape(10.dp))
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Жиры", fontSize = 10.sp, color = MutedText, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text("${entry.fats} г", fontSize = 12.sp, color = DeepNavy, fontWeight = FontWeight.Black)
                                }
                            }
                            // Carbs
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(FieldBg, RoundedCornerShape(10.dp))
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Углеводы", fontSize = 10.sp, color = MutedText, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text("${entry.carbs} г", fontSize = 12.sp, color = DeepNavy, fontWeight = FontWeight.Black)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CalendarScreen(viewModel: TrackerViewModel) {
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val allCalorieSums by viewModel.allCalorieSums.collectAsStateWithLifecycle()
    val allCalorieLimits by viewModel.allCalorieLimits.collectAsStateWithLifecycle()
    val calorieEntries by viewModel.calorieEntries.collectAsStateWithLifecycle()
    val activePlan by viewModel.activeTrainingPlan.collectAsStateWithLifecycle()
    val assignments by viewModel.planAssignments.collectAsStateWithLifecycle()

    var calendarInstance by remember { mutableStateOf(Calendar.getInstance()) }
    val currentYear = calendarInstance.get(Calendar.YEAR)
    val currentMonth = calendarInstance.get(Calendar.MONTH)

    val monthName = remember(currentMonth) {
        val formatter = try {
            SimpleDateFormat("LLLL yyyy", Locale.forLanguageTag("ru"))
        } catch (e: Exception) {
            SimpleDateFormat("MMMM yyyy", Locale.forLanguageTag("ru"))
        }
        formatter.format(calendarInstance.time).replaceFirstChar { it.uppercase() }
    }

    // Prepare calendar grid days
    val daysList = remember(currentYear, currentMonth) {
        getCalendarDays(currentYear, currentMonth)
    }

    val totalCalForSelected = allCalorieSums[selectedDate] ?: 0
    val limitForSelected = viewModel.getCalorieLimitForDateSynchronous(selectedDate, allCalorieLimits)
    val entriesForSelected = calorieEntries

    val calendarScrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(calendarScrollState)
            .padding(horizontal = 20.dp)
            .padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Month Selection Header Navigation with High Contrast chevrons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    val nextCalendar = calendarInstance.clone() as Calendar
                    nextCalendar.add(Calendar.MONTH, -1)
                    calendarInstance = nextCalendar
                },
                modifier = Modifier
                    .background(LightBlueBg, CircleShape)
                    .border(BorderStroke(1.dp, AccentBlue.copy(alpha = 0.2f)), CircleShape)
                    .size(44.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Предыдущий месяц",
                    tint = AccentBlue
                )
            }

            Text(
                text = monthName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = DeepNavy,
                letterSpacing = (-0.5).sp
            )

            IconButton(
                onClick = {
                    val nextCalendar = calendarInstance.clone() as Calendar
                    nextCalendar.add(Calendar.MONTH, 1)
                    calendarInstance = nextCalendar
                },
                modifier = Modifier
                    .background(LightBlueBg, CircleShape)
                    .border(BorderStroke(1.dp, AccentBlue.copy(alpha = 0.2f)), CircleShape)
                    .size(44.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Следующий месяц",
                    tint = AccentBlue
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Days labels of weekdays (ПН ... ВС) in Bold Capitalized style
        Row(modifier = Modifier.fillMaxWidth()) {
            val weekdays = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")
            weekdays.forEach { weekday ->
                Text(
                    text = weekday.uppercase(Locale.forLanguageTag("ru")),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp,
                    color = MutedText,
                    letterSpacing = 1.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Calendar Grid Matrix Cards - Enlarged & Flattened
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(26.dp),
            border = BorderStroke(1.dp, DividerColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Loop chunks of 7 cells
                daysList.chunked(7).forEach { weekDays ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        weekDays.forEach { day ->
                            val isSelected = day.dateStr == selectedDate
                            val isToday = day.isToday
                            val dailyCalories = allCalorieSums[day.dateStr] ?: 0
                            val dailyLimitForCell = viewModel.getCalorieLimitForDateSynchronous(day.dateStr, allCalorieLimits)

                            // Weekly recurrence check for the active plan
                            val calDayOfWeek = remember(day.dateStr) {
                                getDayOfWeekFromDateStr(day.dateStr)
                            }
                            val hasWorkout = remember(activePlan, assignments, calDayOfWeek) {
                                activePlan != null && calDayOfWeek >= 0 && assignments.any {
                                    it.planId == activePlan!!.id && it.dayOfWeek == calDayOfWeek
                                }
                            }

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        when {
                                            isSelected -> AccentBlue
                                            isToday -> LightBlueBg
                                            hasWorkout -> LightBlueBg.copy(alpha = 0.4f)
                                            else -> Color.Transparent
                                        }
                                    )
                                    .clickable {
                                        viewModel.selectDate(day.dateStr)
                                    }
                                    .testTag("day_cell_${day.dateStr}"),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "${day.dayOfMonth}",
                                    fontSize = 16.sp,
                                    fontWeight = if (isSelected || isToday) FontWeight.Black else FontWeight.Bold,
                                    color = when {
                                        isSelected -> Color.White
                                        isToday -> OnLightBlue
                                        day.isCurrentMonth -> CharcoalText
                                        else -> BorderGray
                                    }
                                )

                                // Dual wellness / workout indicators coexisting beautifully
                                if (dailyCalories > 0 || hasWorkout) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (dailyCalories > 0) {
                                            val isCellExceeded = dailyCalories > dailyLimitForCell
                                            Box(
                                                modifier = Modifier
                                                    .size(6.dp)
                                                    .background(
                                                        color = if (isCellExceeded) WarningRed else SafeGreen,
                                                        shape = CircleShape
                                                    )
                                            )
                                        }
                                        if (dailyCalories > 0 && hasWorkout) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                        }
                                        if (hasWorkout) {
                                            Box(
                                                modifier = Modifier
                                                    .size(6.dp)
                                                    .background(
                                                        color = if (isSelected) Color.White else AccentBlue,
                                                        shape = CircleShape
                                                    )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Information Card about Selected Date History Header
        Text(
            text = "СВОДКА ЗА ВЫБРАННЫЙ ДЕНЬ",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MutedText,
            letterSpacing = 1.2.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(26.dp),
            border = BorderStroke(1.dp, DividerColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = viewModel.getFormattedSelectedDateForUI(),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = DeepNavy
                    )
                    val percent = if (limitForSelected > 0) (totalCalForSelected * 100) / limitForSelected else 0
                    Text(
                        text = "$percent% лимита",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = if (totalCalForSelected > limitForSelected) WarningRed else SafeGreen
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Потреблено", fontSize = 12.sp, color = MutedText, fontWeight = FontWeight.Bold)
                        Text("$totalCalForSelected ккал", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = CharcoalText)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Лимит калорий", fontSize = 12.sp, color = MutedText, fontWeight = FontWeight.Bold)
                        Text("$limitForSelected ккал", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = CharcoalText)
                    }
                }

                // If logs exist, display mini summary of records
                if (entriesForSelected.isNotEmpty()) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = DividerColor)
                    Text(
                        text = "Записи калорий:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = DeepNavy
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    entriesForSelected.take(2).forEach {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("• ${it.dishName}", fontSize = 13.sp, color = MutedText, fontWeight = FontWeight.Medium, maxLines = 1)
                            Text("${it.calories} ккал", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AccentBlue)
                        }
                    }
                    if (entriesForSelected.size > 2) {
                        Text(
                            text = "и ещё ${entriesForSelected.size - 2} записи(ей)...",
                            fontSize = 11.sp,
                            color = MutedText,
                            modifier = Modifier.padding(top = 4.dp),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WeightTrackerScreen(viewModel: TrackerViewModel) {
    val weightEntries by viewModel.weightEntries.collectAsStateWithLifecycle()
    var weightInput by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(top = 24.dp, bottom = 32.dp)
    ) {
        // Tracker title
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Text(
                    text = "КОНТРОЛЬ ВЕСА И ПРОГРЕСС",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MutedText,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Панель веса",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = DeepNavy,
                    letterSpacing = (-0.5).sp
                )
            }
        }

        // Beautiful Interactive Highlighted status card for recent recorded weight
        item {
            val latestWeight = weightEntries.sortedByDescending { it.date + it.timestamp }.firstOrNull()?.weight
            if (latestWeight != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = LightCardBg),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "ТЕКУЩИЙ ВЕС",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MutedText,
                                letterSpacing = 1.sp
                            )
                            Row(
                                verticalAlignment = Alignment.Bottom,
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                Text(
                                    text = "$latestWeight",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Black,
                                    color = DeepNavy
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "кг",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MutedText,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }
                        }
                        // Sparkline graphics visualization
                        Box(modifier = Modifier.size(width = 100.dp, height = 40.dp)) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val path = Path().apply {
                                    moveTo(0f, size.height * 0.8f)
                                    quadraticTo(size.width * 0.3f, size.height * 0.7f, size.width * 0.45f, size.height * 0.45f)
                                    quadraticTo(size.width * 0.75f, size.height * 0.3f, size.width, size.height * 0.15f)
                                }
                                drawPath(
                                    path = path, 
                                    color = AccentBlue, 
                                    style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                                )
                                drawCircle(color = AccentBlue, radius = 3.5.dp.toPx(), center = Offset(size.width, size.height * 0.15f))
                            }
                        }
                    }
                }
            }
        }

        // Custom Weight interactive Canvas Chart Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(26.dp),
                border = BorderStroke(1.dp, DividerColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Text(
                        text = "График колебаний веса",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = DeepNavy,
                        modifier = Modifier.padding(bottom = 14.dp)
                    )

                    if (weightEntries.size < 2) {
                        // Placeholders for less than 2 entries
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Введите вес как минимум за два разных дня,\nчтобы увидеть график динамики колебаний.",
                                fontSize = 13.sp,
                                color = MutedText,
                                textAlign = TextAlign.Center,
                                lineHeight = 18.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    } else {
                        // Drawing line fluctuations chart
                        WeightCustomChart(weightEntries)
                    }
                }
            }
        }

        // Weight Manual entries Log Form
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(26.dp),
                border = BorderStroke(1.dp, DividerColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Записать текущий вес",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = DeepNavy,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = weightInput,
                            onValueChange = { input ->
                                // Custom weight logic validation checking decimals/fractions
                                if (input.all { it.isDigit() || it == '.' } && input.count { it == '.' } <= 1 && input.length <= 6) {
                                    weightInput = input
                                }
                            },
                            placeholder = { Text("Вес (кг)", color = MutedText, fontWeight = FontWeight.Bold) },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal,
                                imeAction = ImeAction.Done
                            ),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AccentBlue,
                                unfocusedBorderColor = Color.Transparent,
                                focusedContainerColor = FieldBg,
                                unfocusedContainerColor = FieldBg,
                                cursorColor = AccentBlue,
                                focusedTextColor = CharcoalText,
                                unfocusedTextColor = CharcoalText
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("inp_weight")
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Button(
                            onClick = {
                                val wVal = weightInput.toDoubleOrNull()
                                if (wVal != null && wVal > 10.0 && wVal < 400.0) {
                                    viewModel.addWeightEntry(wVal)
                                    weightInput = ""
                                    focusManager.clearFocus()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentBlue, contentColor = Color.White),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp, pressedElevation = 0.dp, hoveredElevation = 0.dp, focusedElevation = 0.dp),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .height(56.dp)
                                .testTag("btn_add_weight")
                        ) {
                            Text("Записать", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }

        // Historical listing header
        item {
            Text(
                text = "ИСТОРИЯ ВЗВЕШИВАНИЙ",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MutedText,
                letterSpacing = 1.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 8.dp),
                textAlign = TextAlign.Start
            )
        }

        // Listing records
        if (weightEntries.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Записей веса пока нет.",
                        fontSize = 14.sp,
                        color = MutedText,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        } else {
            items(weightEntries.sortedByDescending { it.date + it.timestamp }, key = { it.id }) { entry ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .border(BorderStroke(1.dp, DividerColor), RoundedCornerShape(16.dp))
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(LightBlueBg, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("⚖️", fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            val displayDate = try {
                                val sdfInput = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                                val dateObj = sdfInput.parse(entry.date) ?: Date()
                                SimpleDateFormat("d MMMM yyyy", Locale.forLanguageTag("ru")).format(dateObj)
                            } catch (e: Exception) {
                                entry.date
                            }
                            Text(
                                text = "$displayDate",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = DeepNavy
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${entry.weight} кг",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = AccentBlue,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        IconButton(
                            onClick = { viewModel.deleteWeightEntry(entry) },
                            modifier = Modifier.testTag("btn_delete_weight_${entry.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Удалить запись веса",
                                tint = WarningRed,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WeightCustomChart(weightEntries: List<WeightEntry>) {
    // Sort weight entries by date safely
    val sorted = remember(weightEntries) {
        weightEntries.sortedBy { it.date }
    }

    val weights = sorted.map { it.weight.toFloat() }
    val maxWeight = (weights.maxOrNull() ?: 100f) + 1.5f
    val minWeight = (weights.minOrNull() ?: 50f) - 1.5f
    val weightDelta = maxWeight - minWeight

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(top = 10.dp, bottom = 4.dp)
    ) {
        val width = size.width
        val height = size.height

        val pointCount = sorted.size
        val xInterval = width / (pointCount - 1).coerceAtLeast(1)

        val points = sorted.mapIndexed { index, entry ->
            val scaleY = (entry.weight.toFloat() - minWeight) / weightDelta
            val x = index * xInterval
            val y = height - (scaleY * height)
            Offset(x, y)
        }

        // Draw horizontal grid lines (min, mid, max weight guidelines)
        val gridLinesCount = 3
        for (i in 0 until gridLinesCount) {
            val gridFraction = i.toFloat() / (gridLinesCount - 1)
            val gridY = height * gridFraction
            drawLine(
                color = DividerColor,
                start = Offset(0f, gridY),
                end = Offset(width, gridY),
                strokeWidth = 1.dp.toPx()
            )
        }

        // Make drawing path loops of bezier curves
        val path = Path().apply {
            if (points.isNotEmpty()) {
                moveTo(points.first().x, points.first().y)
                for (i in 1 until points.size) {
                    val pPrev = points[i - 1]
                    val pCurr = points[i]
                    // Control points for nice smooth curves
                    cubicTo(
                        (pPrev.x + pCurr.x) / 2f, pPrev.y,
                        (pPrev.x + pCurr.x) / 2f, pCurr.y,
                        pCurr.x, pCurr.y
                    )
                }
            }
        }

        // Semi transparent bottom fill shading shader gradient
        val fillPath = Path().apply {
            addPath(path)
            if (points.isNotEmpty()) {
                lineTo(points.last().x, height)
                lineTo(points.first().x, height)
                close()
            }
        }

        // Draw Shading Area using theme colors
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(AccentBlue.copy(alpha = 0.35f), Color.Transparent),
                startY = 0f,
                endY = height
            )
        )

        // Draw connecting curves line
        drawPath(
            path = path,
            color = AccentBlue,
            style = Stroke(width = 3.dp.toPx())
        )

        // Draw points with rounded white halos
        points.forEachIndexed { idx, offset ->
            drawCircle(
                color = Color.White,
                radius = 6.dp.toPx(),
                center = offset
            )
            drawCircle(
                color = AccentBlue,
                radius = 3.5.dp.toPx(),
                center = offset
            )
        }
    }
    
    // Labels corresponding to X-axis dates below the chart
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val sorted = weightEntries.sortedBy { it.date }
        if (sorted.isNotEmpty()) {
            val labelCount = if (sorted.size >= 4) 4 else sorted.size
            for (i in 0 until labelCount) {
                val idx = if (labelCount > 1) {
                    ((sorted.size - 1) * i) / (labelCount - 1)
                } else 0
                
                val item = sorted.getOrNull(idx)
                val displayLabel = if (item != null) {
                    try {
                        val inputSdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                        val dateObj = inputSdf.parse(item.date) ?: Date()
                        SimpleDateFormat("d.MM", Locale.US).format(dateObj)
                    } catch (e: Exception) {
                        item.date
                    }
                } else ""
                
                Text(
                    text = displayLabel,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MutedText
                )
            }
        }
    }
}

@Composable
fun CalorieLimitDialog(
    currentLimit: Int,
    onDismiss: () -> Unit,
    onSave: (Int) -> Unit
) {
    var limitInput by remember { mutableStateOf(currentLimit.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Лимит калорий", fontWeight = FontWeight.ExtraBold, color = DeepNavy, fontSize = 18.sp) },
        text = {
            Column {
                Text(
                    text = "Установите целевую норму потребления калорий на день (ккал):",
                    fontSize = 14.sp,
                    color = MutedText,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                OutlinedTextField(
                    value = limitInput,
                    onValueChange = { input ->
                        if (input.all { it.isDigit() } && input.length <= 5) {
                            limitInput = input
                        }
                    },
                    modifier = Modifier.fillMaxWidth().testTag("inp_limit_dialog"),
                    label = { Text("Калории (ккал)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentBlue,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = FieldBg,
                        unfocusedContainerColor = FieldBg,
                        cursorColor = AccentBlue,
                        focusedLabelColor = AccentBlue,
                        unfocusedLabelColor = MutedText,
                        focusedTextColor = CharcoalText,
                        unfocusedTextColor = CharcoalText
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val limitNum = limitInput.toIntOrNull()
                    if (limitNum != null && limitNum > 100) {
                        onSave(limitNum)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue, contentColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("btn_save_limit")
            ) {
                Text("Сохранить", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена", color = MutedText, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(26.dp)
    )
}

// Generate calendar days Matrix logic
fun getCalendarDays(year: Int, month: Int): List<CalendarDay> {
    val list = mutableListOf<CalendarDay>()

    val cal = Calendar.getInstance()
    cal.set(Calendar.YEAR, year)
    cal.set(Calendar.MONTH, month)
    cal.set(Calendar.DAY_OF_MONTH, 1)

    // Monday as 1st day of week for Russia (Calendar MONDAY = 2, Calendar SUNDAY = 1)
    val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
    
    // Shift: Monday=0, Tuesday=1 ... Sunday=6
    val prefixEmptyCells = (firstDayOfWeek - Calendar.MONDAY + 7) % 7

    val prevCal = cal.clone() as Calendar
    prevCal.add(Calendar.MONTH, -1)
    val maxDaysPrev = prevCal.getActualMaximum(Calendar.DAY_OF_MONTH)
    
    for (i in (maxDaysPrev - prefixEmptyCells + 1)..maxDaysPrev) {
        prevCal.set(Calendar.DAY_OF_MONTH, i)
        list.add(
            CalendarDay(
                dateStr = getFormattedDateForEngine(prevCal.time),
                dayOfMonth = i,
                isCurrentMonth = false,
                isToday = isSameDay(prevCal, Calendar.getInstance())
            )
        )
    }

    val maxDaysCur = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    for (i in 1..maxDaysCur) {
        cal.set(Calendar.DAY_OF_MONTH, i)
        list.add(
            CalendarDay(
                dateStr = getFormattedDateForEngine(cal.time),
                dayOfMonth = i,
                isCurrentMonth = true,
                isToday = isSameDay(cal, Calendar.getInstance())
            )
        )
    }

    val totalCells = list.size
    val remaining = (7 - (totalCells % 7)) % 7
    val nextCal = cal.clone() as Calendar
    nextCal.add(Calendar.MONTH, 1)
    for (i in 1..remaining) {
        nextCal.set(Calendar.DAY_OF_MONTH, i)
        list.add(
            CalendarDay(
                dateStr = getFormattedDateForEngine(nextCal.time),
                dayOfMonth = i,
                isCurrentMonth = false,
                isToday = isSameDay(nextCal, Calendar.getInstance())
            )
        )
    }

    return list
}

private fun getFormattedDateForEngine(date: Date): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    return formatter.format(date)
}

private fun isSameDay(c1: Calendar, c2: Calendar): Boolean {
    return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
           c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)
}

private fun getDayOfWeekFromDateStr(dateStr: String): Int {
    return try {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val date = formatter.parse(dateStr) ?: return -1
        val cal = Calendar.getInstance()
        cal.time = date
        val day = cal.get(Calendar.DAY_OF_WEEK)
        if (day == Calendar.SUNDAY) 6 else day - 2
    } catch (e: Exception) {
        -1
    }
}

data class CalendarDay(
    val dateStr: String,
    val dayOfMonth: Int,
    val isCurrentMonth: Boolean,
    val isToday: Boolean
)

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun WorkoutsScreen(viewModel: TrackerViewModel) {
    // Collect states
    val exercises by viewModel.exercises.collectAsStateWithLifecycle()
    val trainingPlans by viewModel.trainingPlans.collectAsStateWithLifecycle()
    val activePlan by viewModel.activeTrainingPlan.collectAsStateWithLifecycle()
    val planAssignments by viewModel.planAssignments.collectAsStateWithLifecycle()
    val weightEntries by viewModel.weightEntries.collectAsStateWithLifecycle()

    var subTab by remember { mutableStateOf(0) } // 0 -> База, 1 -> Конструктор
    var editingPlanId by remember { mutableStateOf<Int?>(null) }

    // Synchronize default editing plan
    LaunchedEffect(activePlan, trainingPlans) {
        if (editingPlanId == null) {
            editingPlanId = activePlan?.id ?: trainingPlans.firstOrNull()?.id
        }
    }

    val latestWeight = remember(weightEntries) {
        weightEntries.sortedByDescending { it.date + it.timestamp }.firstOrNull()?.weight ?: 70.0
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Title Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "СПОРТИВНЫЙ РАЗДЕЛ",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MutedText,
                    letterSpacing = 1.2.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Тренировки",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = DeepNavy,
                    letterSpacing = (-0.5).sp
                )
            }
        }

        // Subtab Pill Switcher - Flat & Beautiful
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(FieldBg)
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (subTab == 0) AccentBlue else Color.Transparent)
                    .clickable { subTab = 0 }
                    .padding(vertical = 10.dp)
                    .testTag("subtab_db"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "База упражнений",
                    color = if (subTab == 0) Color.White else CharcoalText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (subTab == 1) AccentBlue else Color.Transparent)
                    .clickable { subTab = 1 }
                    .padding(vertical = 10.dp)
                    .testTag("subtab_creator"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "План тренировок",
                    color = if (subTab == 1) Color.White else CharcoalText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }

        if (subTab == 0) {
            ExerciseDatabaseSection(
                viewModel = viewModel,
                exercises = exercises,
                activePlan = activePlan,
                planAssignments = planAssignments,
                latestWeight = latestWeight
            )
        } else {
            TrainingPlanCreatorSection(
                viewModel = viewModel,
                exercises = exercises,
                trainingPlans = trainingPlans,
                activePlanId = activePlan?.id,
                planAssignments = planAssignments,
                editingPlanId = editingPlanId,
                onSelectEditingPlan = { editingPlanId = it }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseDatabaseSection(
    viewModel: TrackerViewModel,
    exercises: List<com.example.data.Exercise>,
    activePlan: com.example.data.TrainingPlan?,
    planAssignments: List<com.example.data.PlanWorkoutDayExercise>,
    latestWeight: Double
) {
    var showAddDialog by remember { mutableStateOf(false) }

    var p_id by remember { mutableStateOf<Int?>(null) }
    var name by remember { mutableStateOf("") }
    var isBodyweight by remember { mutableStateOf(false) }
    var weightInput by remember { mutableStateOf("") }
    var setsInput by remember { mutableStateOf("4") }
    var repsInput by remember { mutableStateOf("12") }
    var errorText by remember { mutableStateOf("") }

    val onEditClick = { exercise: com.example.data.Exercise ->
        p_id = exercise.id
        name = exercise.name
        isBodyweight = exercise.isBodyweight
        weightInput = exercise.workingWeight.toString()
        setsInput = exercise.sets.toString()
        repsInput = exercise.repetitions.toString()
        errorText = ""
        showAddDialog = true
    }

    val onCreateClick = {
        p_id = null
        name = ""
        isBodyweight = false
        weightInput = ""
        setsInput = "4"
        repsInput = "12"
        errorText = ""
        showAddDialog = true
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "МОИ УПРАЖНЕНИЯ (${exercises.size})",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MutedText,
                letterSpacing = 0.5.sp
            )
            Button(
                onClick = { onCreateClick() },
                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue, contentColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                modifier = Modifier.testTag("btn_new_exercise")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Новое упражнение", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (exercises.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🏋️", fontSize = 36.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "В базе пока нет упражнений.\nНажмите кнопку выше, чтобы добавить!",
                        color = MutedText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(exercises) { exercise ->
                    // Calculate assigned days text
                    val assignedDays = remember(activePlan, planAssignments, exercise) {
                        if (activePlan == null) emptyList()
                        else {
                            planAssignments
                                .filter { it.planId == activePlan.id && it.exerciseId == exercise.id }
                                .map { it.dayOfWeek }
                                .sorted()
                        }
                    }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, DividerColor),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onEditClick(exercise) }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = exercise.name,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    color = DeepNavy
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "${exercise.sets} подх. \u2022 ${exercise.repetitions} повт.",
                                        fontSize = 13.sp,
                                        color = CharcoalText,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(FieldBg)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = if (exercise.isBodyweight) "Вес тела" else "${exercise.workingWeight} кг",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = AccentBlue
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Dynamic plan indicator badge
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    val daysAbbr = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")
                                    if (assignedDays.isEmpty()) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(BorderGray.copy(alpha = 0.3f))
                                                .padding(horizontal = 8.dp, vertical = 3.dp)
                                        ) {
                                            Text(
                                                text = "Не используется",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MutedText
                                            )
                                        }
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(LightBlueBg)
                                                .padding(horizontal = 8.dp, vertical = 3.dp)
                                        ) {
                                            val dayString = assignedDays.joinToString(", ") { daysAbbr[it] }
                                            Text(
                                                text = "В плане: $dayString",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = OnLightBlue
                                            )
                                        }
                                    }
                                }
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = { onEditClick(exercise) },
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(LightBlueBg)
                                        .size(36.dp)
                                        .testTag("btn_edit_exercise_${exercise.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Редактировать",
                                        tint = AccentBlue,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                IconButton(
                                    onClick = { viewModel.deleteExercise(exercise.id) },
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(SoftRose)
                                        .size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Удалить",
                                        tint = WarningRed,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        // Automatically fill user's weight if bodyweight toggle is enabled
        LaunchedEffect(isBodyweight) {
            if (isBodyweight) {
                weightInput = latestWeight.toString()
            }
        }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = {
                Text(
                    text = if (p_id != null) "Редактировать упражнение" else "Новое упражнение",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = DeepNavy
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        textStyle = TextStyle(color = Color.Black, fontSize = 14.sp),
                        label = { Text("Название упражнения") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedPlaceholderColor = Color.DarkGray,
                            unfocusedPlaceholderColor = Color.DarkGray,
                            focusedLabelColor = AccentBlue,
                            unfocusedLabelColor = Color.Gray,
                            focusedBorderColor = AccentBlue,
                            unfocusedContainerColor = FieldBg,
                            focusedContainerColor = FieldBg
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("inp_ex_name")
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isBodyweight = !isBodyweight }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isBodyweight,
                            onCheckedChange = { isBodyweight = it },
                            colors = CheckboxDefaults.colors(checkedColor = AccentBlue),
                            modifier = Modifier.testTag("chk_ex_bodyweight")
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Упражнение с собственным весом",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalText
                        )
                    }

                    OutlinedTextField(
                        value = weightInput,
                        onValueChange = { input ->
                            if (!isBodyweight) {
                                if (input.isEmpty() || input.toDoubleOrNull() != null || input.all { it.isDigit() || it == '.' }) {
                                    weightInput = input
                                }
                            }
                        },
                        textStyle = TextStyle(color = Color.Black, fontSize = 14.sp),
                        label = { Text("Рабочий вес (кг)") },
                        enabled = !isBodyweight,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedPlaceholderColor = Color.DarkGray,
                            unfocusedPlaceholderColor = Color.DarkGray,
                            focusedLabelColor = AccentBlue,
                            unfocusedLabelColor = Color.Gray,
                            focusedBorderColor = AccentBlue,
                            unfocusedContainerColor = if (isBodyweight) BorderGray.copy(alpha = 0.2f) else FieldBg,
                            focusedContainerColor = FieldBg,
                            disabledContainerColor = BorderGray.copy(alpha = 0.15f),
                            disabledTextColor = MutedText
                        ),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("inp_ex_weight")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = setsInput,
                            onValueChange = { setsInput = it },
                            textStyle = TextStyle(color = Color.Black, fontSize = 14.sp),
                            label = { Text("Подходы") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                focusedPlaceholderColor = Color.DarkGray,
                                unfocusedPlaceholderColor = Color.DarkGray,
                                focusedLabelColor = AccentBlue,
                                unfocusedLabelColor = Color.Gray,
                                focusedBorderColor = AccentBlue,
                                unfocusedContainerColor = FieldBg,
                                focusedContainerColor = FieldBg
                            ),
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f).testTag("inp_ex_sets")
                        )
                        OutlinedTextField(
                            value = repsInput,
                            onValueChange = { repsInput = it },
                            textStyle = TextStyle(color = Color.Black, fontSize = 14.sp),
                            label = { Text("Повторения") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                focusedPlaceholderColor = Color.DarkGray,
                                unfocusedPlaceholderColor = Color.DarkGray,
                                focusedLabelColor = AccentBlue,
                                unfocusedLabelColor = Color.Gray,
                                focusedBorderColor = AccentBlue,
                                unfocusedContainerColor = FieldBg,
                                focusedContainerColor = FieldBg
                            ),
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f).testTag("inp_ex_reps")
                        )
                    }

                    if (errorText.isNotEmpty()) {
                        Text(
                            text = errorText,
                            color = WarningRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isBlank()) {
                            errorText = "Заполните название"
                            return@Button
                        }
                        val weight = weightInput.toDoubleOrNull() ?: 0.0
                        val sets = setsInput.toIntOrNull() ?: 4
                        val reps = repsInput.toIntOrNull() ?: 12
                        
                        if (p_id != null) {
                            viewModel.updateExercise(p_id!!, name, weight, isBodyweight, sets, reps)
                        } else {
                            viewModel.addExercise(name, weight, isBodyweight, sets, reps)
                        }
                        showAddDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(if (p_id != null) "Сохранить" else "Создать", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Отмена", fontWeight = FontWeight.Bold, color = MutedText)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(26.dp)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TrainingPlanCreatorSection(
    viewModel: TrackerViewModel,
    exercises: List<com.example.data.Exercise>,
    trainingPlans: List<com.example.data.TrainingPlan>,
    activePlanId: Int?,
    planAssignments: List<com.example.data.PlanWorkoutDayExercise>,
    editingPlanId: Int?,
    onSelectEditingPlan: (Int) -> Unit
) {
    var showCreatePlanDialog by remember { mutableStateOf(false) }
    var renamingPlan by remember { mutableStateOf<com.example.data.TrainingPlan?>(null) }
    var selectedDayIndex by remember { mutableStateOf(0) } // 0 = Mon, 6 = Sun

    // Drag and drop state coordinators
    var parentContainerCoords: LayoutCoordinates? by remember { mutableStateOf(null) }
    val dayBounds = remember { mutableStateMapOf<Int, androidx.compose.ui.geometry.Rect>() }
    val exerciseCoords = remember { mutableStateMapOf<Int, LayoutCoordinates>() }

    var draggingExerciseId by remember { mutableStateOf<Int?>(null) }
    var draggingExercise by remember { mutableStateOf<com.example.data.Exercise?>(null) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    var dragStartOffset by remember { mutableStateOf(Offset.Zero) }
    var hoveredDayIndex by remember { mutableStateOf<Int?>(null) }

    // Backup tap assignment click state
    var quickAssignExercise by remember { mutableStateOf<com.example.data.Exercise?>(null) }

    val scrollState = rememberScrollState()
    val duplicateWarningExerciseName by viewModel.duplicateWarningExerciseName.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned { parentContainerCoords = it }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Plan Selector Row
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                     text = "ПЛАНЫ ТРЕНИРОВОК",
                     fontSize = 11.sp,
                     fontWeight = FontWeight.Bold,
                     color = MutedText,
                     letterSpacing = 0.5.sp
                )
                Button(
                    onClick = { showCreatePlanDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentBlue, contentColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                    modifier = Modifier.testTag("btn_new_plan")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Новый план", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }

            if (trainingPlans.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(160.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "У вас пока нет планов тренировок.\nСоздайте первый план, чтобы назначить упражнения!",
                        color = MutedText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                // Plans List
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, DividerColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Выберите план для редактирования и расписания:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MutedText,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        trainingPlans.forEach { plan ->
                            val isEditing = plan.id == editingPlanId
                            val isActive = plan.id == activePlanId

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSelectEditingPlan(plan.id) }
                                    .background(
                                        if (isEditing) LightBlueBg.copy(alpha = 0.4f) else Color.Transparent,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    RadioButton(
                                        selected = isEditing,
                                        onClick = { onSelectEditingPlan(plan.id) },
                                        colors = RadioButtonDefaults.colors(selectedColor = AccentBlue)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = plan.name,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DeepNavy
                                    )
                                    if (isActive) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(AccentBlue)
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "Активен",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                    if (!isActive) {
                                        TextButton(
                                            onClick = { viewModel.setPlanActive(plan.id) },
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Text("Активировать", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AccentBlue)
                                        }
                                    }
                                    IconButton(
                                        onClick = { renamingPlan = plan },
                                        modifier = Modifier.size(30.dp).testTag("btn_rename_plan_${plan.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Переименовать план",
                                            tint = AccentBlue,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                    IconButton(
                                        onClick = { viewModel.deleteTrainingPlan(plan.id) },
                                        modifier = Modifier.size(30.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Удалить план",
                                            tint = WarningRed,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Plan Builder Interface
            if (editingPlanId != null) {
                val plan = trainingPlans.firstOrNull { it.id == editingPlanId }
                if (plan != null) {
                    val daysAbbr = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")
                    val daysFull = listOf("Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье")

                    Text(
                        text = "КОНСТРУКТОР ПЛАНА: ${plan.name.uppercase(Locale.getDefault())}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MutedText,
                        letterSpacing = 0.5.sp,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )

                    Text(
                        text = "Перетащите упр-е на день недели в верхнем ряду, либо нажмите кнопку '+' на карточке.",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MutedText,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )

                    // 1. TOP ROW OF WEEK DAYS (DROP TARGETS)
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        for (dayIdx in 0..6) {
                            val count = planAssignments.count { it.planId == plan.id && it.dayOfWeek == dayIdx }
                            val isSelected = selectedDayIndex == dayIdx
                            val isHovered = hoveredDayIndex == dayIdx

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .onGloballyPositioned { layoutCoords ->
                                        val parentCoords = parentContainerCoords
                                        if (parentCoords != null && parentCoords.isAttached && layoutCoords.isAttached) {
                                            dayBounds[dayIdx] = parentCoords.localBoundingBoxOf(layoutCoords)
                                        }
                                    }
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        when {
                                            isHovered -> AccentBlue.copy(alpha = 0.25f)
                                            isSelected -> AccentBlue
                                            count > 0 -> LightBlueBg.copy(alpha = 0.6f)
                                            else -> Color.White
                                        }
                                    )
                                    .border(
                                        BorderStroke(
                                            width = if (isHovered) 2.dp else 1.dp,
                                            color = when {
                                                isHovered -> AccentBlue
                                                isSelected -> AccentBlue
                                                count > 0 -> AccentBlue.copy(alpha = 0.4f)
                                                else -> DividerColor
                                            }
                                        ),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable { selectedDayIndex = dayIdx }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = daysAbbr[dayIdx],
                                        fontWeight = FontWeight.Black,
                                        fontSize = 13.sp,
                                        color = if (isSelected) Color.White else DeepNavy
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "$count упр.",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        color = if (isSelected) Color.White.copy(alpha = 0.8f) else MutedText
                                    )
                                }
                            }
                        }
                    }

                    // 2. MIDDLE VIEW: ASSIGNED EXERCISES FOR ACTIVE SELECTED DAY OF WEEK
                    val dayExercises = remember(plan, planAssignments, exercises, selectedDayIndex) {
                        val assigned = planAssignments
                            .filter { it.planId == plan.id && it.dayOfWeek == selectedDayIndex }
                            .sortedBy { it.displayOrder }
                        assigned.mapNotNull { assignment ->
                            exercises.find { it.id == assignment.exerciseId }
                        }
                    }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, DividerColor),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                        modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp).fillMaxWidth()) {
                            Text(
                                text = "РАСПИСАНИЕ НА ${daysFull[selectedDayIndex].uppercase()}:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MutedText,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            if (dayExercises.isEmpty()) {
                                Box(
                                    modifier = Modifier.fillMaxWidth().height(80.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "На этот день упражнений нет.\nДобавьте их из пула уп-ний ниже!",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MutedText,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            } else {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    dayExercises.forEachIndexed { index, exercise ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(FieldBg, RoundedCornerShape(10.dp))
                                                .padding(horizontal = 12.dp, vertical = 6.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(
                                                    text = exercise.name,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp,
                                                    color = DeepNavy
                                                )
                                                val weightLabel = if (exercise.isBodyweight) "Вес тела" else "${exercise.workingWeight} кг"
                                                Text(
                                                    text = "${exercise.sets} подх. \u2022 ${exercise.repetitions} повт. \u2022 $weightLabel",
                                                    fontSize = 11.sp,
                                                    color = MutedText,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                if (index > 0) {
                                                    IconButton(
                                                        onClick = {
                                                            val updatedList = dayExercises.toMutableList()
                                                            val temp = updatedList[index]
                                                            updatedList[index] = updatedList[index - 1]
                                                            updatedList[index - 1] = temp
                                                            viewModel.updateAssignmentsOrder(plan.id, selectedDayIndex, updatedList)
                                                        },
                                                        modifier = Modifier.size(24.dp)
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.KeyboardArrowUp,
                                                            contentDescription = "Вверх",
                                                            tint = AccentBlue,
                                                            modifier = Modifier.size(18.dp)
                                                        )
                                                    }
                                                }
                                                if (index < dayExercises.size - 1) {
                                                    IconButton(
                                                        onClick = {
                                                            val updatedList = dayExercises.toMutableList()
                                                            val temp = updatedList[index]
                                                            updatedList[index] = updatedList[index + 1]
                                                            updatedList[index + 1] = temp
                                                            viewModel.updateAssignmentsOrder(plan.id, selectedDayIndex, updatedList)
                                                        },
                                                        modifier = Modifier.size(24.dp)
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.KeyboardArrowDown,
                                                            contentDescription = "Вниз",
                                                            tint = AccentBlue,
                                                            modifier = Modifier.size(18.dp)
                                                        )
                                                    }
                                                }
                                                IconButton(
                                                    onClick = {
                                                        viewModel.removeExerciseFromDay(plan.id, selectedDayIndex, exercise.id)
                                                    },
                                                    modifier = Modifier.size(24.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Close,
                                                        contentDescription = "Убрать",
                                                        tint = WarningRed,
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 3. BOTTOM SECTION: EXERCISE POOL (STATIC)
                    Text(
                        text = "ВСЕ УПРАЖНЕНИЯ (НАЖМИТЕ '+', ЧТОБЫ ДОБАВИТЬ):",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MutedText,
                        letterSpacing = 0.5.sp,
                        modifier = Modifier.padding(top = 12.dp, bottom = 6.dp)
                    )

                    if (exercises.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(100.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Сначала добавьте упражнения на вкладке\n'База упражнений'!",
                                color = MutedText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            exercises.forEach { exercise ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    shape = RoundedCornerShape(14.dp),
                                    border = BorderStroke(1.dp, DividerColor),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("exercise_static_card_${exercise.id}")
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = exercise.name,
                                                fontWeight = FontWeight.Black,
                                                fontSize = 14.sp,
                                                color = DeepNavy
                                            )
                                            val weightLabel = if (exercise.isBodyweight) "Вес тела" else "${exercise.workingWeight} кг"
                                            Text(
                                                text = "${exercise.sets} подх. \u2022 ${exercise.repetitions} повт. \u2022 $weightLabel",
                                                color = MutedText,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        // Backup Tap helper addition
                                        IconButton(
                                            onClick = { quickAssignExercise = exercise },
                                            modifier = Modifier
                                                .clip(CircleShape)
                                                .background(FieldBg)
                                                .size(36.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Add,
                                                contentDescription = "Добавить на день",
                                                tint = AccentBlue,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Draggable floating feedback preview overlay
        if (draggingExercise != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(2.dp, AccentBlue),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .size(width = 160.dp, height = 75.dp)
                    .offset {
                        IntOffset(
                            (dragOffset.x - 80.dp.toPx()).roundToInt(),
                            (dragOffset.y - 37.dp.toPx()).roundToInt()
                        )
                    }
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = draggingExercise!!.name,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        maxLines = 1,
                        color = DeepNavy
                    )
                    Text(
                        text = "${draggingExercise!!.sets}x${draggingExercise!!.repetitions}",
                        color = MutedText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Backup Tap dialog assignment
        if (quickAssignExercise != null && editingPlanId != null) {
            val daysAbbr = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")

            AlertDialog(
                onDismissRequest = { quickAssignExercise = null },
                title = {
                    Text(
                        text = "Добавить в расписание",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = DeepNavy
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Выберите день недели для упражнения '${quickAssignExercise!!.name}':",
                            fontSize = 13.sp,
                            color = CharcoalText,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            for (dayIdx in 0..6) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(LightBlueBg)
                                        .clickable {
                                            viewModel.assignExerciseToDay(editingPlanId, dayIdx, quickAssignExercise!!.id)
                                            quickAssignExercise = null
                                        }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = daysAbbr[dayIdx],
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = OnLightBlue
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = { quickAssignExercise = null }) {
                        Text("Закрыть", fontWeight = FontWeight.Bold, color = MutedText)
                    }
                },
                containerColor = Color.White,
                shape = RoundedCornerShape(26.dp)
            )
        }

        // Plan Creation Input Dialog
        if (showCreatePlanDialog) {
            var planName by remember { mutableStateOf("") }
            var planError by remember { mutableStateOf("") }

            AlertDialog(
                onDismissRequest = { showCreatePlanDialog = false },
                title = {
                    Text(
                        text = "Создать план тренировок",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = DeepNavy
                    )
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = planName,
                            onValueChange = { planName = it },
                            textStyle = TextStyle(color = Color.Black, fontSize = 14.sp),
                            label = { Text("Название плана") },
                            placeholder = { Text("например, Сплит Пн/Ср/Пт") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                focusedPlaceholderColor = Color.DarkGray,
                                unfocusedPlaceholderColor = Color.DarkGray,
                                focusedLabelColor = AccentBlue,
                                unfocusedLabelColor = Color.Gray,
                                focusedBorderColor = AccentBlue,
                                unfocusedContainerColor = FieldBg,
                                focusedContainerColor = FieldBg
                            ),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("inp_plan_name")
                        )
                        if (planError.isNotEmpty()) {
                            Text(
                                text = planError,
                                color = WarningRed,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (planName.isBlank()) {
                                planError = "Заполните название"
                                return@Button
                            }
                            viewModel.addTrainingPlan(planName)
                            showCreatePlanDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Создать", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCreatePlanDialog = false }) {
                        Text("Отмена", fontWeight = FontWeight.Bold, color = MutedText)
                    }
                },
                containerColor = Color.White,
                shape = RoundedCornerShape(26.dp)
            )
        }

        // Plan Rename Input Dialog
        if (renamingPlan != null) {
            var planNewName by remember { mutableStateOf(renamingPlan!!.name) }
            var planRenameError by remember { mutableStateOf("") }

            AlertDialog(
                onDismissRequest = { renamingPlan = null },
                title = {
                    Text(
                        text = "Переименовать план",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = DeepNavy
                    )
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = planNewName,
                            onValueChange = { planNewName = it },
                            textStyle = TextStyle(color = Color.Black, fontSize = 14.sp),
                            label = { Text("Новое название плана") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                focusedLabelColor = AccentBlue,
                                unfocusedLabelColor = Color.Gray,
                                focusedBorderColor = AccentBlue,
                                unfocusedContainerColor = FieldBg,
                                focusedContainerColor = FieldBg
                            ),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("inp_rename_plan_name")
                        )
                        if (planRenameError.isNotEmpty()) {
                            Text(
                                text = planRenameError,
                                color = WarningRed,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (planNewName.isBlank()) {
                                planRenameError = "Заполните название"
                                return@Button
                            }
                            viewModel.renameTrainingPlan(renamingPlan!!.id, planNewName)
                            renamingPlan = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Сохранить", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { renamingPlan = null }) {
                        Text("Отмена", fontWeight = FontWeight.Bold, color = MutedText)
                    }
                },
                containerColor = Color.White,
                shape = RoundedCornerShape(26.dp)
            )
        }

        // Duplicate Exercise Warning Dialog
        if (duplicateWarningExerciseName != null) {
            AlertDialog(
                onDismissRequest = { viewModel.clearDuplicateWarning() },
                title = {
                    Text(
                        text = "Упражнение уже добавлено",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = WarningRed
                    )
                },
                text = {
                    Text(
                        text = "Упражнение \"${duplicateWarningExerciseName}\" уже добавлено в расписание на этот день. Повторное добавление в один и тот же день не разрешено.",
                        fontSize = 14.sp,
                        color = CharcoalText,
                        fontWeight = FontWeight.Medium
                    )
                },
                confirmButton = {
                    Button(
                        onClick = { viewModel.clearDuplicateWarning() },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("ОК", fontWeight = FontWeight.Bold)
                    }
                },
                containerColor = Color.White,
                shape = RoundedCornerShape(26.dp)
            )
        }
    }
}
