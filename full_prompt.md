You are a senior Android developer. I need you to scaffold a complete, production-ready 
Native Android application from scratch inside this folder. The folder already contains 
these 4 files at the root level:

- Model_V5.tflite        (TensorFlow Lite crop disease model)
- class_names.json       (38 class label names as a JSON array)
- labels.txt             (38 class labels, one per line)
- MainActivity.java      (ignore this file, do not use it)

YOUR FIRST TASK — PROJECT SCAFFOLDING:
Create a complete Android Studio-compatible project with this exact package name:
  com.cropdoctor.app

Use this folder/file structure:
The root project folder name is CINQUE1.

  app/
    src/
      main/
        assets/
          (copy Model_V5.tflite, class_names.json, labels.txt here)
        java/com/cropdoctor/app/
          data/
            ClassNameRepository.kt
          model/
            TFLiteInferenceHelper.kt
            InferenceResult.kt
          ui/
            theme/
              Theme.kt
              Color.kt
              Type.kt
          screens/
            SplashScreen.kt
            HomeScreen.kt
            PreviewScreen.kt
            ResultScreen.kt
          utils/
            BitmapUtils.kt
            PermissionUtils.kt
          viewmodel/
            CropDiseaseViewModel.kt
          MainActivity.kt
          CropDoctorApp.kt
        res/
          drawable/
            ic_launcher_background.xml
            ic_camera.xml
            ic_gallery.xml
            ic_leaf.xml
          mipmap-hdpi/
          mipmap-mdpi/
          mipmap-xhdpi/
          mipmap-xxhdpi/
          mipmap-xxxhdpi/
          values/
            strings.xml
            colors.xml
            themes.xml
          values-night/
            themes.xml
        AndroidManifest.xml
    build.gradle   (app-level)
  build.gradle     (project-level)
  gradle.properties
  settings.gradle
  local.properties  (with sdk.dir placeholder)
  gradlew
  gradlew.bat
  gradle/
    wrapper/
      gradle-wrapper.properties

---

STEP 1 — Copy asset files:
Copy Model_V5.tflite, class_names.json, and labels.txt from the project root into:
  app/src/main/assets/

---

STEP 2 — settings.gradle (project root):

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "CropDoctor"
include ':app'

---

STEP 3 — build.gradle (project-level root):

buildscript {
    ext {
        compose_version = '1.5.4'
        kotlin_version = '1.9.10'
    }
}
plugins {
    id 'com.android.application' version '8.1.2' apply false
    id 'com.android.library' version '8.1.2' apply false
    id 'org.jetbrains.kotlin.android' version '1.9.10' apply false
}

---

STEP 4 — app/build.gradle:

plugins {
    id 'com.android.application'
    id 'org.jetbrains.kotlin.android'
}

android {
    namespace 'com.cropdoctor.app'
    compileSdk 34

    defaultConfig {
        applicationId "com.cropdoctor.app"
        minSdk 24
        targetSdk 34
        versionCode 1
        versionName "1.0"
        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary true
        }
    }

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }

    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = '1.8'
    }

    buildFeatures {
        compose true
    }

    composeOptions {
        kotlinCompilerExtensionVersion '1.5.3'
    }

    packagingOptions {
        resources {
            excludes += '/META-INF/{AL2.0,LGPL2.1}'
        }
    }

    aaptOptions {
        noCompress "tflite"
    }
}

