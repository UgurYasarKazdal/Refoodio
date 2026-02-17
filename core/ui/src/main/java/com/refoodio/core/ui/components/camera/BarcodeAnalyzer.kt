package com.refoodio.core.ui.components.camera

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

class BarcodeAnalyzer(
    private val onBarcodeDetected: (String) -> Unit,
    private val isLoading: () -> Boolean
) : ImageAnalysis.Analyzer {

    private var isScanning = true
    private val scanner = BarcodeScanning.getClient()

    // Doğrulama için yeni değişkenler
    private var lastScannedBarcode: String? = null
    private var barcodeCount = 0
    private val requiredCount = 3 // Aynı barkodu 3 kez görürse kabul et

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage == null || !isScanning || isLoading()) {
            imageProxy.close()
            return
        }

        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                val barcode = barcodes.firstOrNull()?.rawValue

                if (barcode != null) {
                    // Güvenlik Kilidi: Öncekiyle aynı mı?
                    if (barcode == lastScannedBarcode) {
                        barcodeCount++
                    } else {
                        lastScannedBarcode = barcode
                        barcodeCount = 1
                    }

                    // Eğer yeterli sayıda aynı barkodu okuduysak ViewModel'e gönder
                    if (barcodeCount >= requiredCount) {
                        isScanning = false
                        onBarcodeDetected(barcode)
                        // Resetle (Yeni bir tarama için)
                        barcodeCount = 0
                        lastScannedBarcode = null
                    }
                }
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }
}