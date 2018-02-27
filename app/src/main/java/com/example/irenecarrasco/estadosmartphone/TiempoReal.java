package com.example.irenecarrasco.estadosmartphone;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.JsonReader;
import android.util.Pair;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class TiempoReal extends AppCompatActivity implements OnMapReadyCallback {

    private TextView callStatus;
    private TextView connectionStatus;
    private TextView serviceStatus;
    private TextView location;
    private ImageView signalLevel;
    private ImageView dataImage;
    private MapView mapView;
    private LatLng coordenadas;
    private String mcc;
    private String mnc;
    private int cellid;
    private int lac;

    private static final String OPENCELLID_KEY = "9c14bfb39bdd69";
    private static final String MAP_VIEW_BUNDLE_KEY = "AIzaSyDnesjxf6CsPatAcVdISbjyHPZwyYRX7FQ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tiempo_real);

        inicializarComponentes();
        preparingURL();
        inicializarListener();
        cargarMapa(savedInstanceState);
    }

    private void inicializarComponentes(){
        callStatus = findViewById(R.id.callStatusValue);
        connectionStatus = findViewById(R.id.connectionStatusValue);
        serviceStatus = findViewById(R.id.serviceStatusValue);
        location = findViewById(R.id.locationValue);
        signalLevel = findViewById(R.id.signalLevelImage);
        dataImage = findViewById(R.id.dataImage);

        coordenadas = new LatLng(-1, -1);
    }

    private void cargarMapa(Bundle bundle){
        Bundle mapViewBundle = null;
        if(bundle != null){
            mapViewBundle = bundle.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        mapView = findViewById(R.id.mapa);
        mapView.onCreate(bundle);
        mapView.getMapAsync(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if(mapViewBundle == null){
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }
    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private void inicializarListener(){

        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        String networkOperator = tm.getNetworkOperator();
        mcc = networkOperator.substring(0,3);
        mnc = networkOperator.substring(3);

        System.out.println("-------------------------------> mcc: "+mcc);
        System.out.println("-------------------------------> mnc: "+mnc);

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

                    int latitud = ((CdmaCellLocation) localizacion).getBaseStationLatitude();
                    int longitud = ((CdmaCellLocation) localizacion).getBaseStationLongitude();

                    coordenadas = new LatLng(((CdmaCellLocation) localizacion).convertQuartSecToDecDegrees(latitud),
                            ((CdmaCellLocation) localizacion).convertQuartSecToDecDegrees(longitud));
                }else if(localizacion instanceof GsmCellLocation){
                    posicion += String.valueOf(((GsmCellLocation) localizacion).getCid())+",";
                    posicion += String.valueOf(((GsmCellLocation) localizacion).getLac());

                    cellid = ((GsmCellLocation) localizacion).getCid();
                    lac = ((GsmCellLocation) localizacion).getLac();

                    System.out.println("-------------------------------> cellid: "+cellid);
                    System.out.println("-------------------------------> lac: "+lac);
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

    }

    private void preparingURL(){
        try {
            //String file="http://opencellid.org/cell/get?key=" + OPENCELLID_KEY + "&mcc=" + mcc + "&mnc=" + mnc + "&lac=" + lac + "&cellid=" + cellid + "&format=json";
            //System.out.println("URL: "+file);

            String file ="http://opencellid.org/cell/get?key=9c14bfb39bdd69&mcc=214&mnc=01&lac=55526&cellid=37647840&format=json";
            URL link = new URL(file);
            new ManagerNet().execute(link);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    class ManagerNet extends AsyncTask<URL, Void, Pair<Double,Double>>{
        @Override
        protected Pair<Double,Double> doInBackground(URL... urls) {

            HttpURLConnection urlConnection = null;

            try{
                URL link = urls[0];
                urlConnection = (HttpURLConnection) link.openConnection();
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.connect();

                int statusCode = urlConnection.getResponseCode();
                if(statusCode == HttpURLConnection.HTTP_OK){
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    JsonReader jsor = new JsonReader(new InputStreamReader(urlConnection.getInputStream()));

                    jsor.beginObject();
                    Double latitud = null;
                    Double longitud = null;
                    while (jsor.hasNext()){
                        String key = jsor.nextName();
                        System.out.println("............................... KEY: "+ key);
                        if(latitud == null && "lat".equals(key)){
                            latitud = jsor.nextDouble();
                            System.out.println("-----------------> latitud: " + latitud.toString());
                        }else if(longitud == null && "lon".equals(key)){
                            longitud = jsor.nextDouble();
                            System.out.println("-----------------> longitud: " + longitud.toString());
                        } else{
                            jsor.skipValue();
                        }
                    }
                    jsor.endObject();
                    return new Pair<>(latitud,longitud);
                }

            }catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Pair<Double,Double> hola) {
                super.onPostExecute(hola);
                coordenadas = new LatLng(hola.first, hola.second);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //LatLng caceres = new LatLng(39.4694077, -6.3705542);
        googleMap.setMinZoomPreference(12);
        System.out.println("COORDENADAS .--------> "+ coordenadas.longitude + "," + coordenadas.latitude);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(coordenadas));
    }
}
