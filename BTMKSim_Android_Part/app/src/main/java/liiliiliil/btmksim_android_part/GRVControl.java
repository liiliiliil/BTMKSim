package liiliiliil.btmksim_android_part;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class GRVControl extends AppCompatActivity {

    private static final String TAG = "GRVCTRL_DEBUG_TAG";
    private static final Handler handlerForBluetooth = new Handler() {
        public void handleMessage(Message msg) {
//            Log.i(TAG, "handlemessage obj="+msg.obj);
        }
    };

    private BluetoothService GRVTransferService = new BluetoothService(handlerForBluetooth);
    private SensorManager sensorManager = null;
    private Sensor gameRotationVector = null;
    private boolean isPausing = false;
    private boolean hasReset = false;
    private boolean inEdgeCase_X = false;
    private boolean inEdgeCase_Y = false;
    private float[] originalOrientation = new float[] {0, 0, 0};
    private float[] currentOrientation = new float[] {0, 0, 0};
    private float orientationEdgeCase_X = 0;
    private float orientationEdgeCase_Y = 0;

    private byte station = 0;


    private Button buttonToResetMouse;
    private Button buttonToPause;
    private Button buttonToLeftClick;
    private Button buttonToRightClick;
    private Button buttonToPressW;
    private Button buttonToPressLeftArrow;
    private Button buttonToPressRightArrow;
//    private int testCount = 0;

//    private TextView Azimuth;
//    private TextView Pitch;
//    private TextView Roll;

//    private static final Handler handlerForSensorLisenter = new Handler() {
////        public void handleMessage(Message msg) {
////            Azimuth.setText(Float.toString(tmpOrientation[0]));
////            Pitch.setText(Float.toString(tmpOrientation[1]));
////            Roll.setText(Float.toString(tmpOrientation[2]));
////        }
//    };

    private int numberOfMassages = 0;


    private interface GRVConstants {
        public static final float ORIENTATION_HALF_RANGE_X = (float)0.6;  // You may change the value here
        public static final float ORIENTATION_HALF_RANGE_Y = (float)0.6;  // You may change the value here
        public static final float ORIENTATION_RANGE_X = ORIENTATION_HALF_RANGE_X * 2;
        public static final float ORIENTATION_RANGE_Y = ORIENTATION_HALF_RANGE_Y * 2;
        public static final float ORIENTATION_EDGECASE_X =  (float)Math.PI - ORIENTATION_RANGE_X;
        public static final float ORIENTATION_EDGECASE_Y =  (float)Math.PI - ORIENTATION_RANGE_Y;
        public static final float ORIENTATION_THRESHOLD_X =  (float)0.0007;
        public static final float ORIENTATION_THRESHOLD_Y =  (float)0.0007;
        public static final float TWO_PI = (float)(Math.PI * 2);

        public static final byte STATION_MOVE = 0;
        public static final byte STATION_LEFT_CLICK = 1;
        public static final byte STATION_RIGHT_CLICK = 2;

        public static final byte STATION_PRESS = 5;

//        public static final byte[] MESSAGE_MOVE = new byte[]{STATION_MOVE};
//        public static final byte[] MESSAGE_LEFT_CLICK = new byte[]{STATION_LEFT_CLICK};


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grvcontrol);

        Log.i(TAG, "已创建GRVControl这个Activity");

        GRVTransferService.startConnectedThread(MainActivity.getBluetoothSocket());
//        Azimuth = findViewById(R.id.Azimuth);
//        Pitch = findViewById(R.id.Pitch);
//        Roll = findViewById(R.id.Roll);


        buttonToResetMouse = findViewById(R.id.buttonToResetMouse);
        buttonToResetMouse.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // set original point of orientation window
                originalOrientation[0] = currentOrientation[0] - GRVConstants.ORIENTATION_HALF_RANGE_X;
                originalOrientation[2] = currentOrientation[2] - GRVConstants.ORIENTATION_HALF_RANGE_Y;

                // deal with edge cases
                if (originalOrientation[0] < - Math.PI){
                    originalOrientation[0] += GRVConstants.TWO_PI;
                }
                if (originalOrientation[2] < - Math.PI){
                    originalOrientation[2] += GRVConstants.TWO_PI;
                }

                Log.i(TAG, "originalOrientation[0] is " + originalOrientation[0]);
                Log.i(TAG, "originalOrientation[2] is " + originalOrientation[2]);


                if (originalOrientation[0] > GRVConstants.ORIENTATION_EDGECASE_X){
                    inEdgeCase_X = true;
                    orientationEdgeCase_X = originalOrientation[0] + GRVConstants.ORIENTATION_RANGE_X - GRVConstants.TWO_PI;
                }
                else{
                    inEdgeCase_X = false;
                }
                if (originalOrientation[2] > GRVConstants.ORIENTATION_EDGECASE_Y){
                    inEdgeCase_Y = true;
                    orientationEdgeCase_Y = originalOrientation[2] + GRVConstants.ORIENTATION_RANGE_Y - GRVConstants.TWO_PI;
                }
                else{
                    inEdgeCase_Y = false;
                }

                hasReset = true;
            }
        });

        buttonToPause= findViewById(R.id.buttonToPause);
        buttonToPause.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (isPausing){
                    isPausing = false;
                    buttonToPause.setText("Pause");
                    Log.i(TAG, "Pause!!");
                }
                else{
                    isPausing = true;
                    buttonToPause.setText("Resume");
                    Log.i(TAG, "Resume!!");
                }


            }
        });

        buttonToLeftClick= findViewById(R.id.buttonToLeftClick);
        buttonToLeftClick.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                station = GRVConstants.STATION_LEFT_CLICK;
                Log.i(TAG, "Left click!!");
            }
        });
        buttonToRightClick= findViewById(R.id.buttonToRightClick);
        buttonToRightClick.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                station = GRVConstants.STATION_RIGHT_CLICK;
                Log.i(TAG, "Right click!!");
            }
        });


        buttonToPressW= findViewById(R.id.buttonToPressW);
        buttonToPressW.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                station = GRVConstants.STATION_PRESS;
                byte[] data = new byte[]{station, (byte)'w'};
                GRVTransferService.sendData(data);
                Log.i(TAG, "Press w!!");
                station = GRVConstants.STATION_MOVE;
            }

        });

        buttonToPressLeftArrow= findViewById(R.id.buttonToPressLeftArrow);
        buttonToPressLeftArrow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                station = GRVConstants.STATION_PRESS;
                byte[] data = new byte[]{station, (byte)27};
                GRVTransferService.sendData(data);
                Log.i(TAG, "Press LeftArrow!!");
                station = GRVConstants.STATION_MOVE;
            }

        });
        buttonToPressRightArrow= findViewById(R.id.buttonToPressRightArrow);
        buttonToPressRightArrow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                station = GRVConstants.STATION_PRESS;
                byte[] data = new byte[]{station, (byte)26};
                GRVTransferService.sendData(data);
                Log.i(TAG, "Press RightArrow!!");
                station = GRVConstants.STATION_MOVE;
            }

        });

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gameRotationVector = sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
        GRVListenerThread sensorListenerThread = new GRVListenerThread(sensorManager, gameRotationVector);
        sensorListenerThread.start();

    }


    private class GRVListenerThread extends Thread implements SensorEventListener {
        private final SensorManager sensorManager;
        private final Sensor gameRotationVector;
//        private Handler handler;

        public GRVListenerThread(SensorManager sm, Sensor GRV){
            sensorManager = sm;
            gameRotationVector = GRV;

        }

        public void run(){
            // SENSOR_DELAY_UI, SENSOR_DELAY_NORMAL, SENSOR_DELAY_GAME, SENSOR_DELAY_FASTEST
            sensorManager.registerListener(this, gameRotationVector, SensorManager.SENSOR_DELAY_FASTEST);
        }

        @Override
        public void onSensorChanged(SensorEvent e){
            if (isPausing || station >= GRVConstants.STATION_PRESS){
                return;
            }

            float[] rotationMatrix = new float[16];
            float[] tmpOrientation = new float[3];
            SensorManager.getRotationMatrixFromVector(rotationMatrix, e.values);
            SensorManager.getOrientation(rotationMatrix, tmpOrientation);


            if ((Math.abs(tmpOrientation[0] - currentOrientation[0]) > GRVConstants.ORIENTATION_THRESHOLD_X)
                    && ((Math.abs(tmpOrientation[2] - currentOrientation[2]) > GRVConstants.ORIENTATION_THRESHOLD_Y))){
                currentOrientation = tmpOrientation;

                if (hasReset){
                    // calculate the relative orientation from original point of orientation window
                    float[] relativeOrientation = new float[2];
                    relativeOrientation[0] = currentOrientation[0] - originalOrientation[0];
                    relativeOrientation[1] = currentOrientation[2] - originalOrientation[2];

                    if (inEdgeCase_X && currentOrientation[0] < orientationEdgeCase_X){
                        relativeOrientation[0] += GRVConstants.TWO_PI;
                    }
                    else if (relativeOrientation[0] < 0){
                        relativeOrientation[0] = 0;
                    }
                    else if (relativeOrientation[0] > GRVConstants.ORIENTATION_RANGE_X){
                        relativeOrientation[0] = GRVConstants.ORIENTATION_RANGE_X;
                    }

                    if (inEdgeCase_Y && currentOrientation[2] < orientationEdgeCase_Y){
                        relativeOrientation[1] += GRVConstants.TWO_PI;
                    }
                    else if (relativeOrientation[1] < 0){
                        relativeOrientation[1] = 0;
                    }
                    else if (relativeOrientation[1] > GRVConstants.ORIENTATION_RANGE_Y){
                        relativeOrientation[1] = GRVConstants.ORIENTATION_RANGE_Y;
                    }


                    byte[] data = Util.mergeTwoBytes(new byte[]{station}, Util.floatArrayToBytes(relativeOrientation));
                    //            Log.i(TAG, "length of relativeOrientation is " + relativeOrientation.length);
                    //            Log.i(TAG, "length of data is " + data.length);
                    GRVTransferService.sendData(data);
//                    numberOfMassages += 1;
//                    Log.i(TAG, "numberOfMassages is " + numberOfMassages);
                    if (station != GRVConstants.STATION_MOVE){
                        station = GRVConstants.STATION_MOVE;
                    }

                }

            }


        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }

    }

}