dependencies {
    // Core Android
    implementation 'androidx.core:core-ktx:1.12.0'
    implementation 'androidx.lifecycle:lifecycle-runtime-ktx:2.7.0'
    implementation 'androidx.activity:activity-compose:1.8.2'

    // Jetpack Compose BOM
    implementation platform('androidx.compose:compose-bom:2023.10.01')
    implementation 'androidx.compose.ui:ui'
    implementation 'androidx.compose.ui:ui-graphics'
    implementation 'androidx.compose.ui:ui-tooling-preview'
    implementation 'androidx.compose.material3:material3'
    implementation 'androidx.compose.material:material-icons-extended'
    implementation 'androidx.compose.animation:animation'

    // Navigation
    implementation 'androidx.navigation:navigation-compose:2.7.5'

    // ViewModel
    implementation 'androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0'
    implementation 'androidx.lifecycle:lifecycle-runtime-compose:2.7.0'

    // TensorFlow Lite
    implementation 'org.tensorflow:tensorflow-lite:2.13.0'
    implementation 'org.tensorflow:tensorflow-lite-support:0.4.4'
    implementation 'org.tensorflow:tensorflow-lite-metadata:0.4.4'
    implementation 'org.tensorflow:tensorflow-lite-gpu:2.13.0'

    // Coil for image loading
    implementation 'io.coil-kt:coil-compose:2.5.0'

    // Coroutines
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3'

    // JSON parsing
    implementation 'org.json:json:20231013'

    // Accompanist Permissions
    implementation 'com.google.accompanist:accompanist-permissions:0.32.0'

    // Testing
    testImplementation 'junit:junit:4.13.2'
    androidTestImplementation 'androidx.test.ext:junit:1.1.5'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.5.1'
    androidTestImplementation platform('androidx.compose:compose-bom:2023.10.01')
    androidTestImplementation 'androidx.compose.ui:ui-test-junit4'
    debugImplementation 'androidx.compose.ui:ui-tooling'
    debugImplementation 'androidx.compose.ui:ui-test-manifest'
}

---

STEP 5 — gradle/wrapper/gradle-wrapper.properties:

distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-8.0-bin.zip
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists

---

STEP 6 — AndroidManifest.xml:

<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <!-- Permissions -->
    <uses-permission android:name="android.permission.CAMERA" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"
        android:maxSdkVersion="32"/>
    <uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
        android:maxSdkVersion="28"/>

    <uses-feature android:name="android.hardware.camera" android:required="false"/>

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.CropDoctor">

        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:theme="@style/Theme.CropDoctor"
            android:windowSoftInputMode="adjustResize">
            <intent-filter>
                <action android:name="android.intent.action.MAIN"/>
                <category android:name="android.intent.category.LAUNCHER"/>
            </intent-filter>
        </activity>

        <!-- FileProvider for camera capture URI -->
        <provider
            android:name="androidx.core.content.FileProvider"
            android:authorities="${applicationId}.fileprovider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/file_paths"/>
        </provider>

    </application>
</manifest>

---

STEP 7 — Create res/xml/file_paths.xml:

<?xml version="1.0" encoding="utf-8"?>
<paths>
    <external-files-path name="my_images" path="Pictures/"/>
    <cache-path name="cached_images" path="images/"/>
</paths>

---

STEP 8 — res/values/strings.xml:

<resources>
    <string name="app_name">CropDoctor</string>
    <string name="subtitle">Crop Disease Detection</string>
    <string name="click_picture">Click Picture</string>
    <string name="upload_gallery">Upload from Gallery</string>
    <string name="detect_disease">Detect Disease?</string>
    <string name="yes">Yes, Detect</string>
    <string name="cancel">Cancel</string>
    <string name="try_another">Try Another Image</string>
    <string name="loading">Analyzing crop...</string>
    <string name="confidence">Confidence</string>
</resources>

---

STEP 9 — res/values/themes.xml and res/values-night/themes.xml:

<!-- values/themes.xml -->
<resources>
    <style name="Theme.CropDoctor" parent="android:Theme.Material.Light.NoActionBar"/>
</resources>

<!-- values-night/themes.xml -->
<resources>
    <style name="Theme.CropDoctor" parent="android:Theme.Material.NoActionBar"/>
</resources>

---

STEP 10 — Now write all Kotlin source files:

--- data/ClassNameRepository.kt ---
/*
 * Reads class_names.json from assets and provides label lookup.
 */
package com.cropdoctor.app.data

import android.content.Context
import org.json.JSONArray

class ClassNameRepository(private val context: Context) {

    // Lazy-loaded list of class name strings from assets/class_names.json
    val classNames: List<String> by lazy {
        loadClassNames()
    }

