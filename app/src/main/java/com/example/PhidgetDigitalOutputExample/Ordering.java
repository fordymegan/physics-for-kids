package com.example.PhidgetDigitalOutputExample;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.util.Log;

import com.phidget22.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Hashtable;

public class Ordering extends Activity {
    List<String> scanOrder = new ArrayList<String>();
    List<String> planetOrder = new ArrayList<String>();
    List<String> checkOrder = new ArrayList<String>();
    List<String> results = new ArrayList<String>();

    List<String> sizeOrder = new ArrayList<String>(Arrays.asList("jupiter", "saturn", "uranus", "neptune", "earth", "venus", "mars", "mercury"));
    List<String> distOrder = new ArrayList<String>(Arrays.asList("mercury", "venus", "earth", "mars", "jupiter", "saturn", "uranus", "neptune"));
    List<String> gOrder = new ArrayList<String>(Arrays.asList("jupiter", "neptune", "earth", "saturn", "venus", "uranus", "mars", "mercury"));

    RFID rfid;
    DigitalOutput green_light;
    DigitalOutput red_light;
    VoltageRatioInput force_button;
    LCD lcd;

    Button game1;
    Button game2;
    Button game3;
    Button clear;
    Integer attempts;

    public static Hashtable<String, String> planets
            = new Hashtable<String, String>();



