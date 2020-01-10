package com.craxiom.networksurvey.azure;

import android.content.Context;
import android.util.Log;

import com.craxiom.networksurvey.BuildConfig;
import com.craxiom.networksurvey.listeners.ISurveyRecordListener;
import com.craxiom.networksurvey.messaging.CdmaRecord;
import com.craxiom.networksurvey.messaging.GsmRecord;
import com.craxiom.networksurvey.messaging.LteRecord;
import com.craxiom.networksurvey.messaging.UmtsRecord;
import com.microsoft.azure.sdk.iot.device.DeviceClient;
import com.microsoft.azure.sdk.iot.device.DeviceTwin.DeviceMethodCallback;
import com.microsoft.azure.sdk.iot.device.DeviceTwin.DeviceMethodData;
import com.microsoft.azure.sdk.iot.device.IotHubClientProtocol;
import com.microsoft.azure.sdk.iot.device.IotHubEventCallback;
import com.microsoft.azure.sdk.iot.device.IotHubMessageResult;
import com.microsoft.azure.sdk.iot.device.IotHubStatusCode;
import com.microsoft.azure.sdk.iot.device.Message;
import com.microsoft.azure.sdk.iot.device.MessageCallback;

import java.io.IOException;
import java.net.URISyntaxException;

public class AzureRecordBroker implements ISurveyRecordListener
{
    private static final String LOG_TAG = AzureRecordBroker.class.getSimpleName();

    private static final String CONNECTION_STRING = BuildConfig.DeviceConnectionString;
    private static final IotHubClientProtocol PROTOCOL = IotHubClientProtocol.MQTT;

    private Context applicationContext;

    private DeviceClient client;

    public AzureRecordBroker(Context applicationContext)
    {
        this.applicationContext = applicationContext;

        try
        {
            initClient();
        } catch (Exception e)
        {
            Log.wtf(LOG_TAG, "Could not initialize the Azure IoT Client.  Messages will not be sent to Azure");
        }
    }

    @Override
    public void onGsmSurveyRecord(GsmRecord gsmRecord)
    {

    }

    @Override
    public void onCdmaSurveyRecord(CdmaRecord cdmaRecord)
    {

    }

    @Override
    public void onUmtsSurveyRecord(UmtsRecord umtsRecord)
    {

    }

    @Override
    public void onLteSurveyRecord(LteRecord lteRecord)
    {
        final Message azureMessage = new Message(lteRecord.toString());
        final String messageId = lteRecord.getMissionId() + lteRecord.getRecordNumber();  // TODO This message ID is not working, likely because of invalid characters
        azureMessage.setProperty("temperatureAlert", "false");
        final String recordNumber = String.valueOf(lteRecord.getRecordNumber());
        azureMessage.setMessageId(recordNumber);

        client.sendEventAsync(azureMessage, new EventCallback(), recordNumber);
    }

    /**
     * Initialize the client connection to the Azure IoT Hub.
     */
    private void initClient() throws URISyntaxException, IOException
    {
        client = new DeviceClient(CONNECTION_STRING, PROTOCOL);

        try
        {
            client.registerConnectionStatusChangeCallback(new IotHubConnectionStatusChangeCallbackLogger(), new Object());
            client.open();
            MessageCallback callback = new SurveyMessageCallback();
            client.setMessageCallback(callback, null);
            client.subscribeToDeviceMethod(new SampleDeviceMethodCallback(), applicationContext, new DeviceMethodStatusCallBack(), null);
        } catch (Exception e)
        {
            System.err.println("Exception while opening IoTHub connection: " + e);
            client.closeNow();
            System.out.println("Shutting down...");
        }
    }

    class EventCallback implements IotHubEventCallback
    {
        public void execute(IotHubStatusCode status, Object context)
        {
            /*Integer i = context instanceof Integer ? (Integer) context : 0;
            System.out.println("IoT Hub responded to message " + i.toString()
                    + " with status " + status.name());*/

            Log.d(LOG_TAG, "Received event callback: status=" + status + " context=" + context);

            if ((status == IotHubStatusCode.OK) || (status == IotHubStatusCode.OK_EMPTY))
            {
                /*TextView txtReceiptsConfirmedVal = findViewById(R.id.txtReceiptsConfirmedVal);
                receiptsConfirmedCount++;
                txtReceiptsConfirmedVal.setText(Integer.toString(receiptsConfirmedCount));*/
            } else
            {
                /*TextView txtSendFailuresVal = findViewById(R.id.txtSendFailuresVal);
                sendFailuresCount++;
                txtSendFailuresVal.setText(Integer.toString(sendFailuresCount));*/
            }
        }
    }

    /**
     * The callback from Azure indicating that the message was received.  TODO This might be a good place to mark the record as synced in the geopackage file.
     */
    class SurveyMessageCallback implements MessageCallback
    {
        public IotHubMessageResult execute(Message msg, Object context)
        {
            Log.d(LOG_TAG, "Received message with content: " + new String(msg.getBytes(), Message.DEFAULT_IOTHUB_MESSAGE_CHARSET));

            // TODO Do something here

            return IotHubMessageResult.COMPLETE;
        }
    }

    protected class DeviceMethodStatusCallBack implements IotHubEventCallback
    {
        public void execute(IotHubStatusCode status, Object context)
        {
            Log.i(LOG_TAG, "IoT Hub responded to device method operation with status " + status.name());
        }
    }

    protected class SampleDeviceMethodCallback implements DeviceMethodCallback
    {
        @Override
        public DeviceMethodData call(String methodName, Object methodData, Object context)
        {
            final DeviceMethodData deviceMethodData = new DeviceMethodData(1, "Fake Response Message");

            // TODO Do something here to handle the response to any device calls
            /*try
            {
                switch (methodName)
                {
                    case "setSendMessagesInterval":
                    {
                        int status = method_setSendMessagesInterval(methodData);
                        deviceMethodData = new DeviceMethodData(status, "executed " + methodName);
                        break;
                    }
                    default:
                    {
                        int status = method_default(methodData);
                        deviceMethodData = new DeviceMethodData(status, "executed " + methodName);
                    }
                }
            } catch (Exception e)
            {
                int status = METHOD_THROWS;
                deviceMethodData = new DeviceMethodData(status, "Method Throws " + methodName);
            }*/
            return deviceMethodData;
        }
    }
}