    private fun loadClassNames(): List<String> {
        return try {
            val jsonString = context.assets.open("class_names.json")
                .bufferedReader()
                .use { it.readText() }
            val jsonArray = JSONArray(jsonString)
            List(jsonArray.length()) { i -> jsonArray.getString(i) }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Formats a raw label like "Tomato___Early_blight" into
     * "Tomato — Early Blight" for display.
     */
    fun formatLabel(raw: String): Pair<String, String> {
        val parts = raw.split("___")
        val crop = parts.getOrElse(0) { raw }.replace("_", " ").trim()
        val disease = parts.getOrElse(1) { "" }.replace("_", " ").trim()
        return Pair(crop, disease)
    }
}

--- model/InferenceResult.kt ---
/*
 * Data class holding the result of a single TFLite inference pass.
 */
package com.cropdoctor.app.model

data class InferenceResult(
    val rawLabel: String,       // e.g. "Tomato___Early_blight"
    val cropName: String,       // e.g. "Tomato"
    val diseaseName: String,    // e.g. "Early Blight"
    val confidence: Float,      // 0.0f to 1.0f
    val isHealthy: Boolean      // true if label contains "healthy"
)

--- model/TFLiteInferenceHelper.kt ---
/*
 * Loads Model_V5.tflite from assets, preprocesses a Bitmap,
 * runs inference, and returns a ranked InferenceResult.
 */
package com.cropdoctor.app.model

import android.content.Context
import android.graphics.Bitmap
import com.cropdoctor.app.data.ClassNameRepository
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class TFLiteInferenceHelper(
    private val context: Context,
    private val repository: ClassNameRepository
) {

    // TFLite model input image size (must match model training config)
    private val IMAGE_SIZE = 224
    // Number of channels: RGB
    private val CHANNELS = 3
    // Bytes per float
    private val BYTES_PER_FLOAT = 4

    private var interpreter: Interpreter? = null

    /**
     * Loads the TFLite model from assets into a MappedByteBuffer.
     * Call this once before running inference.
     */
    fun loadModel() {
        val options = Interpreter.Options().apply {
            numThreads = 4
        }
        interpreter = Interpreter(loadModelFile(), options)
    }

    private fun loadModelFile(): MappedByteBuffer {
        val assetFileDescriptor = context.assets.openFd("Model_V5.tflite")
        val fileInputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel = fileInputStream.channel
        val startOffset = assetFileDescriptor.startOffset
        val declaredLength = assetFileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    /**
     * Runs inference on a given Bitmap.
     * @param bitmap The input image (will be resized internally)
     * @return InferenceResult with top prediction and confidence
     */
    fun classify(bitmap: Bitmap): InferenceResult {
        val interpreter = interpreter
            ?: throw IllegalStateException("Model not loaded. Call loadModel() first.")

        // Resize bitmap to model input size
        val resized = Bitmap.createScaledBitmap(bitmap, IMAGE_SIZE, IMAGE_SIZE, true)

        // Prepare input ByteBuffer: shape [1, 224, 224, 3], normalized to [0,1]
        val inputBuffer = ByteBuffer.allocateDirect(
            1 * IMAGE_SIZE * IMAGE_SIZE * CHANNELS * BYTES_PER_FLOAT
        ).apply { order(ByteOrder.nativeOrder()) }

        for (y in 0 until IMAGE_SIZE) {
            for (x in 0 until IMAGE_SIZE) {
                val pixel = resized.getPixel(x, y)
                // Normalize RGB to [0, 1]
                inputBuffer.putFloat(((pixel shr 16) and 0xFF) / 255.0f)
                inputBuffer.putFloat(((pixel shr 8) and 0xFF) / 255.0f)
                inputBuffer.putFloat((pixel and 0xFF) / 255.0f)
            }
        }

        // Output buffer: [1, NUM_CLASSES]
        val numClasses = repository.classNames.size
        val outputBuffer = Array(1) { FloatArray(numClasses) }

        // Run inference
        interpreter.run(inputBuffer, outputBuffer)

        // Find top prediction
        val probabilities = outputBuffer[0]
        val maxIndex = probabilities.indices.maxByOrNull { probabilities[it] } ?: 0
        val confidence = probabilities[maxIndex]
        val rawLabel = repository.classNames.getOrElse(maxIndex) { "Unknown" }
        val (crop, disease) = repository.formatLabel(rawLabel)

        return InferenceResult(
            rawLabel = rawLabel,
            cropName = crop,
            diseaseName = if (disease.isEmpty()) "Unknown" else disease,
            confidence = confidence,
            isHealthy = rawLabel.contains("healthy", ignoreCase = true)
        )
    }

    /** Release interpreter resources */
    fun close() {
        interpreter?.close()
        interpreter = null
    }
}

--- utils/BitmapUtils.kt ---
/*
 * Utility functions for bitmap manipulation and compression
 * before passing to the TFLite model.
 */
package com.cropdoctor.app.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import java.io.InputStream

object BitmapUtils {

    /**
     * Loads a Bitmap from a content URI, auto-rotating based on EXIF data.
     * Returns null if the image cannot be loaded.
     */
    fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
        return try {
            val inputStream: InputStream = context.contentResolver.openInputStream(uri)
                ?: return null
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            // Fix rotation using EXIF data
            val exifStream = context.contentResolver.openInputStream(uri) ?: return bitmap
            val exif = ExifInterface(exifStream)
            val rotatedBitmap = rotateBitmapIfRequired(bitmap, exif)
            exifStream.close()
            rotatedBitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Rotates a bitmap based on EXIF orientation data.
     */
    private fun rotateBitmapIfRequired(bitmap: Bitmap, exif: ExifInterface): Bitmap {
        val orientation = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            else -> return bitmap
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    /**
     * Compresses a bitmap to reduce memory usage before inference.
     * @param maxDimension Maximum width or height in pixels
     */
    fun compressBitmap(bitmap: Bitmap, maxDimension: Int = 512): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        if (width <= maxDimension && height <= maxDimension) return bitmap

        val scale = maxDimension.toFloat() / maxOf(width, height)
        val newWidth = (width * scale).toInt()
        val newHeight = (height * scale).toInt()
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }
}

--- utils/PermissionUtils.kt ---
/*
 * Centralizes permission logic for Camera and Storage access.
 */
package com.cropdoctor.app.utils

import android.os.Build

object PermissionUtils {

    /**
     * Returns the correct list of permissions needed based on Android version.
     */
    fun getRequiredPermissions(): Array<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.READ_MEDIA_IMAGES
            )
        } else {
            arrayOf(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    }
}

--- viewmodel/CropDiseaseViewModel.kt ---
/*
 * ViewModel that manages state for the crop disease detection workflow.
 * Survives configuration changes and exposes StateFlow to Compose UI.
 */
package com.cropdoctor.app.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cropdoctor.app.data.ClassNameRepository
import com.cropdoctor.app.model.InferenceResult
import com.cropdoctor.app.model.TFLiteInferenceHelper
import com.cropdoctor.app.utils.BitmapUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Sealed class for all possible UI states
sealed class DetectionState {
    object Idle : DetectionState()
    object ModelLoading : DetectionState()
    object ModelReady : DetectionState()
    object Analyzing : DetectionState()
    data class Success(val result: InferenceResult, val bitmap: Bitmap) : DetectionState()
    data class Error(val message: String) : DetectionState()
}

class CropDiseaseViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ClassNameRepository(application)
    private val inferenceHelper = TFLiteInferenceHelper(application, repository)

    // Currently selected image bitmap for preview
    private val _selectedBitmap = MutableStateFlow<Bitmap?>(null)
    val selectedBitmap: StateFlow<Bitmap?> = _selectedBitmap.asStateFlow()

    // Main detection state
    private val _detectionState = MutableStateFlow<DetectionState>(DetectionState.ModelLoading)
    val detectionState: StateFlow<DetectionState> = _detectionState.asStateFlow()

    init {
        loadModel()
    }

    /** Loads the TFLite model asynchronously in a background thread */
    private fun loadModel() {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    inferenceHelper.loadModel()
                }
                _detectionState.value = DetectionState.ModelReady
            } catch (e: Exception) {
                _detectionState.value = DetectionState.Error("Failed to load model: ${e.message}")
            }
        }
    }

    /**
     * Called when user selects an image (from camera or gallery).
     * Loads and compresses the bitmap for preview.
     */
    fun onImageSelected(uri: Uri) {
        viewModelScope.launch {
            val bitmap = withContext(Dispatchers.IO) {
                val raw = BitmapUtils.uriToBitmap(getApplication(), uri)
                raw?.let { BitmapUtils.compressBitmap(it) }
            }
            if (bitmap != null) {
                _selectedBitmap.value = bitmap
                _detectionState.value = DetectionState.ModelReady
            } else {
                _detectionState.value = DetectionState.Error("Could not load image. Please try another.")
            }
        }
    }

    /**
     * Runs TFLite inference on the currently selected bitmap.
     */
    fun detectDisease() {
        val bitmap = _selectedBitmap.value ?: return
        viewModelScope.launch {
            _detectionState.value = DetectionState.Analyzing
            try {
                val result = withContext(Dispatchers.Default) {
                    inferenceHelper.classify(bitmap)
                }
                _detectionState.value = DetectionState.Success(result, bitmap)
            } catch (e: Exception) {
                _detectionState.value = DetectionState.Error("Detection failed: ${e.message}")
            }
        }
    }

    /** Resets state so user can try another image */
    fun reset() {
        _selectedBitmap.value = null
        _detectionState.value = DetectionState.ModelReady
    }

    override fun onCleared() {
        super.onCleared()
        inferenceHelper.close()
    }
}

