package com.mrousavy.camera.core

import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis.Analyzer
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.io.Closeable

import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.NotFoundException
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.Result
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.multi.GenericMultipleBarcodeReader
import com.google.zxing.multi.MultipleBarcodeReader

class CodeScannerPipeline(val isSupportGoogle:Boolean,val configuration: CameraConfiguration.CodeScanner, val callback: CameraSession.Callback) :
  Closeable,
  Analyzer {
  companion object {
    private const val TAG = "CodeScannerPipeline"
  }
  // Google mlkit 扫码识别
  private val scanner: BarcodeScanner
  // 下面两个是 zxing 的扫码识别
  private val reader : MultiFormatReader
  private val multipleReader:MultipleBarcodeReader


  init {
    val types = configuration.codeTypes.map { it.toBarcodeType() }
    val barcodeScannerOptions = BarcodeScannerOptions.Builder()
      .setBarcodeFormats(types[0], *types.toIntArray())
      .build()
    scanner = BarcodeScanning.getClient(barcodeScannerOptions)
    reader = MultiFormatReader();
    multipleReader = GenericMultipleBarcodeReader(reader)

  }

  @OptIn(ExperimentalGetImage::class)
  override fun analyze(imageProxy: ImageProxy) {
    val image = imageProxy.image ?: throw InvalidImageTypeError()
    if (isSupportGoogle){
      try {
        val inputImage = InputImage.fromMediaImage(image, imageProxy.imageInfo.rotationDegrees)
        scanner.process(inputImage)
          .addOnSuccessListener { barcodes ->
            if (barcodes.isNotEmpty()) {

              callback.onCodeScanned(barcodes, CodeScannerFrame(inputImage.width, inputImage.height))
            }
          }
          .addOnFailureListener { error ->
            Log.e(TAG, "Failed to process Image!", error)
            callback.onError(error)
          }
          .addOnCompleteListener {
            imageProxy.close()
          }
      } catch (e: Throwable) {
        Log.e(TAG, "Failed to process Image!", e)
        imageProxy.close()
      }
    } else {
      // 不支持google服务，使用zxing识别
      val bitmap = imageProxy.toBitmap();
      val width = bitmap.width
      val height = bitmap.height
      val pixels = IntArray(width * height)
      bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
      val source = RGBLuminanceSource(width, height, pixels)
      val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
      val multiple:Boolean = true;
      val barcodes:List<Result>;
      try {
        if (multiple) {
          barcodes = multipleReader.decodeMultiple(binaryBitmap).toList()
        } else {
          barcodes = listOf(reader.decode(binaryBitmap))
        }
        callback.onCodeScannedZxing(barcodes, CodeScannerFrame(width, height))
      } catch (e: NotFoundException) {
        // 没有识别到二维码，不算错误。什么都不处理
//        callback.onError(e)
        imageProxy.close()
      } catch (e: Exception){
        Log.e(TAG, "Failed to process Image!", e)
        callback.onError(e)
        imageProxy.close()
      }
    }
  }

  override fun close() {
    scanner.close()
  }
}
