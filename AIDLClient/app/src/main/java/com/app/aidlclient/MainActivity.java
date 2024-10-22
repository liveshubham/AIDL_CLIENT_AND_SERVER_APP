package com.app.aidlclient;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.app.aidlclient.R;
import com.app.aidlserver.IMyAidlInterface;

public class MainActivity extends AppCompatActivity {

    private TextView displayText;
    private Button messagebutton;
    private int value = -1;
    private static final String TAG = "MainActivity";
    private IMyAidlInterface iMyAidlInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        displayText = findViewById(R.id.displayText);
        messagebutton = findViewById(R.id.display_message_button);
        messagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    int color = iMyAidlInterface.getColor();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            boolean status = true;
                            if (status) {
                                status = false;
                                displayText.setText(color + "color value ");
                                displayText.setBackgroundColor(color);
                            } else {
                                status = true;
                                displayText.setText(color + "color value ");
                                displayText.setBackgroundColor(color);
                            }
                        }
                    });
                } catch (RemoteException e) {
                    Log.e("Client", "RemoteException: " + e);
                }
            }
        });
        Intent intent = new Intent();
        intent.setAction("com.app.aidlserver.IMyAidlInterface");
        intent.setPackage("com.app.aidlserver");
        bindService(intent, serverClient, BIND_AUTO_CREATE);
    }

    private ServiceConnection serverClient = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            iMyAidlInterface = IMyAidlInterface.Stub.asInterface(iBinder);

            if (iMyAidlInterface != null) {
                try {
                    int color = iMyAidlInterface.getColor();
//                    value = color;
                    Log.d(TAG, "onServiceConnected: lavkush" + color);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "" + color, Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            iMyAidlInterface = null;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}