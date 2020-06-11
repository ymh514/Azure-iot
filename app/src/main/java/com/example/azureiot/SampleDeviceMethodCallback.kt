package com.example.azureiot

import android.util.Log
import com.microsoft.azure.sdk.iot.device.DeviceTwin.DeviceMethodData
import org.json.JSONException
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset

class SampleDeviceMethodCallback :
    com.microsoft.azure.sdk.iot.device.DeviceTwin.DeviceMethodCallback {
    val TAG = "MainActivity_SampleDeviceMethodCallback"
    override fun call(methodName: String, methodData: Any, context: Any): DeviceMethodData {
        var deviceMethodData: DeviceMethodData
        try {
            when (methodName) {
                "setSendMessagesInterval" -> {
                    val status = method_setSendMessagesInterval(methodName)
                    deviceMethodData = DeviceMethodData(status, "executed $methodName")
                }
                else -> {
                    val status = method_default(methodData)
                    deviceMethodData = DeviceMethodData(status, "executed $methodName")
                }
            }
        } catch (e: Exception) {
            val status = Configs.METHOD_THROWS
            deviceMethodData = DeviceMethodData(status, "Method Throws $methodName")
        }

        return deviceMethodData
    }

    @Throws(UnsupportedEncodingException::class, JSONException::class)
    private fun method_setSendMessagesInterval(methodData: Any): Int {
        val payload = String(methodData as ByteArray, Charset.forName("UTF-8")).replace("\"", "")
        val obj = JSONObject(payload)
        val sendMessagesInterval = obj.getInt("sendInterval")
        Log.d(TAG,"sendMessagesInterval: $sendMessagesInterval")
        return Configs.METHOD_SUCCESS
    }
    private fun method_default(data: Any): Int {
        Log.d(TAG,"invoking default method for this device")
        // Insert device specific code here
        return Configs.METHOD_NOT_DEFINED
    }
}

