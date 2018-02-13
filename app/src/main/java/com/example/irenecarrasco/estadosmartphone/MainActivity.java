package com.example.irenecarrasco.estadosmartphone;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Button obtenerDatos;
    private TextView id;
    private TextView phoneNumber;
    private TextView swVersion;
    private TextView operatorName;
    private TextView simCode;
    private TextView simOperator;
    private TextView simSerial;
    private TextView susID;
    private TextView redType;
    private TextView voiceType;

    private TelephonyManager tm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inicializarComponentes();
        mostrarInformacionTelefono();
    }

    private void inicializarComponentes() {
        obtenerDatos = findViewById(R.id.obtenerDatos);
        id = findViewById(R.id.idDispositivoValue);
        phoneNumber = findViewById(R.id.phoneNumberValue);
        swVersion = findViewById(R.id.swVersionValue);
        operatorName = findViewById(R.id.nameValue);
        simCode = findViewById(R.id.simCodeValue);
        simOperator = findViewById(R.id.simOperatorValue);
        simSerial = findViewById(R.id.simSerialValue);
        susID = findViewById(R.id.susIDValue);
        redType = findViewById(R.id.redTypeValue);
        voiceType = findViewById(R.id.voiceTypeValue);

        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
    }

    private void mostrarInformacionTelefono() {
        obtenerDatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                Log.i("LLEGA", "LLEGA");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    id.setText(tm.getMeid());
                }else{
                    id.setText(tm.getDeviceId());
                }

                phoneNumber.setText(tm.getLine1Number());
                swVersion.setText(tm.getDeviceSoftwareVersion());
                operatorName.setText(tm.getNetworkOperatorName());
                simCode.setText(tm.getSimCountryIso());
                simOperator.setText(tm.getSimOperatorName());
                simSerial.setText(tm.getSimSerialNumber());
                susID.setText(tm.getSubscriberId());
               /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    voiceType.setText(tm.getVoiceNetworkType());
                }else{
                    voiceType.setText(tm.getNetworkType());
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    redType.setText(tm.getDataNetworkType());
                }else{
                    redType.setText(tm.getNetworkType());
                }*/

            }
        });
    }
}
