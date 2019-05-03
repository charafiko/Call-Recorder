package com.balakrishnan.poorna.call_recordingapp;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    TextView tv,tv2;
    EditText ed2;
    Button b1;
    TelephonyManager tm;
    boolean buttonpress=false;
    PhoneStateListener ls;
    private static final int MY_PERMISSION_REQUEST_CALL_PHONE=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        tv = findViewById(R.id.textView);
        tv2=findViewById(R.id.textView2);
        ed2 = findViewById(R.id.editText2);
        b1 = findViewById(R.id.button);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonpress=true;
                if(ed2.getText().toString().length()<10)
                    Toast.makeText(getApplicationContext(), "Enter a valid number", Toast.LENGTH_SHORT).show();
                else {
                    makeCall();

                    ed2.setText("");
                    ls = new PhoneStateListener() {
                        @Override
                        public void onCallStateChanged(int state, String incomingNumber) {
                            if (TelephonyManager.CALL_STATE_OFFHOOK ==state ) {
                                Toast.makeText(getApplicationContext(), "Starting call recording service", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), RecordingService.class);
                                startService(intent);
                                Toast.makeText(getApplicationContext(), "Call Recording is set ON", Toast.LENGTH_SHORT).show();
                                System.out.println("Main Activity hook off running");
                            }

                        }
                    };
                    tm.listen(ls, PhoneStateListener.LISTEN_CALL_STATE);

                }

            }
        });
        ls = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                if (TelephonyManager.CALL_STATE_IDLE == state) {
                    Toast.makeText(getApplicationContext(), "Idle state", Toast.LENGTH_SHORT).show();
                } else if (TelephonyManager.CALL_STATE_RINGING == state) {
                    Toast.makeText(getApplicationContext(), "Phone ringing", Toast.LENGTH_SHORT).show();
                    if(buttonpress==false) {
                        Toast.makeText(getApplicationContext(), "Starting call recording service", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), RecordingService.class);
                        startService(intent);
                        Toast.makeText(getApplicationContext(), "Call Recording is set ON", Toast.LENGTH_SHORT).show();
                    }
                } else if (TelephonyManager.CALL_STATE_OFFHOOK == state) {
                    //Toast.makeText(getApplicationContext(), "Call received", Toast.LENGTH_LONG).show();
                }

            }
        };
        tm.listen(ls, PhoneStateListener.LISTEN_CALL_STATE);

    }
    @Override
    public void onClick(View v) {

    }
    public void makeCall()
    {
        Intent in = new Intent(Intent.ACTION_CALL);
        String phnno = String.format("tel: %s", ed2.getText().toString());
        in.setData(Uri.parse(phnno));
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE);
        if (result == PackageManager.PERMISSION_GRANTED){

            startActivity(in);

        } else {

            requestForCallPermission();
        }
    }
    public void requestForCallPermission()
    {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CALL_PHONE))
        {
        }
        else {

            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CALL_PHONE},1);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();

        // Runtime permission
        try {

            boolean permissionGranted_OutgoingCalls = ActivityCompat.checkSelfPermission(this, Manifest.permission.PROCESS_OUTGOING_CALLS) == PackageManager.PERMISSION_GRANTED;
            boolean permissionGranted_phoneState = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
            boolean permissionGranted_recordAudio = ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
            boolean permissionGranted_WriteExternal = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
            boolean permissionGranted_ReadExternal = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;


            if (permissionGranted_OutgoingCalls) {
                if (permissionGranted_phoneState) {
                    if (permissionGranted_recordAudio) {
                        if (permissionGranted_WriteExternal) {
                            if (permissionGranted_ReadExternal) {
                                try {
                                    // toggleButton.setVisibility(View.VISIBLE);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 200);
                            }
                        } else {
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 300);
                        }
                    } else {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 400);
                    }
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 500);
                }
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.PROCESS_OUTGOING_CALLS}, 600);
            }

        } catch (
                Exception e)

        {
            e.printStackTrace();
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 200 || requestCode == 300 || requestCode == 400 || requestCode == 500 || requestCode == 600) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    //toggleButton.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if(requestCode==1){
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    makeCall();
                }
            }
        }
    }




}

