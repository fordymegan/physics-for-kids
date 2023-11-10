package com.example.PhidgetDigitalOutputExample;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.phidget22.*;

import java.text.DecimalFormat;

public class Angles extends Activity {

    RCServo rc1;
    DigitalOutput b1;

    Button engagedButton;
    SeekBar accelerationBar;
    SeekBar velocityLimitBar;
    SeekBar targetPositionBar;

    Toast errToast;

    double minAcceleration;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.angleview);

        //Hide device information and settings until one is attached
        LinearLayout settingsAndData = (LinearLayout) findViewById(R.id.settingsAndDatar);
        settingsAndData.setVisibility(LinearLayout.GONE);

        //set button functionality
        engagedButton = (Button) findViewById(R.id.engagedButtonr);
        engagedButton.setOnClickListener(new engagedChangeListener());

        //set acceleration seek bar functionality
        accelerationBar = (SeekBar) findViewById(R.id.accelerationBarr);
        accelerationBar.setOnSeekBarChangeListener(new accelerationChangeListener());

        //set velocity limit seek bar functionality
        velocityLimitBar = (SeekBar) findViewById(R.id.velocityLimitBarr);
        velocityLimitBar.setOnSeekBarChangeListener(new velocityLimitChangeListener());

        targetPositionBar = (SeekBar) findViewById(R.id.targetPositionBarr);
        targetPositionBar.incrementProgressBy(1);
        targetPositionBar.setOnSeekBarChangeListener(new targetPositionChangeListener());

        //hide acceleration and velocity controls
        ((LinearLayout)findViewById(R.id.accelerationSectionr)).setVisibility(LinearLayout.GONE);
        ((LinearLayout)findViewById(R.id.velocityLimitSectionr)).setVisibility(LinearLayout.GONE);
        ((LinearLayout)findViewById(R.id.velocityInfor)).setVisibility(LinearLayout.GONE);

        ((TextView)findViewById(R.id.velocityTxtr)).setText("");
        ((TextView)findViewById(R.id.positionTxtr)).setText("");

        try
        {
            rc1 = new RCServo();


            //Allow direct USB connection of Phidgets
//			if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_USB_HOST))
//                com.phidget22.usb.Manager.Initialize(this);

            //Enable server discovery to list remote Phidgets
            this.getSystemService(Context.NSD_SERVICE);
            Net.enableServerDiscovery(ServerType.DEVICE_REMOTE);
//
//            //Add a specific network server to communicate with Phidgets remotely
            Net.addServer("LAPTOP-V64H9NM1", "172.28.160.1", 5661, "", 0); // RPi @ Home

            //Set addressing parameters to specify which channel to open (if any)
            rc1.setDeviceSerialNumber(14875);
            rc1.setIsRemote(true);


            rc1.addAttachListener(new AttachListener() {
                public void onAttach(final AttachEvent attachEvent) {
                    AttachEventHandler handler = new AttachEventHandler(rc1);
                    synchronized(handler)
                    {
                        runOnUiThread(handler);
                        try {
                            handler.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            rc1.addDetachListener(new DetachListener() {
                public void onDetach(final DetachEvent detachEvent) {
                    DetachEventHandler handler = new DetachEventHandler(rc1);
                    synchronized(handler)
                    {
                        runOnUiThread(handler);
                        try {
                            handler.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });



            rc1.addErrorListener(new ErrorListener() {
                public void onError(final ErrorEvent errorEvent) {
                    ErrorEventHandler handler = new ErrorEventHandler(rc1, errorEvent);
                    runOnUiThread(handler);

                }
            });



            rc1.addPositionChangeListener(new RCServoPositionChangeListener() {
                public void onPositionChange(RCServoPositionChangeEvent positionChangeEvent) {
                    RCServoPositionChangeEventHandler handler = new RCServoPositionChangeEventHandler(rc1, positionChangeEvent);
                    runOnUiThread(handler);
                }
            });

            rc1.addVelocityChangeListener(new RCServoVelocityChangeListener() {
                public void onVelocityChange(RCServoVelocityChangeEvent velocityChangeEvent) {
                    RCServoVelocityChangeEventHandler handler = new RCServoVelocityChangeEventHandler(rc1, velocityChangeEvent);
                    runOnUiThread(handler);
                }
            });

            rc1.open();

            b1 = new DigitalOutput();
            b1.setDeviceSerialNumber(38480);
            b1.setChannel(0);
            b1.setIsRemote(true);

            b1.addAttachListener(new AttachListener() {
                public void onAttach(final AttachEvent attachEvent) {
                    AttachEventHandler2 handler = new AttachEventHandler2(b1);
                    synchronized(handler)
                    {
                        runOnUiThread(handler);
                        try {
                            handler.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            b1.addDetachListener(new DetachListener() {
                public void onDetach(final DetachEvent detachEvent) {
                    DetachEventHandler2 handler = new DetachEventHandler2(b1);
                    synchronized(handler)
                    {
                        runOnUiThread(handler);
                        try {
                            handler.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            b1.open();
        } catch (PhidgetException pe) {
            pe.printStackTrace();
        }

    }

    private class engagedChangeListener implements Button.OnClickListener {
        public void onClick(View v) {
            try {
                if(engagedButton.getText() == "Engage") {
                    rc1.setEngaged(true);
                    engagedButton.setText("Disengage");
                }
                else {
                    rc1.setEngaged(false);
                    engagedButton.setText("Engage");
                }
            } catch (PhidgetException e) {
                e.printStackTrace();
            }
        }
    }

    private class accelerationChangeListener implements SeekBar.OnSeekBarChangeListener {
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            if(fromUser) {
                try {
                    TextView accelerationTxt = (TextView) findViewById(R.id.accelerationTxtr);
                    DecimalFormat numberFormat = new DecimalFormat("#.##");
                    double acceleration = ((double) progress / seekBar.getMax()) *
                            (rc1.getMaxAcceleration() - rc1.getMinAcceleration()) + rc1.getMinAcceleration();

                    accelerationTxt.setText(numberFormat.format(acceleration));
                    rc1.setAcceleration(acceleration);
                } catch (PhidgetException e) {
                    e.printStackTrace();
                }
            }
        }

        public void onStartTrackingTouch(SeekBar seekBar) {}

        public void onStopTrackingTouch(SeekBar seekBar) {}
    }

    private class velocityLimitChangeListener implements SeekBar.OnSeekBarChangeListener {
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            if(fromUser) {
                try {
                    TextView velocityLimitTxt = (TextView) findViewById(R.id.velocityLimitTxtr);
                    DecimalFormat numberFormat = new DecimalFormat("#.##");
                    double velocityLimit = ((double) progress / seekBar.getMax()) *
                            (rc1.getMaxVelocityLimit() - rc1.getMinVelocityLimit()) + rc1.getMinVelocityLimit();
                    velocityLimitTxt.setText(numberFormat.format(velocityLimit));
                    rc1.setVelocityLimit(velocityLimit);
                } catch (PhidgetException e) {
                    e.printStackTrace();
                }
            }
        }

        public void onStartTrackingTouch(SeekBar seekBar) {}

        public void onStopTrackingTouch(SeekBar seekBar) {}
    }

    private class targetPositionChangeListener implements SeekBar.OnSeekBarChangeListener {
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            if(fromUser) {
                try {
                    TextView targetPositionTxt = (TextView) findViewById(R.id.targetPositionTxtr);
                    TextView whichPlanet = (TextView) findViewById(R.id.whichPlanetID);
                    double targetPosition = Math.round(((double) progress / seekBar.getMax()) *
                            (rc1.getMaxPosition() - rc1.getMinPosition()) + rc1.getMinPosition());
                    targetPositionTxt.setText(String.valueOf(targetPosition));
                    rc1.setTargetPosition(targetPosition);
                    if(targetPosition == 1){
                        whichPlanet.setText("Mercury");
                        b1.setDutyCycle(1);
                        Thread.sleep(100);
                        b1.setDutyCycle(0);


                    }
                    else if(targetPosition == 3){
                        whichPlanet.setText("Jupiter");
                        b1.setDutyCycle(1);
                        Thread.sleep(200);
                        b1.setDutyCycle(0);
                    }
                    else if(targetPosition == 23){
                        whichPlanet.setText("Earth");
                        b1.setDutyCycle(1);
                        Thread.sleep(200);
                        b1.setDutyCycle(0);
                    }
                    else if(targetPosition == 30){
                        whichPlanet.setText("Neptune");
                        b1.setDutyCycle(1);
                        Thread.sleep(200);
                        b1.setDutyCycle(0);
                    }
                    else if(targetPosition == 25){
                        whichPlanet.setText("Mars");
                        b1.setDutyCycle(1);
                        Thread.sleep(200);
                        b1.setDutyCycle(0);
                    }
                    else if(targetPosition == 98){
                        whichPlanet.setText("Uranus");
                        b1.setDutyCycle(1);
                        Thread.sleep(200);
                        b1.setDutyCycle(0);
                    }
                    else if(targetPosition == 177){
                        whichPlanet.setText("Venus");
                        b1.setDutyCycle(1);
                        Thread.sleep(200);
                        b1.setDutyCycle(0);
                    }
                    else if(targetPosition == 27){
                        whichPlanet.setText("Saturn");
                        b1.setDutyCycle(1);
                        Thread.sleep(200);
                        b1.setDutyCycle(0);
                    }
                    else {
                        whichPlanet.setText("");
                    }
                } catch (PhidgetException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void onStartTrackingTouch(SeekBar seekBar) {}

        public void onStopTrackingTouch(SeekBar seekBar) {}
    }

    class AttachEventHandler implements Runnable {
        Phidget rc1;

        public AttachEventHandler(Phidget rc1) {
            this.rc1 = rc1;
        }

        public void run() {
            LinearLayout settingsAndData = (LinearLayout) findViewById(R.id.settingsAndDatar);
            settingsAndData.setVisibility(LinearLayout.VISIBLE);

            TextView attachedTxt = (TextView) findViewById(R.id.attachedTxtr);

            attachedTxt.setText("Attached");
            try {
                TextView nameTxt = (TextView) findViewById(R.id.nameTxtr);
                TextView serialTxt = (TextView) findViewById(R.id.serialTxtr);
                TextView versionTxt = (TextView) findViewById(R.id.versionTxtr);
                TextView channelTxt = (TextView) findViewById(R.id.channelTxtr);
                TextView hubPortTxt = (TextView) findViewById(R.id.hubPortTxtr);
                TextView labelTxt = (TextView) findViewById(R.id.labelTxtr);

                nameTxt.setText(rc1.getDeviceName());
                serialTxt.setText(Integer.toString(rc1.getDeviceSerialNumber()));
                versionTxt.setText(Integer.toString(rc1.getDeviceVersion()));
                channelTxt.setText(Integer.toString(rc1.getChannel()));
                hubPortTxt.setText(Integer.toString(rc1.getHubPort()));
                labelTxt.setText(rc1.getDeviceLabel());

                SeekBar targetPositionBar = (SeekBar) findViewById(R.id.targetPositionBarr);
                targetPositionBar.setProgress(targetPositionBar.getMax()/2);

                double targetPosition = (((RCServo)rc1).getMaxPosition() - ((RCServo)rc1).getMinPosition())/2
                        + ((RCServo)rc1).getMinPosition();

                TextView targetPositionTxt = (TextView) findViewById(R.id.targetPositionTxtr);
                targetPositionTxt.setText(String.valueOf(targetPosition));

                ((RCServo)rc1).setTargetPosition(targetPosition);

                switch(rc1.getDeviceID()) {
                    case PN_1066:
                    case PN_1061:
                        TextView accelerationTxt = (TextView) findViewById(R.id.accelerationTxtr);
                        accelerationTxt.setText(String.valueOf(((RCServo)rc1).getAcceleration()));

                        SeekBar accelerationBar = (SeekBar) findViewById(R.id.accelerationBarr);
                        accelerationBar.setProgress((int) ((((RCServo)rc1).getAcceleration() - ((RCServo)rc1).getMinAcceleration())
                                / (((RCServo)rc1).getMaxAcceleration() - ((RCServo)rc1).getMinAcceleration()) * accelerationBar.getMax()));

                        TextView velocityLimitTxt = (TextView) findViewById(R.id.velocityLimitTxtr);
                        velocityLimitTxt.setText(String.valueOf(((RCServo)rc1).getVelocityLimit()));

                        SeekBar velocityLimitBar = (SeekBar) findViewById(R.id.velocityLimitBarr);
                        velocityLimitBar.setProgress((int) ((((RCServo)rc1).getVelocityLimit() - ((RCServo)rc1).getMinVelocityLimit())
                                / (((RCServo)rc1).getMaxVelocityLimit() - ((RCServo)rc1).getMinVelocityLimit()) * velocityLimitBar.getMax()));

                        ((LinearLayout)findViewById(R.id.accelerationSectionr)).setVisibility(LinearLayout.VISIBLE);
                        ((LinearLayout)findViewById(R.id.velocityLimitSectionr)).setVisibility(LinearLayout.VISIBLE);
                        ((LinearLayout)findViewById(R.id.velocityInfor)).setVisibility(LinearLayout.VISIBLE);
                        break;
                    default:
                        break;
                }

                engagedButton.setText("Engage");
            } catch (PhidgetException e) {
                e.printStackTrace();
            }

            //notify that we're done
            synchronized(this)
            {
                this.notify();
            }
        }
    }

    class DetachEventHandler implements Runnable {
        Phidget rc1;

        public DetachEventHandler(Phidget rc1) {
            this.rc1 = rc1;
        }

        public void run() {
            LinearLayout settingsAndData = (LinearLayout) findViewById(R.id.settingsAndDatar);

            settingsAndData.setVisibility(LinearLayout.GONE);

            TextView attachedTxt = (TextView) findViewById(R.id.attachedTxtr);
            attachedTxt.setText("Detached");

            TextView nameTxt = (TextView) findViewById(R.id.nameTxtr);
            TextView serialTxt = (TextView) findViewById(R.id.serialTxtr);
            TextView versionTxt = (TextView) findViewById(R.id.versionTxtr);
            TextView channelTxt = (TextView) findViewById(R.id.channelTxtr);
            TextView hubPortTxt = (TextView) findViewById(R.id.hubPortTxtr);
            TextView labelTxt = (TextView) findViewById(R.id.labelTxtr);

            nameTxt.setText(R.string.unknown_val);
            serialTxt.setText(R.string.unknown_val);
            versionTxt.setText(R.string.unknown_val);
            channelTxt.setText(R.string.unknown_val);
            hubPortTxt.setText(R.string.unknown_val);
            labelTxt.setText(R.string.unknown_val);

            //hide acceleration and velocity controls on detach
            ((LinearLayout)findViewById(R.id.accelerationSectionr)).setVisibility(LinearLayout.GONE);
            ((LinearLayout)findViewById(R.id.velocityLimitSectionr)).setVisibility(LinearLayout.GONE);
            ((LinearLayout)findViewById(R.id.velocityInfor)).setVisibility(LinearLayout.GONE);

            ((TextView)findViewById(R.id.velocityTxtr)).setText("");
            ((TextView)findViewById(R.id.positionTxtr)).setText("");

            //notify that we're done
            synchronized(this)
            {
                this.notify();
            }
        }
    }

    class AttachEventHandler2 implements Runnable {
        Phidget ch;

        public AttachEventHandler2(Phidget ch) {
            this.ch = ch;
        }

        public void run() {
        }
    }

    class DetachEventHandler2 implements Runnable {
        Phidget ch;

        public DetachEventHandler2(Phidget ch) {
            this.ch = ch;
        }

        public void run() {
        }
    }


    class ErrorEventHandler implements Runnable {
        Phidget rc1;
        ErrorEvent errorEvent;

        public ErrorEventHandler(Phidget rc1, ErrorEvent errorEvent) {
            this.rc1 = rc1;
            this.errorEvent = errorEvent;
        }

        public void run() {
            if (errToast == null)
                errToast = Toast.makeText(getApplicationContext(), errorEvent.getDescription(), Toast.LENGTH_SHORT);

            //replace the previous toast message if a new error occurs
            errToast.setText(errorEvent.getDescription());
            errToast.show();
        }
    }

    class RCServoPositionChangeEventHandler implements Runnable {
        Phidget rc1;
        RCServoPositionChangeEvent positionChangeEvent;

        public RCServoPositionChangeEventHandler(Phidget rc1, RCServoPositionChangeEvent positionChangeEvent) {
            this.rc1 = rc1;
            this.positionChangeEvent = positionChangeEvent;
        }

        public void run() {
            DecimalFormat numberFormat = new DecimalFormat("#.##");
            TextView positionTxt = (TextView)findViewById(R.id.positionTxtr);
            positionTxt.setText(numberFormat.format(positionChangeEvent.getPosition()));
        }
    }

    class RCServoVelocityChangeEventHandler implements Runnable {
        Phidget rc1;
        RCServoVelocityChangeEvent velocityChangeEvent;

        public RCServoVelocityChangeEventHandler(Phidget rc1, RCServoVelocityChangeEvent velocityChangeEvent) {
            this.rc1 = rc1;
            this.velocityChangeEvent = velocityChangeEvent;
        }

        public void run() {
            DecimalFormat numberFormat = new DecimalFormat("#.##");
            TextView velocityTxt = (TextView)findViewById(R.id.velocityTxtr);
            velocityTxt.setText(numberFormat.format(velocityChangeEvent.getVelocity()));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            rc1.close();
            b1.close();

        } catch (PhidgetException e) {
            e.printStackTrace();
        }

        //Disable USB connection to Phidgets
//    	if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_USB_HOST))
//            com.phidget22.usb.Manager.Uninitialize();
    }

}
