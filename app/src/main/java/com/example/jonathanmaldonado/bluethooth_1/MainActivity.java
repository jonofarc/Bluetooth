package com.example.jonathanmaldonado.bluethooth_1;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import android.widget.Toast;
import java.util.ArrayList;
import java.util.Set;


public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSION_REQUEST_CONSTANT = 11;
    Button b1,b2,b3,b4;
    private BluetoothAdapter BA;
    private Set<BluetoothDevice>pairedDevices;
    ListView lv;
    ArrayList list = new ArrayList();
    //private ArrayList<BluetoothDevice> mDeviceList = new ArrayList<BluetoothDevice>();

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (BA.ACTION_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BA.EXTRA_STATE, BA.ERROR);

                if (state == BA.STATE_ON) {
                    showToast("Adapter Enabled");


                }else{
                    showToast("Adapter disabled");
                }
            } else if (BA.ACTION_DISCOVERY_STARTED.equals(action)) {
               // mDeviceList = new ArrayList<BluetoothDevice>();
                showToast("starting now");
                list.clear();


            } else if (BA.ACTION_DISCOVERY_FINISHED.equals(action)) {


                if(list.size()==0){
                    list.add("No Devices Found Scaning");

                }
                showToast("Finished");
                setAdapter(list);
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                //mDeviceList.add(device);

                showToast("Found device " + device.getName());
                list.add("Device: " + device.getName());



            }
        }
    };
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        b1 = (Button) findViewById(R.id.button);
        b2=(Button)findViewById(R.id.button2);
        b3=(Button)findViewById(R.id.button3);
        b4=(Button)findViewById(R.id.button4);

        BA = BluetoothAdapter.getDefaultAdapter();
        lv = (ListView)findViewById(R.id.listView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();

        filter.addAction(BA.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BA.ACTION_DISCOVERY_STARTED);
        filter.addAction(BA.ACTION_DISCOVERY_FINISHED);

        registerReceiver(mReceiver, filter);
    }

    public void on(View v){
        if (!BA.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            //startActivityForResult(turnOn, 0);
            startActivity(turnOn);
            Toast.makeText(getApplicationContext(), "Turned on",Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Already on", Toast.LENGTH_LONG).show();
        }
    }

    public void off(View v){
        BA.disable();
        Toast.makeText(getApplicationContext(), "Turned off" ,Toast.LENGTH_LONG).show();
    }


    public  void visible(View v){
        Intent getVisible = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        getVisible.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 150);//Change discoverable time from default 120 seconds to specified time in seconds
        //startActivityForResult(getVisible, 0);
        startActivity(getVisible);
    }


    public void list(View v){
        list.clear();
        pairedDevices = BA.getBondedDevices();



        for(BluetoothDevice bt : pairedDevices) list.add(bt.getName()  + "\n" + bt.getAddress());
        Toast.makeText(getApplicationContext(), "Showing Paired Devices",Toast.LENGTH_SHORT).show();

        if(list.size()==0){
                list.add("No Devices Found Paired");

        }

        setAdapter(list);
    }

    public void getDevices(View view) {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},MY_PERMISSION_REQUEST_CONSTANT);
        BA.startDiscovery();

    }
    public void setAdapter(ArrayList mylist){

        final ArrayAdapter adapter = new  ArrayAdapter(this,android.R.layout.simple_list_item_1, mylist);

        lv.setAdapter(adapter);

    }
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_CONSTANT: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission granted!
                }
                return;
            }
        }
    }
}
