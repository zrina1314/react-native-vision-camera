//package com.visioncamerazxing
//
//import com.facebook.react.ReactPackage
//import com.facebook.react.bridge.NativeModule
//import com.facebook.react.bridge.ReactApplicationContext
//import com.facebook.react.uimanager.ViewManager
//import com.mrousavy.camera.frameprocessors.FrameProcessorPluginRegistry
//import com.mrousavy.camera.frameprocessors.FrameProcessorPluginRegistry.PluginInitializer
//import com.visioncamerazxing.VisionCameraZXingModule
//
//class VisionCameraZXingPackage : ReactPackage {
//    override fun createNativeModules(reactContext: ReactApplicationContext): List<NativeModule> {
//        val modules: MutableList<NativeModule> = ArrayList()
//        modules.add(VisionCameraZXingModule(reactContext))
//        return modules
//    }
//
//    override fun createViewManagers(reactContext: ReactApplicationContext): List<ViewManager<*, *>> {
//        return emptyList()
//    }
//
//    companion object {
//        init {
//            FrameProcessorPluginRegistry.addFrameProcessorPlugin(
//                "zxing",
//                PluginInitializer { ZXingFrameProcessorPlugin() })
//        }
//    }
//}
