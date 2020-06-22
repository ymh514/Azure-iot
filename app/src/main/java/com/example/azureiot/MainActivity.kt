package com.example.azureiot

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.microsoft.azure.sdk.iot.device.*
import com.microsoft.azure.sdk.iot.device.DeviceTwin.Property
import com.microsoft.azure.sdk.iot.device.DeviceTwin.TwinPropertyCallBack
import com.microsoft.azure.sdk.iot.device.transport.ExponentialBackoffWithJitter
import io.reactivex.Completable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.net.URISyntaxException


class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private var client: DeviceClient? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            Log.d(TAG, "Btn1 click")

            val completable = getCompletable()

            var msgSentCount = 0

            completable
                .subscribeOn(Schedulers.newThread())
                .subscribeBy(  // named arguments for lambda Subscribers
                    onError = {
                        Log.d(TAG, "Error: ${it.message}")
                        it.printStackTrace()
                    },
                    onComplete = {
                        Log.d(TAG, "After init client")

                        val message = "Hello from jc device"
                        try {

                            val sendMessage = Message(message)
                            sendMessage.setProperty("testAlert", "true")
                            val messageId = java.util.UUID.randomUUID().toString()
                            Log.d(TAG, "message id: $messageId")

                            sendMessage.setMessageId(messageId)
                            Log.d(TAG, "Message Sent: $sendMessage")
                            val eventCallback = EventCallback()
                            client?.sendEventAsync(sendMessage, eventCallback, msgSentCount)
                            msgSentCount++
                        } catch (e: Exception) {
                            Log.e(TAG, "Exception while sending event: $e")
                        }
                    }
                )
        }

        button2.setOnClickListener {
            Log.d(TAG, "Btn2 click")
            client?.closeNow()
            Log.d(TAG, "Shutting down...")
        }
    }

    private fun getCompletable(): Completable {
        return Completable.create {
            Log.d(TAG, "Create observable")

            initClient()
            Thread.sleep(2000)

            when (true) {
                true -> it.onComplete()
                false -> it.onError(Throwable("Error"))
            }
        }
    }

    // Azure iot
    @Throws(URISyntaxException::class, IOException::class)
    private fun initClient() {

        client = DeviceClient(Configs.CONN_STRING, Configs.PROTOCOL)
        try {

            client?.registerConnectionStatusChangeCallback(
                IotHubConnectionStatusChangeCallbackLogger(),
                Any()
            )
            val callback = MessageCallback()
            client?.setMessageCallback(callback, null)

            client?.setRetryPolicy(ExponentialBackoffWithJitter(3, 100, 10 * 100, 100, true))

            client?.open() // MARK: open before subscribe

            Log.d(TAG,"After open")

            client?.subscribeToDeviceMethod(
                SampleDeviceMethodCallback(),
                applicationContext, DeviceMethodStatusCallBack(), null
            )
            Log.d(TAG,"After subscribeToDeviceMethod")

//            // MARK: get twin data
//            try {
//                client?.startDeviceTwin(DeviceTwinStatusCallBack(), null, OnProperty(), null)
//                val onDesiredPropertyChange: Map<Property, com.microsoft.azure.sdk.iot.device.DeviceTwin.Pair<TwinPropertyCallBack, Any?>> =
//                    object :
//                        HashMap<Property, com.microsoft.azure.sdk.iot.device.DeviceTwin.Pair<TwinPropertyCallBack, Any?>>() {
//                        init {
//                            put(
//                                Property("location", null),
//                                com.microsoft.azure.sdk.iot.device.DeviceTwin.Pair<TwinPropertyCallBack, Any?>(
//                                    OnProperty(),
//                                    null
//                                )
//                            )
//                        }
//                    }
//
//                client?.subscribeToTwinDesiredProperties(onDesiredPropertyChange)
////                client?.getDeviceTwin()
//                // FIXME: seems subscribe 2 times
//
//            } catch (e: java.lang.Exception) {
//                e.printStackTrace()
//                Log.d(TAG, "Eception: $e")
//            }
//
//            // MARK: report data with twin
//            try {
//                Log.d(TAG, "Update reported properties...")
//                val ss = SimpleGpsCoordinate(30.0, 10.0)
//
//                val reportProperties: Set<Property> = setOf(Property("location", ss))
//                client?.sendReportedProperties(reportProperties)
//                Log.d(TAG, "Update reported properties done")
//
//            } catch (e: java.lang.Exception) {
//                e.printStackTrace()
//                Log.d(TAG, "Eception: $e")
//            }
//            //
        } catch (e: Exception) {
            Log.e(TAG, "Exception while opening IoTHub connection: $e")
            client?.closeNow()
            Log.d(TAG, "Shutting down...")
        }

    }

    protected class DeviceTwinStatusCallBack : IotHubEventCallback {
        val TAG = "MainActivity_DeviceTwinStatusCallBack"
        override fun execute(responseStatus: IotHubStatusCode?, callbackContext: Any?) {
            Log.d(
                TAG,
                "IoT Hub responded to device twin operation with status \" + ${responseStatus?.name}"
            )
        }
//        override fun execute(status: IotHubStatusCode, context: Any) {
//            Log.d(TAG,"IoT Hub responded to device twin operation with status \" + ${status.name}")
//        }
    }

    protected class OnProperty : TwinPropertyCallBack {
        val TAG = "MainActivity_OnProperty"

        override fun TwinPropertyCallBack(property: Property?, context: Any?) {

            Log.d(TAG, "TwinPropertyCallBack: $property, context: $context")

            property?.let {
                Log.d(TAG, "Last update: ${it.lastUpdated}")
                Log.d(TAG, "Last update version: ${it.lastUpdatedVersion}")
                Log.d(TAG, "isReported: ${it.isReported}")
                Log.d(TAG, "version: ${it.version}")

                if (!it.getIsReported()) {
                    // Desired
                    Log.d(TAG, "getIsReported")

                    Log.d(TAG, "Lastupdate: ${it.lastUpdated}")
                    Log.d(TAG, "Lastversion: ${it.lastUpdatedVersion}")

                    if (it.getKey().equals("location")) {
                        Log.d(TAG, "equals(\"location\")")
                        val value = it.value
                        Log.d(TAG, "location:$value")
                    }

                }
            }


        }
    }

    internal inner class MessageCallback : com.microsoft.azure.sdk.iot.device.MessageCallback {
        override fun execute(msg: Message, context: Any): IotHubMessageResult {
            Log.d(
                TAG,
                "Received message with content: " + String(
                    msg.bytes,
                    Message.DEFAULT_IOTHUB_MESSAGE_CHARSET
                )
            )
//            msgReceivedCount++

            Log.d(
                TAG, "[" + String(
                    msg.bytes,
                    Message.DEFAULT_IOTHUB_MESSAGE_CHARSET
                ) + "]"
            )

            return IotHubMessageResult.COMPLETE
        }
    }

}
