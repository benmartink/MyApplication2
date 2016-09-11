package demo.java.com.myapplication2;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

public class activity_sign_in extends AppCompatActivity {

    EditText etUserName,etPass;
    Button btnSingIn;
    String usr, pwd;
    Boolean loginOk = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
    }

    public void iniciarSesion(View view) {
        etUserName = (EditText)findViewById(R.id.etUserName);
        etPass = (EditText)findViewById(R.id.etPass);
        usr = etUserName.getText().toString();
        pwd = etPass.getText().toString();
        if (usr.trim().length()!=0 && pwd.trim().length()!=0)
        {
            int rows = 0;
            try {

            }catch (Exception ex)
            {
                rows = 0;
            }

        }
        else
        {
            Toast.makeText(getApplicationContext(), "El Usuario y/o Contraseña son inconrrectos.", Toast.LENGTH_LONG).show();
        }
        new HttpRequestObtenerUsuario().execute(usr, pwd);
    }

    public class HttpRequestObtenerUsuario extends AsyncTask<String,Void,Void> {
        String strUser = "";
        String strPwd ="";
        HttpURLConnection urlConnection = null;
        String strUrl = "";
        EN_Usuario objUsuario;

        @Override
        protected void onPreExecute() {
            findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
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
                        strPwd = params[1];
                    }
                }
                URL url = new URL(getResources().getString(R.string.urlServicio) + "/Usuario.svc/iniciarSesion");
                Log.i("response", getResources().getString(R.string.urlServicio) + "/Usuario.svc/iniciarSesion");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setConnectTimeout(10000);
                urlConnection.setReadTimeout(20000);
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json");

                OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("Usuario", strUser);
                jsonObject.put("Clave", strPwd);
                out.write(jsonObject.toString());
                out.close();

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String response = convertStreamToString(in).trim();
                if (response.length()>0)
                {
                    JSONObject x = new JSONObject(response);
                    objUsuario = EN_Usuario.fromJson(x);
                    if (objUsuario!=null) {
                        loginOk = true;
                    }else
                    {
                        loginOk = false;
                    }
                }
                else
                {
                    loginOk = false;
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
            if(loginOk)
            {
                SessionManager session = new SessionManager(getApplicationContext());
                Log.i("nombres: ", objUsuario.getNombres() + " " + objUsuario.getApellidos());
                Log.i("correo: ", objUsuario.getCorreo());
                session.createLoginSession(objUsuario.getNombres() + " " + objUsuario.getApellidos(), objUsuario.getCorreo(), objUsuario.getGUID() + "");
                Intent intent = new Intent(getApplicationContext(), activity_home.class);
                startActivity(intent);
            }
            else
            {
                findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(),"Usuario / clave incorrectos", Toast.LENGTH_LONG).show();
            }
            super.onPostExecute(aVoid);
            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
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