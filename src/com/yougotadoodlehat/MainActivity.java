package com.yougotadoodlehat;

import android.app.Activity;
import android.graphics.Path;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);


        final DoodleView doodleView = (DoodleView)findViewById(R.id.doodleview);
        findViewById(R.id.clearbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doodleView.clear();
            }
        });


        final BluetoothFactory bt = new BluetoothFactory(this);
        bt.connect(new BluetoothConnectCallback() {
            @Override
            public void onConnected(final BluetoothConnection connection) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Connected!", Toast.LENGTH_LONG).show();
                    }
                });

                doodleView.setOnChangeListener(new DoodleView.OnChangeListener() {
                    @Override
                    public void onChange(List<DoodleLine> lines) {

                        final StringBuilder sb = new StringBuilder();
                        for (DoodleLine line : lines) {
                            line.appendToStringBuilder(sb);
                        }
                        sb.append("X");

                        connection.writeString(sb.toString());

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, sb.length() + " written!", Toast.LENGTH_LONG).show();
                            }
                        });


                    }
                });
            }
        });
    }
}
