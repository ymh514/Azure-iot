package com.example.azureiot

import android.util.Log
import com.microsoft.azure.sdk.iot.device.IotHubEventCallback
import com.microsoft.azure.sdk.iot.device.IotHubStatusCode

class DeviceMethodStatusCallBack : IotHubEventCallback {
    val TAG = "MainActivity_DeviceMethodStatusCallBack"
    override fun execute(status: IotHubStatusCode, context: Any) {
        Log.d(
            TAG,
            "IoT Hub responded to device method operation with status ${status.name}"
        )
    }
}