package liiliiliil.btmksim_android_part;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

public class BluetoothConnection extends AppCompatActivity {

    private static final String TAG = "InBTConnection";

    private static final UUID MY_UUID = UUID.fromString("59d45e75-9b1f-465b-a6b5-08e68103a228");

    private static BluetoothAdapter bluetoothAdapter = null;

    private ListView devicesListView = null;
    private ArrayAdapter<String> listAdapter = null;

    private List<String> deviceNames = new ArrayList<>();
    private Map<String, BluetoothDevice> deviceInfos = new TreeMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_connection);

        bluetoothAdapter = MainActivity.getBluetoothAdapter();

        devicesListView = findViewById(R.id.devicesListView);


        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                deviceNames.add(deviceName);
                deviceInfos.put(deviceName, device);
                //deviceInfos.put(deviceName, device.getAddress());  // MAC address

            }
        }

        listAdapter = new ArrayAdapter<>(BluetoothConnection.this,
                R.layout.items_layout,
                R.id.deviceName,
                deviceNames);
        devicesListView.setAdapter(listAdapter);

        devicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String result = parent.getItemAtPosition(position).toString();//获取选择项的值
                Toast.makeText(BluetoothConnection.this, "您点击了"+result, Toast.LENGTH_SHORT).show();

                BluetoothDevice chosenDevice = deviceInfos.get(result);
                MainActivity.setBluetoothDevice(chosenDevice);
                ConnectThread connectThread = new ConnectThread(chosenDevice);
                connectThread.start();
            }
        });
    }


    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
//            Log.i("看这儿!!!!!", "正在初始化ConnectThread");
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            mmDevice = device;

            try {
//                Log.i("看这儿!!!!!", "尝试获取socket");
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
//                Log.i("看这儿!!!!!", "获取socket失败");
                Log.e(TAG, "Socket's create() method failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
//            Log.i("看这儿!!!!!", "正在run……");
            // Cancel discovery because it otherwise slows down the connection.
            bluetoothAdapter.cancelDiscovery();

            try {
//                Log.i("看这儿!!!!!", "在try了");
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();
            } catch (IOException connectException) {
//                Log.i("看这儿!!!!!", "try出错了");
                // Unable to connect; close the socket and return.
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.e(TAG, "Could not close the client socket", closeException);
                }
                return;
            }
//            Log.i("看这儿!!!!!", "要执行manageMyConnectedSocket了");
            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            this.manageMyConnectedSocket(mmSocket);
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the client socket", e);
            }
        }

        public void manageMyConnectedSocket(BluetoothSocket mmSocket){
//            Log.i("看这儿!!!!!", "已为您经连接上了这个设备");
            MainActivity.setBluetoothSocket(mmSocket);
        }
    }
}
