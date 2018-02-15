package com.example.irenecarrasco.estadosmartphone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class TiempoReal extends AppCompatActivity {

    private TextView callStatus;
    private TextView connectionStatus;
    private TextView serviceStatus;
    private TextView location;
    private ImageView signalLevel;
    private ImageView dataImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tiempo_real);

        inicializarComponentes();
    }

    private void inicializarComponentes(){
        callStatus = findViewById(R.id.callStatusValue);
        connectionStatus = findViewById(R.id.connectionStatusValue);
        serviceStatus = findViewById(R.id.serviceStatusValue);
        location = findViewById(R.id.locationValue);
        signalLevel = findViewById(R.id.signalLevelImage);
        dataImage = findViewById(R.id.dataImage);
    }
}