--- ui/theme/Color.kt ---
package com.cropdoctor.app.ui.theme

import androidx.compose.ui.graphics.Color

val Green10 = Color(0xFF003314)
val Green20 = Color(0xFF00682A)
val Green40 = Color(0xFF1B8C3E)
val Green80 = Color(0xFF8FD9A8)
val Green90 = Color(0xFFB0EDBB)

val DarkGreen10 = Color(0xFF00210C)
val DarkGreen20 = Color(0xFF003919)
val DarkGreen40 = Color(0xFF00522A)
val DarkGreen80 = Color(0xFF72DB91)
val DarkGreen90 = Color(0xFF8FF7AB)

val Orange10 = Color(0xFF361000)
val Orange20 = Color(0xFF5C1A00)
val Orange40 = Color(0xFF9B3400)
val Orange80 = Color(0xFFFFB59E)
val Orange90 = Color(0xFFFFDBCF)

val Red10 = Color(0xFF410001)
val Red20 = Color(0xFF680003)
val Red40 = Color(0xFF9E0007)
val Red80 = Color(0xFFFFB4A9)
val Red90 = Color(0xFFFFDAD4)

val Grey10 = Color(0xFF1A1C18)
val Grey20 = Color(0xFF2F312C)
val Grey90 = Color(0xFFE2E3DC)
val Grey95 = Color(0xFFF0F1EA)
val Grey99 = Color(0xFFFBFDF4)

