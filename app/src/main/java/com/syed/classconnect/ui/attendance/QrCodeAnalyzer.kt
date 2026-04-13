package com.syed.classconnect.ui.attendance

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import timber.log.Timber

/**
 * CameraX analyzer for real-time QR code detection using ML Kit
 * Replaces ZXing's IntentIntegrator with in-app camera preview
 */
class QrCodeAnalyzer(
    private val onQrCodeDetected: (String) -> Unit,
    private val onError: (Exception) -> Unit = {}
) : ImageAnalysis.Analyzer {

    private val barcodeScanner = BarcodeScanning.getClient()

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image

        if (mediaImage != null) {
            val inputImage = InputImage.fromMediaImage(
                mediaImage,
                imageProxy.imageInfo.rotationDegrees
            )

            barcodeScanner.process(inputImage)
                .addOnSuccessListener { barcodes ->
                    barcodes.forEach { barcode ->
                        val qrCodeValue = barcode.rawValue
                        if (qrCodeValue != null) {
                            Timber.d("QR Code detected: $qrCodeValue")
                            onQrCodeDetected(qrCodeValue)
                        }
                    }
                    imageProxy.close()
                }
                .addOnFailureListener { exception ->
                    onError(exception)
                    imageProxy.close()
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }

    /**
     * Closes the barcode scanner to free resources
     */
    fun close() {
        barcodeScanner.close()
    }
}

/**
 * QR Code expiry countdown timer
 */
data class QrCodeExpiryState(
    val remainingSeconds: Int,
    val isExpired: Boolean = remainingSeconds <= 0,
    val warningLevel: WarningLevel = when {
        remainingSeconds > 180 -> WarningLevel.GREEN
        remainingSeconds > 60 -> WarningLevel.YELLOW
        else -> WarningLevel.RED
    }
)

enum class WarningLevel {
    GREEN,   // 3+ minutes remaining
    YELLOW,  // 1-3 minutes remaining
    RED      // Less than 1 minute remaining
}

/**
 * Formats countdown timer display
 */
fun formatCountdownTime(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%02d:%02d", minutes, remainingSeconds)
}

