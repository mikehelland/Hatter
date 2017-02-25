package com.yougotadoodlehat;

import android.app.Activity;
import android.graphics.Path;
import android.graphics.drawable.shapes.PathShape;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

public class HatActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN|
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_FULLSCREEN|WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.main);

        final DoodleView doodleView = (DoodleView)findViewById(R.id.doodleview);
        findViewById(R.id.clearbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doodleView.clear();
            }
        });



        BluetoothFactory bt = new BluetoothFactory(this);
        bt.listen(new BluetoothDataCallback() {
            @Override
            public void newData(String newData) {

                if (!newData.endsWith("X")) {
                    partialTransmission = partialTransmission + newData;
                    return;
                }

                List<Path> paths = new ArrayList<Path>();
                Path path;
                String newString = partialTransmission + newData.substring(0, newData.length() - 2);
                partialTransmission = "";


                Log.d("MGH BT dat", newData);

                String[] xy;
                float x, y;
                String[] coords;
                String[] lines = newString.split("\\|");
                boolean firstcoord;
                for (String lineString : lines) {
                    path = new Path();
                    coords = lineString.split(";");
                    firstcoord = true;
                    for (String coord : coords) {
                        xy = coord.split(",");

                        if (xy.length < 2 || xy[0].length() == 0)
                            continue;

                        x = Float.parseFloat(xy[0]) * doodleView.getWidth();
                        y = Float.parseFloat(xy[1]) * doodleView.getHeight();

                        if (!firstcoord) {
                            path.lineTo(x, y);

                        }
                        path.moveTo(x, y);
                        Log.d("MGH adding coord?", x + ", " + y);
                        firstcoord = false;
                    }

                    paths.add(path);

                }

                doodleView.setPaths(paths);
            }
        });


    }
}
