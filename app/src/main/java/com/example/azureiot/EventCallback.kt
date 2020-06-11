package com.example.azureiot

import android.util.Log
import com.microsoft.azure.sdk.iot.device.IotHubEventCallback
import com.microsoft.azure.sdk.iot.device.IotHubStatusCode

class EventCallback : IotHubEventCallback {
    val TAG = "MainActivity_EventCallback"
    private var receiptsConfirmedCount = 0
    private var sendFailuresCount = 0

    override fun execute(status: IotHubStatusCode, context: Any) {
        val i = if (context is Int) context as Int else 0

        Log.d(TAG,"IoT Hub responded to message " + i!!.toString()
                + " with status " + status.name)

        if ((status == IotHubStatusCode.OK) || (status == IotHubStatusCode.OK_EMPTY)) {
            Log.d(
                TAG,
                "txtReceiptsConfirmedVal receiptsConfirmedCount: $receiptsConfirmedCount"
            )

            receiptsConfirmedCount++
        } else {
            sendFailuresCount++
            Log.d(
                TAG,
                "txtReceiptsConfirmedVal sendFailuresCount: $sendFailuresCount"
            )
        }
    }
}