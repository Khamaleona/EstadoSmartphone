package com.example.irenecarrasco.estadosmartphone;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.TelecomManager;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.net.URI;
import java.util.ArrayList;
import java.util.concurrent.BrokenBarrierException;

public class TiempoReal extends AppCompatActivity {

    private TextView callStatus;
    private TextView connectionStatus;
    private TextView serviceStatus;
    private TextView location;
    private ImageView signalLevel;
    private ImageView dataImage;
    private Button goToLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tiempo_real);

        inicializarComponentes();
        inicializarListener();
    }

    private void inicializarComponentes(){
        callStatus = findViewById(R.id.callStatusValue);
        connectionStatus = findViewById(R.id.connectionStatusValue);
        serviceStatus = findViewById(R.id.serviceStatusValue);
        location = findViewById(R.id.locationValue);
        signalLevel = findViewById(R.id.signalLevelImage);
        dataImage = findViewById(R.id.dataImage);
        goToLocation = findViewById(R.id.goToLocalization);
    }

    private void inicializarListener(){
        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        int eventos = PhoneStateListener.LISTEN_CALL_STATE
                |PhoneStateListener.LISTEN_CELL_LOCATION
                |PhoneStateListener.LISTEN_DATA_ACTIVITY
                |PhoneStateListener.LISTEN_CALL_FORWARDING_INDICATOR
                |PhoneStateListener.LISTEN_DATA_CONNECTION_STATE
                |PhoneStateListener.LISTEN_SERVICE_STATE
                |PhoneStateListener.LISTEN_SIGNAL_STRENGTHS;
        PhoneStateListener listenerTelefono = new PhoneStateListener(){

            //Estado de la llamada
            public void onCallStateChanged (int estado, String numeroEntrante){
                String estadoTelefono = "Desconocido";
                switch (estado){
                    case TelephonyManager.CALL_STATE_IDLE:
                        estadoTelefono = "Sin actividad";
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        estadoTelefono = "Descolgado";
                        break;
                    case TelephonyManager.CALL_STATE_RINGING:
                        estadoTelefono = "Llamada entrante";
                        break;
                }
                callStatus.setText(estadoTelefono);
                super.onCallStateChanged(estado,numeroEntrante);
            }

            //Estado de la conexión
            public void onDataConnectionStateChanged(int estado){
                String estadoConexion = "Desconocido";
                switch (estado){
                    case TelephonyManager.DATA_DISCONNECTED:
                        estadoConexion = "Desconectado";
                        break;
                    case TelephonyManager.DATA_SUSPENDED:
                        estadoConexion = "Suspendido";
                        break;
                    case TelephonyManager.DATA_CONNECTING:
                        estadoConexion = "Conectando";
                        break;
                    case TelephonyManager.DATA_CONNECTED:
                        estadoConexion = "Conectado";
                        break;
                }
                connectionStatus.setText(estadoConexion);
                super.onDataConnectionStateChanged(estado);
            }

            //Estado del servicio
            public void onServiceStateChanged(ServiceState estadoTelefono){
                String estadoServicio = "Desconocido";
                switch (estadoTelefono.getState()){
                    case ServiceState.STATE_EMERGENCY_ONLY:
                        estadoServicio = "Solo llamadas de emergencia";
                        break;
                    case ServiceState.STATE_IN_SERVICE:
                        estadoServicio = "Operativo";
                        break;
                    case ServiceState.STATE_OUT_OF_SERVICE:
                        estadoServicio = "Fuera de servicio";
                        break;
                    case ServiceState.STATE_POWER_OFF:
                        estadoServicio = "Apagado";
                        break;
                }
                serviceStatus.setText(estadoServicio);
                super.onServiceStateChanged(estadoTelefono);
            }

            //Localización
            public void onCellLocationChanged(CellLocation localizacion) {
                String posicion = "";

                if (localizacion instanceof CdmaCellLocation){
                    posicion += String.valueOf(((CdmaCellLocation) localizacion).getBaseStationLatitude())+",";
                    posicion += String.valueOf(((CdmaCellLocation) localizacion).getBaseStationLongitude());
                }else if(localizacion instanceof GsmCellLocation){
                    posicion += String.valueOf(((GsmCellLocation) localizacion).getCid())+",";
                    posicion += String.valueOf(((GsmCellLocation) localizacion).getLac());
                }
                location.setText(posicion);
            }

            //Dirección del tráfico
            public void onDataActivity(int direccion){
                switch (direccion){
                    case TelephonyManager.DATA_ACTIVITY_NONE:
                        dataImage.setImageResource(R.drawable.nodata);
                        break;
                    case TelephonyManager.DATA_ACTIVITY_IN:
                        dataImage.setImageResource(R.drawable.indata);
                        break;
                    case TelephonyManager.DATA_ACTIVITY_OUT:
                        dataImage.setImageResource(R.drawable.outdata);
                        break;
                    case TelephonyManager.DATA_ACTIVITY_INOUT:
                        dataImage.setImageResource(R.drawable.bidata);
                        break;
                    default:
                        dataImage.setImageResource(R.drawable.nodata);
                        break;
                }
                super.onDataActivity(direccion);
            }

            //Potencia de la señal
            public void onSignalStrengthsChanged(SignalStrength fuerza){
                Log.i("Fuerza", String.valueOf(fuerza.getLevel()));
                System.out.println("FUERZA: "+fuerza.getLevel());
                switch (fuerza.getLevel()){
                    case 0:
                        signalLevel.setImageResource(R.drawable.level1);
                        break;
                    case 1:
                        signalLevel.setImageResource(R.drawable.level2);
                        break;
                    case 2:
                        signalLevel.setImageResource(R.drawable.level3);
                        break;
                    case 3:
                        signalLevel.setImageResource(R.drawable.level4);
                        break;
                    case 4:
                        signalLevel.setImageResource(R.drawable.level5);
                        break;
                }
                super.onSignalStrengthsChanged(fuerza);
            }

        };
        tm.listen(listenerTelefono, eventos);

        goToLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TiempoReal.this, MapsActivity.class);
                startActivity(intent);
            }
        });
    }


}
