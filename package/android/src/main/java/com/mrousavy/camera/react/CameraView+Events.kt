package com.mrousavy.camera.react

import android.graphics.Point
import android.util.Log
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactContext
import com.facebook.react.bridge.WritableMap
import com.facebook.react.uimanager.UIManagerHelper
import com.facebook.react.uimanager.events.Event
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.zxing.BarcodeFormat
import com.google.zxing.Result
import com.mrousavy.camera.core.CameraError
import com.mrousavy.camera.core.CodeScannerFrame
import com.mrousavy.camera.core.UnknownCameraError
import com.mrousavy.camera.core.types.CodeType
import com.mrousavy.camera.core.types.Orientation
import com.mrousavy.camera.core.types.ShutterType

fun CameraView.invokeOnInitialized() {
  Log.i(CameraView.TAG, "invokeOnInitialized()")

  val surfaceId = UIManagerHelper.getSurfaceId(this)
  val event = CameraInitializedEvent(surfaceId, id)
  this.sendEvent(event)
}

fun CameraView.invokeOnStarted() {
  Log.i(CameraView.TAG, "invokeOnStarted()")

  val surfaceId = UIManagerHelper.getSurfaceId(this)
  val event = CameraStartedEvent(surfaceId, id)
  this.sendEvent(event)
}

fun CameraView.invokeOnStopped() {
  Log.i(CameraView.TAG, "invokeOnStopped()")

  val surfaceId = UIManagerHelper.getSurfaceId(this)
  val event = CameraStoppedEvent(surfaceId, id)
  this.sendEvent(event)
}

fun CameraView.invokeOnPreviewStarted() {
  Log.i(CameraView.TAG, "invokeOnPreviewStarted()")

  val surfaceId = UIManagerHelper.getSurfaceId(this)
  val event = CameraPreviewStartedEvent(surfaceId, id)
  this.sendEvent(event)
}

fun CameraView.invokeOnPreviewStopped() {
  Log.i(CameraView.TAG, "invokeOnPreviewStopped()")

  val surfaceId = UIManagerHelper.getSurfaceId(this)
  val event = CameraPreviewStoppedEvent(surfaceId, id)
  this.sendEvent(event)
}

fun CameraView.invokeOnShutter(type: ShutterType) {
  Log.i(CameraView.TAG, "invokeOnShutter($type)")

  val surfaceId = UIManagerHelper.getSurfaceId(this)
  val data = Arguments.createMap()
  data.putString("type", type.unionValue)

  val event = CameraShutterEvent(surfaceId, id, data)
  this.sendEvent(event)
}

fun CameraView.invokeOnOutputOrientationChanged(outputOrientation: Orientation) {
  Log.i(CameraView.TAG, "invokeOnOutputOrientationChanged($outputOrientation)")

  val surfaceId = UIManagerHelper.getSurfaceId(this)
  val data = Arguments.createMap()
  data.putString("outputOrientation", outputOrientation.unionValue)

  val event = CameraOutputOrientationChangedEvent(surfaceId, id, data)
  this.sendEvent(event)
}

fun CameraView.invokeOnPreviewOrientationChanged(previewOrientation: Orientation) {
  Log.i(CameraView.TAG, "invokeOnPreviewOrientationChanged($previewOrientation)")

  val surfaceId = UIManagerHelper.getSurfaceId(this)
  val data = Arguments.createMap()
  data.putString("previewOrientation", previewOrientation.unionValue)

  val event = CameraPreviewOrientationChangedEvent(surfaceId, id, data)
  this.sendEvent(event)
}

fun CameraView.invokeOnError(error: Throwable) {
  Log.e(CameraView.TAG, "invokeOnError(...):")
  error.printStackTrace()

  val cameraError =
    when (error) {
      is CameraError -> error
      else -> UnknownCameraError(error)
    }
  val data = Arguments.createMap()
  data.putString("code", cameraError.code)
  data.putString("message", cameraError.message)
  cameraError.cause?.let { cause ->
    data.putMap("cause", errorToMap(cause))
  }

  val surfaceId = UIManagerHelper.getSurfaceId(this)
  val event = CameraErrorEvent(surfaceId, id, data)
  this.sendEvent(event)
}

fun CameraView.invokeOnViewReady() {
  val surfaceId = UIManagerHelper.getSurfaceId(this)
  val event = CameraViewReadyEvent(surfaceId, id)
  this.sendEvent(event)
}

fun CameraView.invokeOnAverageFpsChanged(averageFps: Double) {
  Log.i(CameraView.TAG, "invokeOnAverageFpsChanged($averageFps)")

  val surfaceId = UIManagerHelper.getSurfaceId(this)
  val data = Arguments.createMap()
  data.putDouble("averageFps", averageFps)

  val event = AverageFpsChangedEvent(surfaceId, id, data)
  this.sendEvent(event)
}

