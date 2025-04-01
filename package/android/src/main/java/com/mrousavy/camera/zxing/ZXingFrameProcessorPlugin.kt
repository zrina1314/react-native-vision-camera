//package com.mrousavy.camera.zxing
//
//import com.facebook.react.bridge.ReadableNativeMap.toHashMap
//import com.google.zxing.BinaryBitmap
//import com.google.zxing.NotFoundException
//import com.google.zxing.PlanarYUVLuminanceSource
//import com.google.zxing.Result
//import com.google.zxing.common.HybridBinarizer
//import com.mrousavy.camera.core.FrameInvalidError
//import com.mrousavy.camera.frameprocessors.Frame
//import com.mrousavy.camera.frameprocessors.FrameProcessorPlugin
//import com.mrousavy.camera.frameprocessors.VisionCameraProxy
////import com.visioncamerazxing.VisionCameraZXingModule
//
//class ZXingFrameProcessorPlugin internal constructor(
//    proxy: VisionCameraProxy,
//    options: Map<String?, Any?>?
//) :
//    FrameProcessorPlugin() {
//    override fun callback(frame: Frame, arguments: Map<String, Any>?): Any? {
//        val array: MutableList<Any> = ArrayList()
//        var multiple: Boolean? = false
//        if (arguments != null) {
//            if (arguments.containsKey("multiple")) {
//                multiple = arguments["multiple"] as Boolean?
//            }
//        }
//        try {
//            val buffer = frame.image.planes[0].buffer
//            val length = buffer.remaining()
//            val bytes = ByteArray(length)
//            buffer[bytes]
//            val source = PlanarYUVLuminanceSource(
//                bytes,
//                frame.width,
//                frame.height,
//                0,
//                0,
//                frame.width,
//                frame.height,
//                false
//            )
//            val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
//            try {
//                if (multiple!!) {
//                    val results: Array<Result> =
//                        VisionCameraZXingModule.decodeBinaryBitmapMultiple(binaryBitmap)
//                    for (result in results) {
//                        array.add(Utils.Companion.wrapResults(result).toHashMap())
//                    }
//                } else {
//                    val result: Result = VisionCameraZXingModule.decodeBinaryBitmap(binaryBitmap)
//                    array.add(Utils.Companion.wrapResults(result).toHashMap())
//                }
//            } catch (e: NotFoundException) {
//            }
//        } catch (e: FrameInvalidError) {
//            throw RuntimeException(e)
//        }
//        return array
//    }
//}
//
//
