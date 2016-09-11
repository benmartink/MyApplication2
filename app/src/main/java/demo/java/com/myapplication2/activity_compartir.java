package demo.java.com.myapplication2;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import demo.java.com.myapplication2.CustomControls.AdapterContactosCercanos.MyListFragment;

public class activity_compartir extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap map;
    private GoogleMap mMap;
    protected GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    double lat = 0, lng = 0;
    private GoogleApiClient client;
    Boolean encontroCercanos = false;
    Boolean ubicacionesEliminadas = false;
    private float radioBusqueda = 30;

    protected synchronized void buildGoogleApiClient() {
        //Toast.makeText(this, "buildGoogleApiClient", Toast.LENGTH_SHORT).show();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compartir);

        SessionManager session = new SessionManager(getApplicationContext());
        boolean sesionIniciada = session.isLoggedIn();
        if(sesionIniciada)
        {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

            map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            mapFragment.getMap().getUiSettings().setZoomControlsEnabled(true);
            mapFragment.getMap().getUiSettings().setZoomGesturesEnabled(true);
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mapFragment.getMap().setMyLocationEnabled(true);


            mapFragment.getMap().setOnMyLocationChangeListener(myLocationChangeListener());
            // ATTENTION: This was auto-generated to implement the App Indexing API.
            // See https://g.co/AppIndexing/AndroidStudio for more information.
            client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        }else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }


    }

    public void regresarHome(View view) {
        finish();
    }

    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener() {
        return new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {

                map.clear();
                Circle circle = map.addCircle(new CircleOptions()
                        .center(new LatLng(location.getLatitude(), location.getLongitude()))
                        .radius(radioBusqueda)
                        .strokeColor(Color.RED)
                        .strokeWidth(4)
                        .fillColor(Color.TRANSPARENT));

                map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 19.0f));

                SessionManager session = new SessionManager(getApplicationContext());
                new HttpRequestObtenerContactosCercanos().execute(location.getLatitude() + "", location.getLongitude() + "", radioBusqueda / 1000 + "", session.getUserDetails().get("IdUsuario") + "");
            }
        };
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            lat = mLastLocation.getLatitude();
            lng = mLastLocation.getLongitude();

            LatLng loc = new LatLng(lat, lng);
            mMap.addMarker(new MarkerOptions().position(loc).title("New Marker"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Add a marker in Sydney, Australia, and move the camera.
        //LatLng sydney = new LatLng(-34, 151);
        //googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //map.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

        //add this here:
        buildGoogleApiClient();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Maps Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://demo.java.com.myapplication2/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Maps Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://demo.java.com.myapplication2/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    @Override
    public void onPause(){
        SessionManager session = new SessionManager(getApplicationContext());
        new HttpRequestEliminarUbicaciones().execute(session.getUserDetails().get("IdUsuario") + "");
        super.onPause();
    }

    public class HttpRequestObtenerContactosCercanos extends AsyncTask<String,Void,Void> {
        String strCoordenadaLatitud = "";
        String strCoordenadaLongitud ="";
        String strRadio = "";
        String strIdUsuario = "";
        HttpURLConnection urlConnection = null;
        String strUrl = "";
        ArrayList<EN_Ubicacion> objUbicaciones;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            Log.i("SYNC", "INIT");

            try {
                if (params!=null)
                {
                    if(params.length > 0)
                    {
                        strCoordenadaLatitud = params[0];
                        strCoordenadaLongitud = params[1];
                        strRadio = params[2];
                        strIdUsuario = params[3];
                    }
                }
                //strCoordenadaLatitud = strCoordenadaLatitud.replace('.',',');
                //strCoordenadaLongitud = strCoordenadaLongitud.replace('.',',');
                //strRadio = strRadio.replace('.',',');
                URL url = new URL(getResources().getString(R.string.urlServicio) + "/Ubicacion.svc/obtenerUbicaciones/" + strCoordenadaLatitud + "/" + strCoordenadaLongitud+ "/" + strRadio+ "/" + strIdUsuario);
                Log.i("response", getResources().getString(R.string.urlServicio) + "/Ubicacion.svc/obtenerUbicaciones/" + strCoordenadaLatitud + "/" + strCoordenadaLongitud+ "/" + strRadio+ "/" + strIdUsuario);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setConnectTimeout(10000);
                urlConnection.setReadTimeout(20000);
                //urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json");

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String response = convertStreamToString(in).trim();
                if (response.length()>0)
                {
                    Log.i("response", response);
                    JSONArray x = new JSONArray(response);
                    objUbicaciones = EN_Ubicacion.fromJson(x);
                    if (objUbicaciones!=null) {
                        encontroCercanos = true;
                    }else
                    {
                        encontroCercanos = false;
                    }
                }
                else
                {
                    encontroCercanos = false;
                    Log.i("response", "sin dato");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(encontroCercanos)
            {
                if(objUbicaciones!=null){
                    for (int i = 0; i < objUbicaciones.size(); i++) {
                        EN_Ubicacion objUbicacion = objUbicaciones.get(i);
                        map.addMarker(new MarkerOptions()
                                        .position(new LatLng(Double.parseDouble(objUbicacion.getCoordenadaLatitud()), Double.parseDouble(objUbicacion.getCoordenadaLongitud())))
                                        .title(objUbicacion.getNombreUsuario() + " " + objUbicacion.getApellidoUsuario())
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.iconocontactocercano))
                                        //.snippet(objUbicacion.getNombreUsuario() + " " + objUbicacion.getApellidoUsuario())
                        );
                    }
                }
            }

            MyListFragment listadoCercanos = (MyListFragment) getFragmentManager()
                    .findFragmentById(R.id.fragment1);
            if (listadoCercanos != null) {
                listadoCercanos.refreshData(objUbicaciones);
                Log.d("Contactos cercanos", "Actualizados");
                int cantidad = 0;
                if (objUbicaciones!=null)
                {
                    cantidad = objUbicaciones.size();
                }
            }
            super.onPostExecute(aVoid);

        }

        private String convertStreamToString(InputStream is) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    line = line + "\n";
                    sb.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return sb.toString();
        }
    }

    public class HttpRequestEliminarUbicaciones extends AsyncTask<String,Void,Void> {
        String strIdUsuario = "";
        HttpURLConnection urlConnection = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            Log.i("SYNC", "INIT ELIMINA");

            try {
                if (params!=null)
                {
                    if(params.length > 0)
                    {
                        strIdUsuario = params[0];
                    }
                }
                URL url = new URL(getResources().getString(R.string.urlServicio) + "/Ubicacion.svc/eliminarUbicaciones");
                Log.i("response", getResources().getString(R.string.urlServicio) + "/Ubicacion.svc/eliminarUbicaciones");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setConnectTimeout(10000);
                urlConnection.setReadTimeout(20000);
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json");

                OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("IdUsuario", strIdUsuario);
                out.write(jsonObject.toString());
                out.close();

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String response = convertStreamToString(in).trim();
                if (response.length()>0)
                {
                    ubicacionesEliminadas = true;
                }
                else
                {
                    ubicacionesEliminadas = false;
                    Log.i("response", "sin dato");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(ubicacionesEliminadas)
            {
                //Toast.makeText(getApplicationContext(),"Ubicaciones eliminadas", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), activity_home.class);
                startActivity(intent);
            }
            super.onPostExecute(aVoid);

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            if(mapFragment!=null) {
                if(mapFragment.getMap()!=null)
                {
                    mapFragment.getMap().setOnMyLocationChangeListener(null);
                }
            }
            //finish();
            //System.exit(0);

        }

        private String convertStreamToString(InputStream is) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    line = line + "\n";
                    sb.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return sb.toString();
        }
    }
}
