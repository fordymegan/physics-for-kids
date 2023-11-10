package com.example.PhidgetDigitalOutputExample;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.util.Log;

import com.phidget22.*;

import java.util.Random;

public class Weights extends Activity {

    int[] weightOptions = {20, 30, 40,50, 60, 70, 80, 90, 100};

    VoltageRatioInput force_plate1;
    VoltageRatioInput force_plate2;
    Button generate;
    DigitalOutput green_light;
    DigitalOutput orange_light;
    DigitalOutput red_light;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        Log.d("hello", "on create weights");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.weightview);

        generate = findViewById(R.id.generate);
        generate.setOnClickListener(new stateChangeListenerGen());


        try{
            this.getSystemService(Context.NSD_SERVICE);
            Net.enableServerDiscovery(ServerType.DEVICE_REMOTE);
            Net.addServer("", "", "", "", ""); // RPi @ Home

            // ADD PHIDGETS

            // CHANGE DEVICE PORT NUMBERS WHEN YOU KNOW WHAT THEY ARE



            force_plate1 = new VoltageRatioInput();
            force_plate1.setDeviceSerialNumber(38480);
            force_plate1.setChannel(1);
            force_plate1.setIsHubPortDevice(false);
//            force_button.setHubPort(0);
            force_plate1.setIsRemote(true);

            force_plate1.addAttachListener(new AttachListener() {
                public void onAttach(final AttachEvent attachEvent) {
                    AttachEventHandlerForce handler = new AttachEventHandlerForce(force_plate1);
                    runOnUiThread(handler);
                }
            });

            force_plate1.addDetachListener(new DetachListener() {
                public void onDetach(final DetachEvent detachEvent) {
                    DetachEventHandlerForce handler = new DetachEventHandlerForce(force_plate1);
                    runOnUiThread(handler);

                }
            });

            force_plate1.addVoltageRatioChangeListener(new VoltageRatioInputVoltageRatioChangeListener() {
                public void onVoltageRatioChange(VoltageRatioInputVoltageRatioChangeEvent voltageRatioChangeEvent) {
                    VoltageRatioInputVoltageRatioChangeEventHandler handler = new VoltageRatioInputVoltageRatioChangeEventHandler(force_plate1, voltageRatioChangeEvent);
                    runOnUiThread(handler);
                }
            });

            force_plate1.open();

            force_plate2 = new VoltageRatioInput();
            force_plate2.setDeviceSerialNumber(38480);
            force_plate2.setChannel(2);
            force_plate2.setIsHubPortDevice(false);
//            force_button.setHubPort(0);
            force_plate2.setIsRemote(true);

            force_plate2.addAttachListener(new AttachListener() {
                public void onAttach(final AttachEvent attachEvent) {
                    AttachEventHandlerForce2 handler = new AttachEventHandlerForce2(force_plate2);
                    runOnUiThread(handler);
                }
            });

            force_plate2.addDetachListener(new DetachListener() {
                public void onDetach(final DetachEvent detachEvent) {
                    DetachEventHandlerForce2 handler = new DetachEventHandlerForce2(force_plate2);
                    runOnUiThread(handler);

                }
            });

            force_plate2.addVoltageRatioChangeListener(new VoltageRatioInputVoltageRatioChangeListener() {
                public void onVoltageRatioChange(VoltageRatioInputVoltageRatioChangeEvent voltageRatioChangeEvent2) {
                    VoltageRatioInputVoltageRatioChangeEventHandler2 handler = new VoltageRatioInputVoltageRatioChangeEventHandler2(force_plate2, voltageRatioChangeEvent2);
                    runOnUiThread(handler);
                }
            });

            force_plate2.open();





            green_light = new DigitalOutput();
            green_light.setDeviceSerialNumber(38480);
            green_light.setChannel(7);
            green_light.setIsRemote(true);

            green_light.addAttachListener(new AttachListener() {
                public void onAttach(final AttachEvent attachEvent) {
                    AttachEventHandlerGreen handler = new AttachEventHandlerGreen(green_light);
                    runOnUiThread(handler);
                }
            });

            green_light.addDetachListener(new DetachListener() {
                public void onDetach(final DetachEvent detachEvent) {
                    DetachEventHandlerGreen handler = new DetachEventHandlerGreen(green_light);
                    runOnUiThread(handler);

                }
            });

            green_light.open();

            red_light = new DigitalOutput();
            red_light.setDeviceSerialNumber(38480);
            red_light.setChannel(6);
            red_light.setIsRemote(true);

            green_light.addAttachListener(new AttachListener() {
                public void onAttach(final AttachEvent attachEvent) {
                    AttachEventHandlerRed handler = new AttachEventHandlerRed(green_light);
                    runOnUiThread(handler);
                }
            });

            green_light.addDetachListener(new DetachListener() {
                public void onDetach(final DetachEvent detachEvent) {
                    DetachEventHandlerRed handler = new DetachEventHandlerRed(green_light);
                    runOnUiThread(handler);

                }
            });
            red_light.open();

        } catch (PhidgetException e) {
            e.printStackTrace();
        }

    }

    private class stateChangeListenerGen implements Button.OnClickListener {
        public void onClick(View v) {

            TextView weightValue = findViewById(R.id.weight_int);

            int rnd = new Random().nextInt(weightOptions.length);

            Integer create = weightOptions[rnd];
            weightValue.setText(String.valueOf(create));

            Log.d("mytag", String.valueOf(rnd));
            // turn on rfid reader here too?


        }
    }

    class AttachEventHandlerForce implements Runnable {
        Phidget ch;

        public AttachEventHandlerForce(Phidget ch) {
            this.ch = ch;
        }

        public void run() {
        }
    }
    class DetachEventHandlerForce implements Runnable {
        Phidget ch;

        public DetachEventHandlerForce(Phidget ch) {
            this.ch = ch;
        }

        public void run() {
        }
    }

    class VoltageRatioInputVoltageRatioChangeEventHandler implements Runnable {
        Phidget force_plate1;
        VoltageRatioInputVoltageRatioChangeEvent voltageRatioChangeEvent;

        public VoltageRatioInputVoltageRatioChangeEventHandler(Phidget force_plate1, VoltageRatioInputVoltageRatioChangeEvent voltageRatioChangeEvent) {
            this.force_plate1 = force_plate1;
            this.voltageRatioChangeEvent = voltageRatioChangeEvent;
        }

        @SuppressLint("SuspiciousIndentation")
        public void run() {
            int number;
            double forceValue = voltageRatioChangeEvent.getVoltageRatio();
            TextView sensorValue = findViewById(R.id.sensor_value);
            TextView plate1 = findViewById(R.id.plate1_value);
            forceValue = forceValue*100;
            String force = String.format("%.0f", forceValue);
            sensorValue.setText(force);
            plate1.setText(force);
            TextView weightValue = findViewById(R.id.weight_int);
            CharSequence x = weightValue.getText();
            Log.d("number value", x.toString());
            try {
                if(x!=""){
                    number = Integer.parseInt(x.toString());

                }
                else number = 0;

                    if(forceValue < number+10 && forceValue>number-10){
                        green_light.setDutyCycle(1);
                        Thread.sleep(100);
                        green_light.setDutyCycle(0);
                    }
                    else{
                        red_light.setDutyCycle(1);
                        Thread.sleep(100);
                        red_light.setDutyCycle(0);
                    }

                } catch (PhidgetException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    class AttachEventHandlerGreen implements Runnable {
        Phidget ch;

        public AttachEventHandlerGreen(Phidget ch) {
            this.ch = ch;
        }

        public void run() {
        }
    }
    class DetachEventHandlerGreen implements Runnable {
        Phidget ch;

        public DetachEventHandlerGreen(Phidget ch) {
            this.ch = ch;
        }

        public void run() {
        }
    }

    class AttachEventHandlerRed implements Runnable {
        Phidget ch;

        public AttachEventHandlerRed(Phidget ch) {
            this.ch = ch;
        }

        public void run() {
        }
    }
    class DetachEventHandlerRed implements Runnable {
        Phidget ch;

        public DetachEventHandlerRed(Phidget ch) {
            this.ch = ch;
        }

        public void run() {
        }
    }


    class AttachEventHandlerForce2 implements Runnable {
        Phidget ch;

        public AttachEventHandlerForce2(Phidget ch) {
            this.ch = ch;
        }

        public void run() {
        }
    }
    class DetachEventHandlerForce2 implements Runnable {
        Phidget ch;

        public DetachEventHandlerForce2(Phidget ch) {
            this.ch = ch;
        }

        public void run() {
        }
    }

    class VoltageRatioInputVoltageRatioChangeEventHandler2 implements Runnable {
        Phidget force_plate2;
        VoltageRatioInputVoltageRatioChangeEvent voltageRatioChangeEvent2;

        public VoltageRatioInputVoltageRatioChangeEventHandler2(Phidget force_plate2, VoltageRatioInputVoltageRatioChangeEvent voltageRatioChangeEvent2) {
            this.force_plate2 = force_plate2;
            this.voltageRatioChangeEvent2 = voltageRatioChangeEvent2;
        }

        public void run() {

            double forceValue = voltageRatioChangeEvent2.getVoltageRatio();
            TextView sensorValue = findViewById(R.id.plate2_value);
            forceValue = forceValue*100;
            String force = String.format("%.0f", forceValue);
            sensorValue.setText(force);


        }

}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {

            green_light.close();
            force_plate1.close();
            force_plate2.close();
            red_light.close();
//            lcd.close();
        } catch (PhidgetException e) {
            e.printStackTrace();
        }
    }
}