    @Override
    public void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.orderview);

        TextView gameTitle = findViewById(R.id.game_title);
        gameTitle.setText("You are playing the first mission! \n Which is to order the planets! \n by size \n " +
                "by distance from the sun \n by gravitational field strength \n When you have got all three orders correct," +
                "you can move onto the next mission! \n");

        attempts = 0;

        //setting buttons up to set planet orders correctly
        game1 = findViewById(R.id.game_one);
        game1.setOnClickListener(new stateChangeListener_g1());
        game2 = findViewById(R.id.game_two);
        game2.setOnClickListener(new stateChangeListener_g2());
        game3 = findViewById(R.id.game_three);
        game3.setOnClickListener(new stateChangeListener_g3());
        clear = findViewById(R.id.clear);
        clear.setOnClickListener(new stateChangeListener_clear());
        try {


            this.getSystemService(Context.NSD_SERVICE);
            Net.enableServerDiscovery(ServerType.DEVICE_REMOTE);
            Net.addServer("", "", "", "", ""); // RPi @ Home




            force_button = new VoltageRatioInput();
            force_button.setDeviceSerialNumber(38480);
            force_button.setChannel(0);
            force_button.setIsHubPortDevice(false);
//            force_button.setHubPort(0);
            force_button.setIsRemote(true);

            force_button.addAttachListener(new AttachListener() {
                public void onAttach(final AttachEvent attachEvent) {
                    AttachEventHandler_force_button handler = new AttachEventHandler_force_button(force_button);
                    runOnUiThread(handler);
                }
            });

            force_button.addDetachListener(new DetachListener() {
                public void onDetach(final DetachEvent detachEvent) {
                    DetachEventHandler_force_button handler = new DetachEventHandler_force_button(force_button);
                    runOnUiThread(handler);

                }
            });

            force_button.addVoltageRatioChangeListener(new VoltageRatioInputVoltageRatioChangeListener() {
                public void onVoltageRatioChange(VoltageRatioInputVoltageRatioChangeEvent voltageRatioChangeEvent) {
                    VoltageRatioInputVoltageRatioChangeEventHandler handler = new VoltageRatioInputVoltageRatioChangeEventHandler(force_button, voltageRatioChangeEvent);
                    runOnUiThread(handler);
                }
            });

            force_button.open();

            rfid = new RFID();
            rfid.setDeviceSerialNumber(25232);
            rfid.setIsRemote(true);

            rfid.addAttachListener(new AttachListener() {
                public void onAttach(final AttachEvent attachEvent) {
                    AttachEventHandler_rfid handler = new AttachEventHandler_rfid(rfid);
                    runOnUiThread(handler);
                }
            });

            rfid.addDetachListener(new DetachListener() {
                public void onDetach(final DetachEvent detachEvent) {
                    DetachEventHandler_rfid handler = new DetachEventHandler_rfid(rfid);
                    runOnUiThread(handler);

                }
            });

            rfid.addTagListener(new RFIDTagListener() {
                public void onTag(RFIDTagEvent tagEvent) {
                    RFIDTagEventHandler handler = new RFIDTagEventHandler(rfid, tagEvent);
                    runOnUiThread(handler);
                }
            });

            rfid.open();
            rfid.setAntennaEnabled(false);

        } catch (PhidgetException e) {
            e.printStackTrace();
        }


        planets.put("0102389919", "jupiter");
        planets.put("010693530c", "saturn");
        planets.put("0106935eb3", "uranus");
        planets.put("01069342eb", "mars");
        planets.put("01069345ef", "venus");
        planets.put("0102388988", "neptune");
        planets.put("01069341ed", "earth");
        planets.put("01023882b1", "mercury");



    }


    private class stateChangeListener_g1 implements Button.OnClickListener {
        public void onClick(View v) {

            TextView gameSelection = findViewById(R.id.game_selection);
            gameSelection.setText("Order by size (largest to smallest)");

            //counting number of attempts, may not be necessary
            attempts = attempts +1;
            if(planetOrder.size() > 0){
                planetOrder.clear();
            }
            // getting correct planet order for game one
            if(planetOrder.size()==0){
                for (int i = 0; i < sizeOrder.size(); i++){
                    String planet = sizeOrder.get(i);
                    planetOrder.add(planet);
                }
            }

            Log.d("mytag", String.valueOf(planetOrder));
            // turn on rfid reader here too?


        }
    }

    private class stateChangeListener_g2 implements Button.OnClickListener {
        public void onClick(View v) {

            TextView gameSelection = findViewById(R.id.game_selection);
            gameSelection.setText("Order by distance from the Sun (closest first)");

            //counting number of attempts, may not be necessary
            attempts = attempts +1;
            if(planetOrder.size() > 0){
                planetOrder.clear();
            }
            // getting correct planet order for game one
            if(planetOrder.size()==0){
                for (int i = 0; i < distOrder.size(); i++){
                    String planet = distOrder.get(i);
                    planetOrder.add(planet);
                }
            }

            Log.d("mytag", String.valueOf(planetOrder));
            // turn on rfid reader here too?


        }
    }
    private class stateChangeListener_g3 implements Button.OnClickListener {
        public void onClick(View v) {

            TextView gameSelection = findViewById(R.id.game_selection);
            gameSelection.setText("Order by Gravitational field strength (largest to smallest)");

            //counting number of attempts, may not be necessary
            attempts = attempts +1;
            if(planetOrder.size() > 0){
                planetOrder.clear();
            }
            // getting correct planet order for game one
            if(planetOrder.size()==0){
                for (int i = 0; i < gOrder.size(); i++){
                    String planet = gOrder.get(i);
                    planetOrder.add(planet);
                }
            }

            Log.d("mytag", String.valueOf(planetOrder));
            // turn on rfid reader here too?


        }
    }

    private class stateChangeListener_clear implements Button.OnClickListener {
        public void onClick(View v) {

            planetOrder.clear();
            scanOrder.clear();
            checkOrder.clear();
            try {
                rfid.setAntennaEnabled(false);
            } catch (PhidgetException e) {
                e.printStackTrace();
            }

            View p1 = findViewById(R.id.planet_one);
            View p2 = findViewById(R.id.planet_two);
            View p3 = findViewById(R.id.planet_three);
            View p4 = findViewById(R.id.planet_four);
            View p5 = findViewById(R.id.planet_five);
            View p6 = findViewById(R.id.planet_six);
            View p7 = findViewById(R.id.planet_seven);
            View p8 = findViewById(R.id.planet_eight);

            p1.setBackgroundColor(Color.BLACK);
            p2.setBackgroundColor(Color.BLACK);
            p3.setBackgroundColor(Color.BLACK);
            p4.setBackgroundColor(Color.BLACK);
            p5.setBackgroundColor(Color.BLACK);
            p6.setBackgroundColor(Color.BLACK);
            p7.setBackgroundColor(Color.BLACK);
            p8.setBackgroundColor(Color.BLACK);



            Log.d("clearing", "clearing");

        }
    }

    class AttachEventHandler_rfid implements Runnable {
        Phidget ch;

        public AttachEventHandler_rfid(Phidget ch) {
            this.ch = ch;
        }

        public void run() {
        }
    }

    class DetachEventHandler_rfid implements Runnable {
        Phidget ch;

        public DetachEventHandler_rfid(Phidget ch) {
            this.ch = ch;
        }

        public void run() {
        }
    }

    class RFIDTagEventHandler implements Runnable{
        Phidget rfid;
        RFIDTagEvent tagEvent;
        public RFIDTagEventHandler(Phidget rfid, RFIDTagEvent tagEvent) {
            this.rfid = rfid;
            this.tagEvent = tagEvent;
        }
        public void run() {

            String tagName = tagEvent.getTag();
            Log.d("mytag4", tagName);
            String planetScanned = tagToPlanet(tagName);
            Log.d("mytag5", planetScanned);
            scanOrder.add(planetScanned);
            Log.d("mytag6", String.valueOf(scanOrder));

            if(planetScanned == planetOrder.get(scanOrder.size()-1)){
                checkOrder.add("true");
                Log.d("mytag7", String.valueOf(checkOrder));
            }
            else if(planetScanned != planetOrder.get(scanOrder.size()-1)){
                checkOrder.add("false");
                Log.d("mytag8", String.valueOf(checkOrder));
            }
            // do red and green light code here

            // checking planet order
            if(scanOrder.size() == 8){
                                checkOrderPlanets((ArrayList) checkOrder);

            }
        }
    }

    String tagToPlanet(String tag){

        String planet = planets.get(tag);
        return planet;

    }

    void checkOrderPlanets(ArrayList checkOrder){

        try {
            rfid.setAntennaEnabled(false);
            Log.d("mytag3", "yeah");
        } catch (PhidgetException e) {
            e.printStackTrace();
        }

        View p1 = findViewById(R.id.planet_one);
        View p2 = findViewById(R.id.planet_two);
        View p3 = findViewById(R.id.planet_three);
        View p4 = findViewById(R.id.planet_four);
        View p5 = findViewById(R.id.planet_five);
        View p6 = findViewById(R.id.planet_six);
        View p7 = findViewById(R.id.planet_seven);
        View p8 = findViewById(R.id.planet_eight);

        boolean allEqual = true;
        for (Object s : checkOrder) {
            if(!s.equals(checkOrder.get(0)))
                allEqual = false;
        }
        if(allEqual == true ){
            results.add("true");
        }
        if (results.size()==3){
            TextView results = findViewById(R.id.endResult);
            results.setText("Well done, you got all three orders correct! \n Now move onto the next game!");
        }

        if(checkOrder.get(0) == "true"){
                p1.setBackgroundColor(Color.GREEN);
            }
            else{
            p1.setBackgroundColor(Color.RED);
            }
        if(checkOrder.get(1) == "true"){
            p2.setBackgroundColor(Color.GREEN);
        }
        else{
            p2.setBackgroundColor(Color.RED);
        }
        if(checkOrder.get(2) == "true"){
            p3.setBackgroundColor(Color.GREEN);
        }
        else{
            p3.setBackgroundColor(Color.RED);
        }
        if(checkOrder.get(3) == "true"){
            p4.setBackgroundColor(Color.GREEN);
        }
        else{
            p4.setBackgroundColor(Color.RED);
        }
        if(checkOrder.get(4) == "true"){
            p5.setBackgroundColor(Color.GREEN);
        }
        else{
            p5.setBackgroundColor(Color.RED);
        }
        if(checkOrder.get(5) == "true"){
            p6.setBackgroundColor(Color.GREEN);
        }
        else{
            p6.setBackgroundColor(Color.RED);
        }
        if(checkOrder.get(6) == "true"){
            p7.setBackgroundColor(Color.GREEN);
        }
        else{
            p7.setBackgroundColor(Color.RED);
        }
        if(checkOrder.get(7) == "true"){
            p8.setBackgroundColor(Color.GREEN);
        }
        else{
            p8.setBackgroundColor(Color.RED);
        }
    }

    class AttachEventHandler_force_button implements Runnable {
        Phidget ch;

        public AttachEventHandler_force_button(Phidget ch) {
            this.ch = ch;
        }

        public void run() {
        }
    }
    class DetachEventHandler_force_button implements Runnable {
        Phidget ch;

        public DetachEventHandler_force_button(Phidget ch) {
            this.ch = ch;
        }

        public void run() {
        }
    }

    class VoltageRatioInputVoltageRatioChangeEventHandler implements Runnable {
        Phidget force_button;
        VoltageRatioInputVoltageRatioChangeEvent voltageRatioChangeEvent;

        public VoltageRatioInputVoltageRatioChangeEventHandler(Phidget force_button, VoltageRatioInputVoltageRatioChangeEvent voltageRatioChangeEvent) {
            this.force_button = force_button;
            this.voltageRatioChangeEvent = voltageRatioChangeEvent;
        }

        public void run() {

            double forceValue = voltageRatioChangeEvent.getVoltageRatio();
            if(forceValue>= 0.1){
                try {
                    rfid.setAntennaEnabled(true);
                    Log.d("mytag3", "yeah");
                } catch (PhidgetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {

            rfid.close();
            force_button.close();
//            lcd.close();
        } catch (PhidgetException e) {
            e.printStackTrace();
        }
    }

}


