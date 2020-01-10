package com.craxiom.networksurvey.azure;

import android.util.Log;

import com.microsoft.azure.sdk.iot.device.IotHubConnectionStatusChangeCallback;
import com.microsoft.azure.sdk.iot.device.IotHubConnectionStatusChangeReason;
import com.microsoft.azure.sdk.iot.device.transport.IotHubConnectionStatus;

/**
 * Copied this class from the Microsoft Azure IoT Android example app.  TODO Update this class for the specific needs of this app
 */
class IotHubConnectionStatusChangeCallbackLogger implements IotHubConnectionStatusChangeCallback
{
    private static final String LOG_TAG = AzureRecordBroker.class.getSimpleName();

    @Override
    public void execute(IotHubConnectionStatus status, IotHubConnectionStatusChangeReason statusChangeReason, Throwable throwable, Object callbackContext)
    {
        Log.i(LOG_TAG, "");
        Log.i(LOG_TAG, "CONNECTION STATUS UPDATE: " + status);
        Log.i(LOG_TAG, "CONNECTION STATUS REASON: " + statusChangeReason);
        Log.i(LOG_TAG, "CONNECTION STATUS THROWABLE: " + (throwable == null ? "null" : throwable.getMessage()));
        Log.i(LOG_TAG, "");

        if (throwable != null)
        {
            throwable.printStackTrace();
        }

        if (status == IotHubConnectionStatus.DISCONNECTED)
        {
            //connection was lost, and is not being re-established. Look at provided exception for
            // how to resolve this issue. Cannot send messages until this issue is resolved, and you manually
            // re-open the device client
        } else if (status == IotHubConnectionStatus.DISCONNECTED_RETRYING)
        {
            //connection was lost, but is being re-established. Can still send messages, but they won't
            // be sent until the connection is re-established
        } else if (status == IotHubConnectionStatus.CONNECTED)
        {
            //Connection was successfully re-established. Can send messages.
        }
    }
}