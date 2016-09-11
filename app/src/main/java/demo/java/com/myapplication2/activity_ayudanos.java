package demo.java.com.myapplication2;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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

public class activity_ayudanos extends AppCompatActivity {

    EditText etComentario;
    String strComentario;
    Boolean comentarioEnviado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ayudanos);
    }

    public void regresarHome(View view) {
        finish();
    }

    public void enviarComentario(View view) {
        etComentario= (EditText)findViewById(R.id.txtComentario);

        strComentario = etComentario.getText().toString();
        if (strComentario.trim().length()==0)
        {
            Toast.makeText(getApplicationContext(), "Ingrese un comentario", Toast.LENGTH_LONG).show();
        }
        else
        {
            SessionManager session = new SessionManager(getApplicationContext());
            new HttpRequestEnviarComentario().execute(strComentario, session.getUserDetails().get("IdUsuario"));
        }
    }

    public class HttpRequestEnviarComentario extends AsyncTask<String,Void,Void> {
        String _strComentario, _strGUID;
        HttpURLConnection urlConnection = null;

        @Override
        protected void onPreExecute() {
            findViewById(R.id.loadingPanelAyudanos).setVisibility(View.VISIBLE);
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
                        _strComentario = params[0];
                        _strGUID = params[1];
                    }
                }
                URL url = new URL(getResources().getString(R.string.urlServicio) + "/Comentario.svc/guardarComentario");
                Log.i("response", getResources().getString(R.string.urlServicio) + "/Comentario.svc/guardarComentario");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setConnectTimeout(10000);
                urlConnection.setReadTimeout(20000);
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json");

                OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("Comentario", _strComentario);
                jsonObject.put("GUID", _strGUID);

                out.write(jsonObject.toString());
                out.close();

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String response = convertStreamToString(in).trim();

                if (response.length()>0)
                {
                    if(response.equals("true"))
                    {
                        comentarioEnviado = true;
                    }
                    if(response.equals("false"))
                    {
                        comentarioEnviado = false;
                    }
                }
                else
                {

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
            findViewById(R.id.loadingPanelAyudanos).setVisibility(View.GONE);
            if(comentarioEnviado)
            {
                findViewById(R.id.formularioComentario).setVisibility(View.GONE);
                findViewById(R.id.mensajeGraciasComentario).setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "Gracias por tu comentario!", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(getApplicationContext(), "No se pudo enviar", Toast.LENGTH_LONG).show();
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
}
