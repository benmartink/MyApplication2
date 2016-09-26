package demo.java.com.myapplication2;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class activity_detallecontacto extends AppCompatActivity {

    Boolean usuarioObtenido = false;

    private static final String ID_USUARIO = "";

    public static void start(Context context, String id) {
        Intent intent = new Intent(context, activity_detallecontacto.class);
        intent.putExtra(ID_USUARIO, id);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detallecontacto);

        SessionManager session = new SessionManager(getApplicationContext());
        boolean sesionIniciada = session.isLoggedIn();
        if(sesionIniciada)
        {
            Intent intent = this.getIntent();
            String id = intent.getStringExtra(ID_USUARIO);
            new HttpRequestObtenerDatosContacto().execute(id);
        }else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }


    }

    public void realizarLlamada(View view) {
        TextView txtCelular = (TextView) findViewById(R.id.txtCelular);
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + txtCelular.getText()));
        startActivity(intent);
    }

    public class HttpRequestObtenerDatosContacto extends AsyncTask<String,Void,Void> {
        String strIdUsuario = "";
        HttpURLConnection urlConnection = null;
        EN_Usuario objUsuario;

        @Override
        protected void onPreExecute() {
            findViewById(R.id.loadingPanelDetalleContacto).setVisibility(View.VISIBLE);
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
                    }
                }
                URL url = new URL(getApplicationContext().getResources().getString(R.string.urlServicio) + "/Usuario.svc/obtenerUsuario/" + strIdUsuario);
                Log.i("response", getApplicationContext().getResources().getString(R.string.urlServicio) + "/Usuario.svc/obtenerUsuario/" + strIdUsuario);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setConnectTimeout(10000);
                urlConnection.setReadTimeout(20000);
                //urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json");

                Log.i("Paso", "1");
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                Log.i("Paso", "1.1");
                String response = convertStreamToString(in).trim();
                Log.i("Paso", "2");
                if (response.length()>0)
                {
                    Log.i("response", response);
                    JSONObject x = new JSONObject(response);
                    objUsuario = EN_Usuario.fromJson(x);
                    if (objUsuario!=null) {
                        usuarioObtenido = true;
                    }else
                    {
                        usuarioObtenido = false;
                    }
                }
                else
                {
                    usuarioObtenido = false;
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
            if(usuarioObtenido)
            {
                String DisplayName = objUsuario.getNombres() + " " + objUsuario.getApellidos();
                String MobileNumber = objUsuario.getCelularFijo();
                String HomeNumber = null;
                String WorkNumber = null;
                String emailID = null;
                String company = "";
                String jobTitle = "";
                TextView txtNombres = (TextView) findViewById(R.id.txtNombres);
                txtNombres.setText(objUsuario.getNombres());

                TextView txtApellidos = (TextView) findViewById(R.id.txtApellidos);
                txtApellidos.setText(objUsuario.getApellidos());

                TextView txtCargo = (TextView) findViewById(R.id.txtCargo);
                txtCargo.setText(objUsuario.getCargo());

                TextView txtCelular = (TextView) findViewById(R.id.txtCelular);
                txtCelular.setText(objUsuario.getCelularFijo());
            }
            super.onPostExecute(aVoid);
            findViewById(R.id.loadingPanelDetalleContacto).setVisibility(View.GONE);
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
    }

    @Override
    public void onBackPressed() { }
}