fun CameraView.invokeOnCodeScanned(barcodes: List<Barcode>, scannerFrame: CodeScannerFrame) {
  val codes = Arguments.createArray()
  barcodes.forEach { barcode ->
    val code = Arguments.createMap()
    val type = CodeType.fromBarcodeType(barcode.format)
    code.putString("type", type.unionValue)
    code.putString("value", barcode.rawValue)

    barcode.boundingBox?.let { rect ->
      val frame = Arguments.createMap()
      frame.putInt("x", rect.left)
      frame.putInt("y", rect.top)
      frame.putInt("width", rect.right - rect.left)
      frame.putInt("height", rect.bottom - rect.top)
      code.putMap("frame", frame)
    }

    barcode.cornerPoints?.let { points ->
      val corners = Arguments.createArray()
      points.forEach { point ->
        val pt = Arguments.createMap()
        pt.putInt("x", point.x)
        pt.putInt("y", point.y)
        corners.pushMap(pt)
      }
      code.putArray("corners", corners)
    }
    codes.pushMap(code)
  }

  val data = Arguments.createMap()
  data.putArray("codes", codes)
  val codeScannerFrame = Arguments.createMap()
  codeScannerFrame.putInt("width", scannerFrame.width)
  codeScannerFrame.putInt("height", scannerFrame.height)
  data.putMap("frame", codeScannerFrame)

  val surfaceId = UIManagerHelper.getSurfaceId(this)
  val event = CameraCodeScannedEvent(surfaceId, id, data)
  this.sendEvent(event)
}


fun CameraView.invokeOnCodeScannedZxing(barcodes: List<Result>, scannerFrame: CodeScannerFrame) {
  val codes = Arguments.createArray()
  barcodes.forEach { barcode ->
    val code = Arguments.createMap()

    // 1. 映射条码格式（ZXing → ML Kit）
    val format = when (barcode.barcodeFormat) {
      BarcodeFormat.QR_CODE -> Barcode.FORMAT_QR_CODE
      BarcodeFormat.AZTEC -> Barcode.FORMAT_AZTEC
      BarcodeFormat.CODABAR -> Barcode.FORMAT_CODABAR
      BarcodeFormat.CODE_39 -> Barcode.FORMAT_CODE_39
      BarcodeFormat.CODE_93 -> Barcode.FORMAT_CODE_93
      BarcodeFormat.CODE_128 -> Barcode.FORMAT_CODE_128
      BarcodeFormat.DATA_MATRIX -> Barcode.FORMAT_DATA_MATRIX
      BarcodeFormat.EAN_8 -> Barcode.FORMAT_EAN_8
      BarcodeFormat.EAN_13 -> Barcode.FORMAT_EAN_13
      BarcodeFormat.ITF -> Barcode.FORMAT_ITF
      BarcodeFormat.PDF_417 -> Barcode.FORMAT_PDF417
      BarcodeFormat.UPC_A -> Barcode.FORMAT_UPC_A
      BarcodeFormat.UPC_E -> Barcode.FORMAT_UPC_E
      else -> Barcode.FORMAT_UNKNOWN
    }

    val type = CodeType.fromBarcodeType(format)

    code.putString("type", type.unionValue)
    code.putString("value", barcode.text)

//    val cornerPoints = barcode.resultPoints?.map { resultPoint ->
//      Point(resultPoint.x.toInt(), resultPoint.y.toInt())
//    }?.toTypedArray()

//    cornerPoints?.let { rect ->
//      val frame = Arguments.createMap()
//      frame.putInt("x", rect.left)
//      frame.putInt("y", rect.top)
//      frame.putInt("width", rect.right - rect.left)
//      frame.putInt("height", rect.bottom - rect.top)
//      code.putMap("frame", frame)
//    }

    barcode.resultPoints?.let { points ->
      val corners = Arguments.createArray()
      points.forEach { point ->
        val pt = Arguments.createMap()
        pt.putInt("x", point.x.toInt())
        pt.putInt("y", point.y.toInt())
        corners.pushMap(pt)
      }
      code.putArray("corners", corners)
    }
    codes.pushMap(code)
  }

  val data = Arguments.createMap()
  data.putArray("codes", codes)
  val codeScannerFrame = Arguments.createMap()
  codeScannerFrame.putInt("width", scannerFrame.width)
  codeScannerFrame.putInt("height", scannerFrame.height)
  data.putMap("frame", codeScannerFrame)

  val surfaceId = UIManagerHelper.getSurfaceId(this)
  val event = CameraCodeScannedEvent(surfaceId, id, data)
  this.sendEvent(event)
}

private fun CameraView.sendEvent(event: Event<*>) {
  val reactContext = context as ReactContext
  val dispatcher =
    UIManagerHelper.getEventDispatcherForReactTag(reactContext, id)
  dispatcher?.dispatchEvent(event)
}

private fun errorToMap(error: Throwable): WritableMap {
  val map = Arguments.createMap()
  map.putString("message", error.message)
  map.putString("stacktrace", error.stackTraceToString())
  error.cause?.let { cause ->
    map.putMap("cause", errorToMap(cause))
  }
  return map
}
