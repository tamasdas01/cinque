/*
 * Navigation host - wires all screens together using
 * Jetpack Navigation Compose.
 */

// CropDoctorApp.kt

package com.cropdoctor.app

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cropdoctor.app.screens.HomeScreen
import com.cropdoctor.app.screens.PreviewScreen
import com.cropdoctor.app.screens.ResultScreen
import com.cropdoctor.app.screens.SplashScreen
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
    val errorMessage = (detectionState as? DetectionState.Error)?.message

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
                errorMessage = errorMessage,
                onDetect = {
                    viewModel.detectDisease()
                },
                onCancel = {
                    viewModel.reset()
                    navController.popBackStack()
                },
                onDismissError = {
                    viewModel.clearError()
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
