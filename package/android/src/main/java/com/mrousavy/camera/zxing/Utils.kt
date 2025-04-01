package com.mrousavy.camera.zxing

import android.graphics.Point
import android.util.Base64
import com.facebook.react.bridge.WritableNativeArray
import com.facebook.react.bridge.WritableNativeMap
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.zxing.Result
import com.google.zxing.ResultPoint
import com.google.zxing.BarcodeFormat



class Utils {
    companion object {
        fun wrapResults(result: Result): WritableNativeMap {
            val map = WritableNativeMap()
            map.putString("barcodeText", result.text)
            map.putString("barcodeFormat", result.barcodeFormat.name)
            val bytes = result.rawBytes
            if (bytes != null) {
                map.putString("barcodeBytesBase64", Base64.encodeToString(bytes, Base64.DEFAULT))
            } else {
                map.putString("barcodeBytesBase64", "")
            }
            var points: Array<ResultPoint>? = null
            try {
                points = result.resultPoints
            } catch (e: Error) {
            }
            val pointsArray = WritableNativeArray()
            if (points != null) {
                for (point in points) {
                    val pointAsMap = WritableNativeMap()
                    pointAsMap.putInt("x", point.x.toInt())
                    pointAsMap.putInt("y", point.y.toInt())
                    pointsArray.pushMap(pointAsMap)
                }
            }
            map.putArray("points", pointsArray)
            return map
        }




    }
}
