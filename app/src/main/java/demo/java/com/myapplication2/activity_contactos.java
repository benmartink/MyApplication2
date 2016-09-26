package demo.java.com.myapplication2;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import demo.java.com.myapplication2.CustomControls.AdapterContactosCercanos.AdapterMisContactos.EndlessListView;
import demo.java.com.myapplication2.CustomControls.AdapterContactosCercanos.AdapterMisContactos.MyAdapterContactos;

import static java.lang.Integer.parseInt;

public class activity_contactos extends Activity {

    private static final int ID_MENU_REFRESH = 666;

    private MyAdapterContactos adapter;
    private EndlessListView endlessListView;

    private int mCount;
    private int CantidadPorPagina = 20;
    private int CantidadPaginas = 1;
    private boolean mHaveMoreDataToLoad;
    Boolean encontroContactos = false;
    private int CantidadTotalContactos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactos);

        mCount = 1;
        SessionManager session = new SessionManager(getApplicationContext());
        boolean sesionIniciada = session.isLoggedIn();
        if(sesionIniciada)
        {
            Intent intent = this.getIntent();
            mHaveMoreDataToLoad = true;
            adapter = new MyAdapterContactos(activity_contactos.this,null);
            endlessListView = (EndlessListView) findViewById(R.id.endlessListView);

            endlessListView.setAdapter(adapter);
            endlessListView.setOnLoadMoreListener(loadMoreListener);

            new HttpRequestIniciarCarga().execute(session.getUserDetails().get("IdUsuario") + "");
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        }else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, ID_MENU_REFRESH, 0, "Refresh");

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return true;
    }

    private void loadMoreData() {
        SessionManager session = new SessionManager(getApplicationContext());
        new LoadMore().execute(session.getUserDetails().get("IdUsuario") + "", mCount + "");
    }

    private EndlessListView.OnLoadMoreListener loadMoreListener = new EndlessListView.OnLoadMoreListener() {

        @Override
        public boolean onLoadMore() {
            if (true == mHaveMoreDataToLoad) {
                loadMoreData();
            } else {
                //Toast.makeText(activity_contactos.this, "No more data to load", Toast.LENGTH_SHORT).show();
            }

            return mHaveMoreDataToLoad;
        }
    };

    private class LoadMore extends AsyncTask<String, Void, Void> {
        String strIdUsuario = "";
        HttpURLConnection urlConnection = null;
        ArrayList<EN_Usuario> objUsuarios = null;
        String paginaActual = "";
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
                        strIdUsuario = params[0];
                        paginaActual = params[1];
                    }
                }

                URL url = new URL(getResources().getString(R.string.urlServicio) + "/Contacto.svc/obtenerContactos/" + strIdUsuario + "/" + (paginaActual) + "/" + CantidadPorPagina);
                Log.i("mCount", "paginaActual: doInBackground: " + paginaActual);
                Log.i("response", getResources().getString(R.string.urlServicio) + "/Contacto.svc/obtenerContactos/" + strIdUsuario + "/" + (paginaActual) + "/" + CantidadPorPagina);
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
                    objUsuarios = new ArrayList<EN_Usuario>();
                    objUsuarios = EN_Usuario.fromJson(x);
                    if (objUsuarios!=null) {
                        encontroContactos = true;
                    }else
                    {
                        encontroContactos = false;
                    }
                }
                else
                {
                    encontroContactos = false;
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
            super.onPostExecute(aVoid);

            adapter.addItems(objUsuarios);
            endlessListView.loadMoreCompleat();
            mHaveMoreDataToLoad = mCount < CantidadPaginas;
            //findViewById(R.id.loadingPanelContactos).setVisibility(View.GONE);
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

    public class HttpRequestIniciarCarga extends AsyncTask<String,Void,Void> {
        String _strGUID;
        HttpURLConnection urlConnection = null;

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
                        _strGUID = params[0];
                    }
                }
                URL url = new URL(getResources().getString(R.string.urlServicio) + "/Contacto.svc/obtenerContactosTotal/" + _strGUID);
                Log.i("response", getResources().getString(R.string.urlServicio) + "/Contacto.svc/obtenerContactosTotal/" + _strGUID);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setConnectTimeout(10000);
                urlConnection.setReadTimeout(20000);
                //urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json");

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String response = convertStreamToString(in).trim();

                if (response.length()>0)
                {
                    CantidadTotalContactos = parseInt(response);
                }
                else
                {

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Double division = Math.ceil(CantidadTotalContactos / CantidadPorPagina);
            CantidadPaginas = division.intValue();
            mCount++;
            new LoadMore().execute(_strGUID, mCount+ "");
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

    public void regresarHome(View view) {
        finish();
        //System.exit(0);
    }

    @Override
    public void onBackPressed() { }

}
