package demo.java.com.myapplication2;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class activity_recuperarclave extends AppCompatActivity {

    EditText etUserName;
    String usr;
    Boolean correoEnviado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperarclave);
    }

    public void recuperarClave(View view) {
        etUserName = (EditText)findViewById(R.id.etUserName);
        usr = etUserName.getText().toString();
        if (usr.trim().length()==0)
        {
            Toast.makeText(getApplicationContext(), "Ingrese una cuenta de correo electrónico", Toast.LENGTH_LONG).show();
        }
        else
        {
            new HttpRequestRecuperarClave().execute(usr);
        }
    }

    public class HttpRequestRecuperarClave extends AsyncTask<String,Void,Void> {
        String strUser = "";
        HttpURLConnection urlConnection = null;
        EN_Usuario objUsuario;

        @Override
        protected void onPreExecute() {
            findViewById(R.id.loadingPanelRecuperarClave).setVisibility(View.VISIBLE);
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
                        strUser = params[0];
                    }
                }
                URL url = new URL(getResources().getString(R.string.urlServicio) + "/Usuario.svc/recuperarClave/" + strUser);
                Log.i("response", getResources().getString(R.string.urlServicio) + "/Usuario.svc/recuperarClave/" + strUser);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setConnectTimeout(10000);
                urlConnection.setReadTimeout(20000);
                urlConnection.setRequestProperty("Content-Type", "application/json");

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String response = convertStreamToString(in).trim();
                if (response.length()>0)
                {
                    if(response.equals("true"))
                    {
                        correoEnviado = true;
                    }
                    if(response.equals("false"))
                    {
                        correoEnviado = false;
                    }
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
            super.onPostExecute(aVoid);
            if(correoEnviado)
            {
                Toast.makeText(getApplicationContext(),"Tu contraseña fue enviada a tu buzón de correo", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), activity_sign_in.class);
                startActivity(intent);
            }
            else
            {
                Toast.makeText(getApplicationContext(),"No se encontró correo electrónico", Toast.LENGTH_LONG).show();
            }
            findViewById(R.id.loadingPanelRecuperarClave).setVisibility(View.GONE);
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
