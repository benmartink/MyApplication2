package demo.java.com.myapplication2;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

public class activity_sign_up extends AppCompatActivity {

    EditText etUserName, etPass, etNombres, etApellidos, etProfesion, etCelular,etRePass ;
    String strUserName, strPass, strNombres, strApellidos, strProfesion, strCelular,strRePass ;
    Boolean correoExiste = true;
    Boolean celularExiste = true;
    Boolean usuarioRegistrado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
    }

    public void registrarUsuario(View view) {
        etNombres = (EditText)findViewById(R.id.etNombres);
        etApellidos = (EditText)findViewById(R.id.etApellidos);
        etProfesion = (EditText)findViewById(R.id.etProfesion);
        etCelular = (EditText)findViewById(R.id.etCelular);
        etUserName = (EditText)findViewById(R.id.etUserName);
        etPass = (EditText)findViewById(R.id.etPass);
        etRePass = (EditText)findViewById(R.id.etRePass);

        strNombres = etNombres.getText().toString();
        strApellidos = etApellidos.getText().toString();
        strProfesion = etProfesion.getText().toString();
        strCelular = etCelular.getText().toString();
        strRePass = etRePass.getText().toString();
        strUserName = etUserName.getText().toString();
        strPass = etPass.getText().toString();

        if (strNombres.trim().length()==0)
        {
            Toast.makeText(getApplicationContext(), "Completar campo: Nombres", Toast.LENGTH_LONG).show();
        }
        else if (strApellidos.trim().length()==0)
        {
            Toast.makeText(getApplicationContext(), "Completar campo: Apellidos", Toast.LENGTH_LONG).show();
        }
        else if (strProfesion.trim().length()==0)
        {
            Toast.makeText(getApplicationContext(), "Completar campo: Profesión", Toast.LENGTH_LONG).show();
        }
        else if (strCelular.trim().length()==0)
        {
            Toast.makeText(getApplicationContext(), "Completar campo: Celular", Toast.LENGTH_LONG).show();
        }
        else if (strUserName.trim().length()==0)
        {
            Toast.makeText(getApplicationContext(), "Completar campo: Correo", Toast.LENGTH_LONG).show();
        }
        else if (strPass.trim().length()==0)
        {
            Toast.makeText(getApplicationContext(), "Completar campo: Contraseña", Toast.LENGTH_LONG).show();
        }
        else if (!strPass.trim().equals(strRePass.trim()))
        {
            Log.i("strPass", strPass);
            Log.i("strRePass", strRePass);
            Toast.makeText(getApplicationContext(), "Contraseñas no coinciden", Toast.LENGTH_LONG).show();
        }
        else
        {
            new HttpRequestValidarCorreo().execute(strNombres, strApellidos, strProfesion, strCelular, strUserName, strPass);
        }
    }

    public class HttpRequestValidarCorreo extends AsyncTask<String,Void,Void> {
        String _strUserName, _strPass, _strNombres, _strApellidos, _strProfesion, _strCelular;
        HttpURLConnection urlConnection = null;

        @Override
        protected void onPreExecute() {
            findViewById(R.id.loadingPanelRegistrar).setVisibility(View.VISIBLE);
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
                        _strNombres = params[0];
                        _strApellidos = params[1];
                        _strProfesion = params[2];
                        _strCelular = params[3];
                        _strUserName = params[4];
                        _strPass = params[5];
                    }
                }
                URL url = new URL(getResources().getString(R.string.urlServicio) + "/Usuario.svc/validarCorreoExiste/" + strUserName);
                Log.i("response", getResources().getString(R.string.urlServicio) + "/Usuario.svc/validarCorreoExiste/" + strUserName);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setConnectTimeout(10000);
                urlConnection.setReadTimeout(20000);
                //urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json");

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String response = convertStreamToString(in).trim();

                if (response.length()>0)
                {
                    if(response.equals("true"))
                    {
                        correoExiste = true;
                    }
                    if(response.equals("false"))
                    {
                        correoExiste = false;
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
            if(correoExiste)
            {
                findViewById(R.id.loadingPanelRegistrar).setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "El correo ingresado ya existe, intente con otro correo", Toast.LENGTH_LONG).show();
            }
            else
            {
                new HttpRequestValidarCelular().execute(strNombres, strApellidos, strProfesion, strCelular, strUserName, strPass);
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

    public class HttpRequestValidarCelular extends AsyncTask<String,Void,Void> {
        String _strUserName, _strPass, _strNombres, _strApellidos, _strProfesion, _strCelular;
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
                        _strNombres = params[0];
                        _strApellidos = params[1];
                        _strProfesion = params[2];
                        _strCelular = params[3];
                        _strUserName = params[4];
                        _strPass = params[5];
                    }
                }
                URL url = new URL(getResources().getString(R.string.urlServicio) + "/Usuario.svc/validarCelularExiste/" + strCelular);
                Log.i("response", getResources().getString(R.string.urlServicio) + "/Usuario.svc/validarCelularExiste/" + strCelular);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setConnectTimeout(10000);
                urlConnection.setReadTimeout(20000);
                //urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json");

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String response = convertStreamToString(in).trim();

                if (response.length()>0)
                {
                    if(response.equals("true"))
                    {
                        celularExiste = true;
                    }
                    if(response.equals("false"))
                    {
                        celularExiste = false;
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
            if(celularExiste)
            {
                findViewById(R.id.loadingPanelRegistrar).setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "El celular ingresado ya existe, intente con otro celular", Toast.LENGTH_LONG).show();
            }
            else
            {
                new HttpRequestRegistrarUsuario().execute(strNombres, strApellidos, strProfesion, strCelular, strUserName, strPass);
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

    public class HttpRequestRegistrarUsuario extends AsyncTask<String,Void,Void> {
        String _strUserName, _strPass, _strNombres, _strApellidos, _strProfesion, _strCelular;
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
                        _strNombres = params[0];
                        _strApellidos = params[1];
                        _strProfesion = params[2];
                        _strCelular = params[3];
                        _strUserName = params[4];
                        _strPass = params[5];
                    }
                }
                URL url = new URL(getResources().getString(R.string.urlServicio) + "/Usuario.svc/registrarUsuario");
                Log.i("response", getResources().getString(R.string.urlServicio) + "/Usuario.svc/registrarUsuario");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setConnectTimeout(10000);
                urlConnection.setReadTimeout(20000);
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json");

                OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("Nombres", _strNombres);
                jsonObject.put("Apellidos", _strApellidos);
                jsonObject.put("Profesion", _strProfesion);
                jsonObject.put("Celular", _strCelular);
                jsonObject.put("Correo", _strUserName);
                jsonObject.put("Clave", _strPass);
                out.write(jsonObject.toString());
                out.close();

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String response = convertStreamToString(in).trim();

                if (response.length()>0)
                {
                    if(response.equals("true"))
                    {
                        usuarioRegistrado = true;
                    }
                    if(response.equals("false"))
                    {
                        usuarioRegistrado = false;
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
            findViewById(R.id.loadingPanelRegistrar).setVisibility(View.GONE);
            if(usuarioRegistrado)
            {
                findViewById(R.id.formularioRegistro).setVisibility(View.GONE);
                findViewById(R.id.mensajeConfirmacion).setVisibility(View.VISIBLE);
                //Toast.makeText(getApplicationContext(), "El celular ingresado ya existe, intente con otro celular", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(getApplicationContext(), "No se pudo registrar al usuario", Toast.LENGTH_LONG).show();
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

    public void abrirIniciarSesion(View view) {
        Intent intent = new Intent(this, activity_sign_in.class);
        startActivity(intent);
    }
}
