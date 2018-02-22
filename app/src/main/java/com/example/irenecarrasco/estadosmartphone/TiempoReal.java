package com.example.irenecarrasco.estadosmartphone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.TelecomManager;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
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
        inicializarListener();
    }

    private void inicializarComponentes(){
        callStatus = findViewById(R.id.callStatusValue);
        connectionStatus = findViewById(R.id.connectionStatusValue);
        serviceStatus = findViewById(R.id.serviceStatusValue);
        location = findViewById(R.id.locationValue);
        signalLevel = findViewById(R.id.signalLevelImage);
        dataImage = findViewById(R.id.dataImage);
    }

    private void inicializarListener(){
        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        int eventos = PhoneStateListener.LISTEN_CALL_STATE
                |PhoneStateListener.LISTEN_CELL_LOCATION
                |PhoneStateListener.LISTEN_DATA_ACTIVITY
                |PhoneStateListener.LISTEN_CALL_FORWARDING_INDICATOR
                |PhoneStateListener.LISTEN_DATA_CONNECTION_STATE
                |PhoneStateListener.LISTEN_SERVICE_STATE;
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
//            public void onCellLocationChanged(CellLocation localizacion){
//
//            }

        };
        tm.listen(listenerTelefono, eventos);
    }


}
