package com.yougotadoodlehat;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

/**
 * User: m
 * Date: 10/31/14
 * Time: 5:12 AM
 */
public class BluetoothFactory {

    private static final String NAME = "Hatter";
    private static final UUID MY_UUID = UUID.fromString("b72bf490-60f6-11e4-9803-0800200c9a66");

    private Activity ctx;
    final BluetoothAdapter bluetooth;

    private BluetoothServerSocket mServerSocket;

    public BluetoothFactory(Activity context) {
        ctx = context;
        bluetooth = BluetoothAdapter.getDefaultAdapter();

        if (bluetooth == null) {
            Log.e("MGH", "Bluetooth doesn't exist!!!!! Oh nooo!");
            return;
        }

        if (!bluetooth.isEnabled()){

            Log.e("MGH", "Bluetooth is off!");
            Log.e("MGH", "Requesting turn on!");

            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            ctx.startActivityForResult(enableBT, 2); //second parameter might never be checked
            ctx.registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent != null &&
                            BluetoothAdapter.STATE_ON == intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                                    BluetoothAdapter.ERROR)) {

                        context.unregisterReceiver(this);

                    }
                }
            }, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));

            return;
        }

    }

    public void listen(final BluetoothDataCallback callback) {

        AcceptThread acceptThread = new AcceptThread(bluetooth, callback);
        acceptThread.start();
    }

    private class AcceptThread extends Thread {

        private BluetoothDataCallback mCallback;

        public AcceptThread(BluetoothAdapter bluetooth, BluetoothDataCallback callback){

            mCallback = callback;

            if (mServerSocket == null ) {
                BluetoothServerSocket tmp = null;
                try {
                    tmp =  bluetooth.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);

                }    catch (IOException e) {
                    Log.e("MGH", "IOException in listenUsingRfcomm");
                }
                mServerSocket = tmp;
            }
        }

        public void run(){

            BluetoothSocket socket;
            while (!isInterrupted()){
                try {
                    socket = mServerSocket.accept();
                } catch (IOException e){
                    Log.e("MGH", "IOException in accept()");
                    break;
                }

                if (socket != null){
                    readSocket(socket.getRemoteDevice(), socket, mCallback);
                }

            }
        }

    }

    private void readSocket(BluetoothDevice device, BluetoothSocket socket, BluetoothDataCallback callback){


        BluetoothConnection ct = new BluetoothConnection(device, this, socket, callback);
        ct.start();

    }


    private boolean isConnectedToTablet = false;
    public void connect(final BluetoothConnectCallback callback) {

        Set<BluetoothDevice> paired = bluetooth.getBondedDevices();
        Iterator<BluetoothDevice> iterator = paired.iterator();
        while (iterator.hasNext()) {
            BluetoothDevice device = iterator.next();

            if (device.getName().equals("Nexus 7")) {

                new ConnectThread(device, callback).start();

            }
        }
    }


    private class ConnectThread extends Thread {
        BluetoothDevice mDevice;
        BluetoothSocket mSocket;
        BluetoothConnectCallback mCallback;

        public ConnectThread(BluetoothDevice device, BluetoothConnectCallback callback) {

            mCallback = callback;

            BluetoothSocket tmp = null;
            mDevice = device;

            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.d("MGH BT", e.getMessage());}
            mSocket = tmp;
        }

        public void run() {
            //mBluetooth.cancelDiscovery();

            boolean good = false;
            try {
                mSocket.connect();
                good = true;

            } catch (IOException connectException) {
                Log.d("MGH BT", connectException.getMessage());
            }
            if (good) {
                BluetoothConnection ct = new BluetoothConnection(mDevice, BluetoothFactory.this,
                        mSocket, null);
                ct.start();

                mCallback.onConnected(ct);

            }
            else {
                try {
                    mSocket.close();
                }
                catch (IOException e) {
                    Log.d("MGH BT", e.getMessage());
                }
            }
        }
    }
}
