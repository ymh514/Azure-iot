package com.example.azureiot

import com.microsoft.azure.sdk.iot.device.IotHubClientProtocol

object Configs {
    val CONN_STRING = BuildConfig.DeviceConnectionString
    val PROTOCOL = IotHubClientProtocol.MQTT

    // Method
    val METHOD_SUCCESS = 200
    val METHOD_THROWS = 403
    val METHOD_NOT_DEFINED = 404

}