val GreenGrey30 = Color(0xFF316847)
val GreenGrey50 = Color(0xFF52A771)
val GreenGrey60 = Color(0xFF74BE91)
val GreenGrey80 = Color(0xFFBBDFCA)
val GreenGrey90 = Color(0xFFD7EFE1)

--- ui/theme/Type.kt ---
package com.cropdoctor.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.15.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    )
)

--- ui/theme/Theme.kt ---
package com.cropdoctor.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Green40,
    onPrimary = Grey99,
    primaryContainer = Green90,
    onPrimaryContainer = Green10,
    secondary = GreenGrey30,
    onSecondary = Grey99,
    secondaryContainer = GreenGrey90,
    onSecondaryContainer = DarkGreen10,
    tertiary = Orange40,
    onTertiary = Grey99,
    tertiaryContainer = Orange90,
    onTertiaryContainer = Orange10,
    error = Red40,
    onError = Grey99,
    errorContainer = Red90,
    onErrorContainer = Red10,
    background = Grey99,
    onBackground = Grey10,
    surface = Grey99,
    onSurface = Grey10,
    surfaceVariant = GreenGrey90,
    onSurfaceVariant = GreenGrey30
)

private val DarkColorScheme = darkColorScheme(
    primary = Green80,
    onPrimary = Green20,
    primaryContainer = Green40,
    onPrimaryContainer = Green90,
    secondary = GreenGrey80,
    onSecondary = DarkGreen20,
    secondaryContainer = DarkGreen40,
    onSecondaryContainer = GreenGrey90,
    tertiary = Orange80,
    onTertiary = Orange20,
    tertiaryContainer = Orange40,
    onTertiaryContainer = Orange90,
    error = Red80,
    onError = Red20,
    errorContainer = Red40,
    onErrorContainer = Red90,
    background = Grey10,
    onBackground = Grey90,
    surface = Grey10,
    onSurface = Grey90,
    surfaceVariant = GreenGrey30,
    onSurfaceVariant = GreenGrey80
)

