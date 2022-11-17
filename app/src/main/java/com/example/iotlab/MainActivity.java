package com.example.iotlab;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.github.angads25.toggle.model.ToggleableView;
import com.github.angads25.toggle.widget.LabeledSwitch;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.Charset;

public class MainActivity extends AppCompatActivity {

    MQTTHelper mqttHelper;
    TextView txtTemp, txtBrig, txtHumid, txtMois, txtAI;
    LabeledSwitch btnLED, btnPUMP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtTemp = findViewById(R.id.txtTemperature);
        txtBrig = findViewById(R.id.txtBrightness);
        txtHumid = findViewById(R.id.txtHumidity);
        txtMois = findViewById(R.id.txtMoisture);
        txtAI = findViewById(R.id.txtAI);

        btnLED = findViewById(R.id.button1);
        btnPUMP = findViewById(R.id.button2);

        btnLED.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                if (isOn == true) {
                    sendDataMQTT("vynguyen5689/feeds/button1", "1");
                } else sendDataMQTT("vynguyen5689/feeds/button1", "0");
            }
        });
        btnPUMP.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                if (isOn == true) {
                    sendDataMQTT("vynguyen5689/feeds/button2", "1");
                } else sendDataMQTT("vynguyen5689/feeds/button2", "0");
            }
        });
        startMQTT();
    }

    public void startMQTT() {
        mqttHelper = new MQTTHelper(this);
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

            }

            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d("TEST", topic + " receive data " + message.toString());
                if (topic.contains("sensor1")) {
                    txtTemp.setText(message.toString() + "°C");
                } else if (topic.contains("sensor2")) {
                    txtBrig.setText(message.toString() + " lx");
                } else if (topic.contains("sensor3")) {
                    txtMois.setText(message.toString() + " %");
                } else if (topic.contains("sensor4")) {
                    txtHumid.setText(message.toString() + " %");
                } else if (topic.contains("button1")) {
                    if (message.toString().equals("1"))
                        btnLED.setOn(true);
                    else btnLED.setOn(false);
                } else if (topic.contains("button2")) {
                    if (message.toString().equals("1"))
                        btnPUMP.setOn(true);
                    else btnPUMP.setOn(false);
                } else if (topic.contains("ai")) {
                    if (message.toString().contains("0"))
                        txtAI.setText("No mask");
                    else if (message.toString().contains("1"))
                        txtAI.setText("With mask");
                    else txtAI.setText("No subject detected!");
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

    public void sendDataMQTT(String topic, String value){
        MqttMessage msg = new MqttMessage();
        msg.setId(1234);
        msg.setQos(0);
        msg.setRetained(false);

        byte[] b = value.getBytes(Charset.forName("UTF-8"));
        msg.setPayload(b);

        try {
            mqttHelper.mqttAndroidClient.publish(topic, msg);
        } catch (MqttException e){}
    }
}