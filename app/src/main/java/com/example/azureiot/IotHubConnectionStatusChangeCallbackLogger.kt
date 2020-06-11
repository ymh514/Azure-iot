package com.example.azureiot

import android.util.Log
import com.microsoft.azure.sdk.iot.device.IotHubConnectionStatusChangeCallback
import com.microsoft.azure.sdk.iot.device.IotHubConnectionStatusChangeReason
import com.microsoft.azure.sdk.iot.device.transport.IotHubConnectionStatus

class IotHubConnectionStatusChangeCallbackLogger : IotHubConnectionStatusChangeCallback {
    private val TAG = "MainActivity_IotHubConnectionStatusChangeCallbackLogger"

    override fun execute(
        status: IotHubConnectionStatus,
        statusChangeReason: IotHubConnectionStatusChangeReason,
        throwable: Throwable?,
        callbackContext: Any
    ) {
        Log.d(TAG, "----------------------------------")
        Log.d(TAG, "CONNECTION STATUS UPDATE: $status")
        Log.d(TAG, "CONNECTION STATUS REASON: $statusChangeReason")
        Log.d(
            TAG,
            "CONNECTION STATUS THROWABLE: \" + if (throwable == null) \"null\" else throwable.message"
        )
        Log.d(TAG, "----------------------------------")

        throwable?.printStackTrace()

        when (status) {
            IotHubConnectionStatus.DISCONNECTED -> {
                //connection was lost, and is not being re-established. Look at provided exception for
                // how to resolve this issue. Cannot send messages until this issue is resolved, and you manually
                // re-open the device client

                Log.d(TAG,"IotHubConnectionStatus.DISCONNECTED")

            }
            IotHubConnectionStatus.DISCONNECTED_RETRYING -> {
                //connection was lost, but is being re-established. Can still send messages, but they won't
                // be sent until the connection is re-established
                Log.d(TAG,"IotHubConnectionStatus.DISCONNECTED_RETRYING")

            }
            IotHubConnectionStatus.CONNECTED -> {
                //Connection was successfully re-established. Can send messages.
                Log.d(TAG,"IotHubConnectionStatus.CONNECTED")

            }

        }
    }
}