@Composable
fun CropDoctorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

--- screens/SplashScreen.kt ---
/*
 * Splash screen shown while the TFLite model is loading.
 * Automatically navigates to HomeScreen when model is ready.
 */
package com.cropdoctor.app.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SplashScreen(
    onModelReady: () -> Unit,
    isModelReady: Boolean
) {
    // Pulse animation for the icon
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    // Navigate once model is ready
    LaunchedEffect(isModelReady) {
        if (isModelReady) onModelReady()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App icon with pulse
            Icon(
                imageVector = Icons.Default.Eco,
                contentDescription = "Crop Doctor",
                modifier = Modifier
                    .size(100.dp)
                    .scale(scale),
                tint = MaterialTheme.colorScheme.onPrimary
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "CropDoctor",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 34.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Crop Disease Detection",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(48.dp))

            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(32.dp),
                strokeWidth = 3.dp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Loading model...",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
            )
        }
    }
}

--- screens/HomeScreen.kt ---
/*
 * Main screen with two large action buttons:
 * Camera capture and Gallery selection.
 */
package com.cropdoctor.app.screens

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    onImageSelected: (Uri) -> Unit,
    onNavigateToPreview: () -> Unit
) {
    val context = LocalContext.current
    var cameraImageUri by remember { mutableStateOf<Uri?>(null) }
    var showPermissionDenied by remember { mutableStateOf(false) }

    // Gallery launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            onImageSelected(it)
            onNavigateToPreview()
        }
    }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            cameraImageUri?.let {
                onImageSelected(it)
                onNavigateToPreview()
            }
        }
    }

    // Camera permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraImageUri = createImageUri(context)
            cameraImageUri?.let { cameraLauncher.launch(it) }
        } else {
            showPermissionDenied = true
        }
    }

    // Permission denied snackbar
    if (showPermissionDenied) {
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(3000)
            showPermissionDenied = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Logo
            Icon(
                imageVector = Icons.Default.Eco,
                contentDescription = null,
                modifier = Modifier.size(72.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "CropDoctor",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Crop Disease Detection",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Divider
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Take or upload a clear photo\nof a crop leaf to detect disease",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Camera Button
            Button(
                onClick = {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Click Picture",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Gallery Button
            OutlinedButton(
                onClick = { galleryLauncher.launch("image/*") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(16.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    width = 2.dp
                )
            ) {
                Icon(
                    imageVector = Icons.Default.PhotoLibrary,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Upload from Gallery",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Offline badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.WifiOff,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Works 100% offline",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                )
            }
        }

        // Permission denied notice
        AnimatedVisibility(
            visible = showPermissionDenied,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = slideInVertically { it },
            exit = slideOutVertically { it }
        ) {
            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = "Camera permission is required to take photos.",
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}

/** Creates a temporary image file URI for camera capture via FileProvider */
private fun createImageUri(context: Context): Uri {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFile = File(
        context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
        "CROP_${timeStamp}.jpg"
    )
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        imageFile
    )
}

--- screens/PreviewScreen.kt ---
/*
 * Shows the selected/captured image preview with a confirmation dialog
 * asking the user to confirm before running detection.
 */
package com.cropdoctor.app.screens

import android.graphics.Bitmap
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun PreviewScreen(
    bitmap: Bitmap?,
    isAnalyzing: Boolean,
    onDetect: () -> Unit,
    onCancel: () -> Unit
) {
    var showConfirmDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Image Preview",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            // Image preview card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Selected crop image",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(20.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Placeholder when no image
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Looks good? Tap 'Detect Disease' to\nanalyze this leaf image.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.weight(1f))

            // Detect button
            Button(
                onClick = { showConfirmDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = bitmap != null && !isAnalyzing
            ) {
                Icon(Icons.Default.Search, contentDescription = null)
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    "Detect Disease",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Cancel button
            TextButton(
                onClick = onCancel,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Cancel — Choose Another",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        // Loading overlay while analyzing
        AnimatedVisibility(
            visible = isAnalyzing,
            modifier = Modifier.fillMaxSize(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.85f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(56.dp),
                        strokeWidth = 4.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Analyzing crop...",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Confirmation dialog
        if (showConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showConfirmDialog = false },
                icon = {
                    Icon(Icons.Default.BugReport, contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary)
                },
                title = {
                    Text("Detect Disease?", fontWeight = FontWeight.Bold)
                },
                text = {
                    Text("The AI model will analyze the leaf image for diseases. This runs completely offline on your device.")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showConfirmDialog = false
                            onDetect()
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Yes, Detect", fontWeight = FontWeight.SemiBold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showConfirmDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

--- screens/ResultScreen.kt ---
/*
 * Displays the inference result: crop name, disease name,
 * confidence percentage, and the analyzed image.
 */
package com.cropdoctor.app.screens

import android.graphics.Bitmap
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cropdoctor.app.model.InferenceResult

@Composable
fun ResultScreen(
    result: InferenceResult,
    bitmap: Bitmap,
    onTryAnother: () -> Unit
) {
    val scrollState = rememberScrollState()
    val confidencePercent = (result.confidence * 100).toInt()

    // Animate confidence bar
    val animatedProgress by animateFloatAsState(
        targetValue = result.confidence,
        animationSpec = tween(durationMillis = 1200, easing = EaseOutCubic),
        label = "confidence"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Title
        Text(
            text = "Detection Result",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        // Image preview (smaller than PreviewScreen)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Analyzed leaf image",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(20.dp)),
                contentScale = ContentScale.Crop
            )
        }

        // Result Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (result.isHealthy)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Healthy / Diseased indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = if (result.isHealthy)
                            Icons.Default.CheckCircle else Icons.Default.Warning,
                        contentDescription = null,
                        tint = if (result.isHealthy)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = if (result.isHealthy) "Healthy Plant" else "Disease Detected",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (result.isHealthy)
                            MaterialTheme.colorScheme.onPrimaryContainer
                        else
                            MaterialTheme.colorScheme.onErrorContainer
                    )
                }

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )

                // Crop name
                LabelValueRow(
                    label = "Crop",
                    value = result.cropName,
                    isHealthy = result.isHealthy
                )

                // Disease name
                LabelValueRow(
                    label = "Condition",
                    value = result.diseaseName,
                    isHealthy = result.isHealthy
                )

                // Confidence
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Confidence",
                            style = MaterialTheme.typography.labelLarge,
                            color = if (result.isHealthy)
                                MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            else
                                MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.7f)
                        )
                        Text(
                            text = "$confidencePercent%",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = if (result.isHealthy)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.error
                        )
                    }
                    LinearProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(50.dp)),
                        color = if (result.isHealthy)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.error,
                        trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    )
                }
            }
        }

        // Offline note
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.WifiOff, null,
                modifier = Modifier.size(14.dp),
                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Detected locally, no internet used",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Try Another button
        Button(
            onClick = onTryAnother,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Default.Refresh, null)
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                "Try Another Image",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun LabelValueRow(label: String, value: String, isHealthy: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = if (isHealthy)
                MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            else
                MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.7f),
            modifier = Modifier.weight(0.35f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = if (isHealthy)
                MaterialTheme.colorScheme.onPrimaryContainer
            else
                MaterialTheme.colorScheme.onErrorContainer,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(0.65f)
        )
    }
}

