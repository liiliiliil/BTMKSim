package liiliiliil.btmksim_android_part;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {


    private static final int REQUEST_ENABLE_BT = 1;
    private static BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private static BluetoothDevice targetDevice = null;
    private static BluetoothSocket mySocket = null;

    private Button buttonToBTConnectionPage;
    private Button buttonToGRVControlPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            System.exit(1);
        }

        buttonToBTConnectionPage = findViewById(R.id.buttonToBTConnectionPage);
        buttonToBTConnectionPage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent connectBTIntent = new Intent(MainActivity.this, BluetoothConnection.class);
                startActivity(connectBTIntent);
            }
        });

        buttonToGRVControlPage = findViewById(R.id.buttonToGRVControlPage);
        buttonToGRVControlPage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent connectGRVControl = new Intent(MainActivity.this, GRVControl.class);
                startActivity(connectGRVControl);
            }
        });


        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT){
            if (resultCode == RESULT_OK){
                Log.i("Log.i: ", "Bluetooth Enable Successful");
            }
            else if (resultCode == RESULT_CANCELED){
                Log.i("Log.i: ", "Bluetooth Enable Failed");
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }

    public static BluetoothAdapter getBluetoothAdapter(){
        return bluetoothAdapter;
    }

    public static void setBluetoothDevice(BluetoothDevice choosenDevice){
        targetDevice = choosenDevice;
    }

    public static void setBluetoothSocket(BluetoothSocket connectedSocket){
        mySocket = connectedSocket;
    }

    public static BluetoothSocket getBluetoothSocket(){
        return mySocket;
    }

}
