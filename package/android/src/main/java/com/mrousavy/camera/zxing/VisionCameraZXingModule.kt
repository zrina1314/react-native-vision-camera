//package com.visioncamerazxing
//
//import android.graphics.Bitmap
//import com.facebook.react.bridge.Promise
//import com.facebook.react.bridge.ReactApplicationContext
//import com.facebook.react.bridge.ReactContextBaseJavaModule
//import com.facebook.react.bridge.ReactMethod
//import com.facebook.react.bridge.ReadableMap
//import com.facebook.react.bridge.WritableNativeArray
//import com.facebook.react.module.annotations.ReactModule
//import com.google.zxing.BinaryBitmap
//import com.google.zxing.MultiFormatReader
//import com.google.zxing.NotFoundException
//import com.google.zxing.RGBLuminanceSource
//import com.google.zxing.Result
//import com.google.zxing.common.HybridBinarizer
//import com.google.zxing.multi.GenericMultipleBarcodeReader
//import com.google.zxing.multi.MultipleBarcodeReader
//
//@ReactModule(name = VisionCameraZXingModule.NAME)
//class VisionCameraZXingModule(var context: ReactApplicationContext) : ReactContextBaseJavaModule(
//    context
//) {
//    override fun getName(): String {
//        return NAME
//    }
//
//    @ReactMethod
//    fun decodeBase64(base64: String?, config: ReadableMap?, promise: Promise) {
//        try {
//            val bitmap: Bitmap = BitmapUtils.base642Bitmap(base64)
//            var multiple = false
//            if (config != null) {
//                if (config.hasKey("multiple")) {
//                    multiple = config.getBoolean(("multiple"))
//                }
//            }
//
//            val width = bitmap.width
//            val height = bitmap.height
//            val pixels = IntArray(width * height)
//            bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
//            val source = RGBLuminanceSource(width, height, pixels)
//            val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
//            val array = WritableNativeArray()
//            try {
//                if (multiple) {
//                    val results = decodeBinaryBitmapMultiple(binaryBitmap)
//                    for (result in results) {
//                        array.pushMap(Utils.wrapResults(result))
//                    }
//                } else {
//                    val result = decodeBinaryBitmap(binaryBitmap)
//                    array.pushMap(Utils.wrapResults(result))
//                }
//            } catch (e: NotFoundException) {
//            }
//            promise.resolve(array)
//        } catch (e: Error) {
//            e.printStackTrace()
//            promise.reject("ZXING", e.message)
//        }
//    }
//
//    companion object {
//        var reader: MultiFormatReader = MultiFormatReader()
//        var multipleReader: MultipleBarcodeReader = GenericMultipleBarcodeReader(reader)
//        const val NAME: String = "VisionCameraZXing"
//
//        @Throws(NotFoundException::class)
//        fun decodeBinaryBitmap(bitmap: BinaryBitmap?): Result {
//            val result = reader.decode(bitmap)
//            return result
//        }
//
//        @Throws(NotFoundException::class)
//        fun decodeBinaryBitmapMultiple(bitmap: BinaryBitmap?): Array<Result> {
//            val results = multipleReader.decodeMultiple(bitmap)
//            return results
//        }
//    }
//}