--- CropDoctorApp.kt ---
/*
 * Navigation host — wires all screens together using
 * Jetpack Navigation Compose.
 */
package com.cropdoctor.app

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cropdoctor.app.screens.*
import com.cropdoctor.app.viewmodel.CropDiseaseViewModel
import com.cropdoctor.app.viewmodel.DetectionState

// Navigation route constants
object Routes {
    const val SPLASH = "splash"
    const val HOME = "home"
    const val PREVIEW = "preview"
    const val RESULT = "result"
}

@Composable
fun CropDoctorApp() {
    val navController = rememberNavController()
    val viewModel: CropDiseaseViewModel = viewModel()
    val detectionState by viewModel.detectionState.collectAsStateWithLifecycle()
    val selectedBitmap by viewModel.selectedBitmap.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH,
        enterTransition = { fadeIn(tween(300)) },
        exitTransition = { fadeOut(tween(300)) }
    ) {
        // Splash screen
        composable(Routes.SPLASH) {
            SplashScreen(
                onModelReady = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                },
                isModelReady = detectionState != DetectionState.ModelLoading
            )
        }

        // Home screen
        composable(Routes.HOME) {
            HomeScreen(
                onImageSelected = { uri ->
                    viewModel.onImageSelected(uri)
                },
                onNavigateToPreview = {
                    navController.navigate(Routes.PREVIEW)
                }
            )
        }

        // Preview/confirm screen
        composable(Routes.PREVIEW) {
            PreviewScreen(
                bitmap = selectedBitmap,
                isAnalyzing = detectionState == DetectionState.Analyzing,
                onDetect = {
                    viewModel.detectDisease()
                },
                onCancel = {
                    viewModel.reset()
                    navController.popBackStack()
                }
            )

            // Navigate to result when inference completes
            LaunchedEffect(detectionState) {
                if (detectionState is DetectionState.Success) {
                    navController.navigate(Routes.RESULT)
                }
            }
        }

        // Result screen
        composable(Routes.RESULT) {
            val state = detectionState
            if (state is DetectionState.Success) {
                ResultScreen(
                    result = state.result,
                    bitmap = state.bitmap,
                    onTryAnother = {
                        viewModel.reset()
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.HOME) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}

--- MainActivity.kt ---
/*
 * Entry point of the application.
 * Sets up Compose and applies the app theme.
 */
package com.cropdoctor.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.cropdoctor.app.ui.theme.CropDoctorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CropDoctorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CropDoctorApp()
                }
            }
        }
    }
}

---

STEP 11 — Add ExifInterface dependency to app/build.gradle dependencies:

implementation 'androidx.exifinterface:exifinterface:1.3.6'

Add this line to the existing dependencies block in app/build.gradle.

---

STEP 12 — gradle.properties:

org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
android.useAndroidX=true
kotlin.code.style=official
android.nonTransitiveRClass=true

---

STEP 13 — local.properties:
Create this file with a placeholder. The developer must update sdk.dir:

sdk.dir=/path/to/your/android/sdk

---

STEP 14 — VERIFICATION CHECKLIST (run these checks after creating all files):

1. Confirm app/src/main/assets/ contains:
   - Model_V5.tflite
   - class_names.json
   - labels.txt

2. Confirm res/xml/file_paths.xml exists.

3. Confirm AndroidManifest.xml has:
   - CAMERA permission
   - READ_MEDIA_IMAGES permission
   - FileProvider block with authorities="${applicationId}.fileprovider"

4. Confirm aaptOptions block with noCompress "tflite" is in app/build.gradle.

5. Confirm all Kotlin files compile with no unresolved import references.
   - Every import used in each file must be present.
   - Do not use any deprecated Compose APIs.

6. Confirm LaunchedEffect in PreviewScreen navigates correctly to result.

7. Confirm ViewModel survives rotation (AndroidViewModel base class used).

8. Do NOT use any internet APIs, cloud calls, or external ML services.

9. Do NOT include the MainActivity.java file from the root — it belongs 
   to a different project (Capacitor). Ignore it completely.

10. After all files are created, print a summary tree of the entire project
    structure so I can verify everything is in place.

---

IMPORTANT FINAL NOTES TO THE AGENT:
- Write every single file completely. Do not truncate or summarize any file.
- Do not skip any file listed above.
- Do not add placeholder comments like "// rest of the code here".
- Every function must be fully implemented.
- All imports in every Kotlin file must be explicitly written at the top.
- After creating all files, tell me exactly how to build and run the app