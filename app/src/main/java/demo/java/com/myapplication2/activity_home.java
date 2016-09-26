package demo.java.com.myapplication2;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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

public class activity_home extends AppCompatActivity {

    Boolean usuarioObtenido = false;
    EditText etNombres, etApellidos, etProfesion, etCelular ;
    String strNombres, strApellidos, strProfesion, strCelular ;
    Boolean usuarioActualizado = false;
    Boolean encontroContactos = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        SessionManager session = new SessionManager(getApplicationContext());
        boolean sesionIniciada = session.isLoggedIn();
        if(sesionIniciada)
        {
            new HttpRequestObtenerDatosContacto().execute(session.getUserDetails().get("IdUsuario"));
        }else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    public void iniciarCompartir(View view) {
        Intent intent = new Intent(this, activity_compartir.class);
        startActivity(intent);
    }

    public void verContactos(View view) {
        Intent intent = new Intent(this, activity_contactos.class);
        startActivity(intent);
    }

    public void actualizarUsuario(View view) {
        etNombres = (EditText)findViewById(R.id.txtNombres);
        etApellidos = (EditText)findViewById(R.id.txtApellidos);
        etProfesion = (EditText)findViewById(R.id.txtCargo);
        etCelular = (EditText)findViewById(R.id.txtCelular);

        strNombres = etNombres.getText().toString();
        strApellidos = etApellidos.getText().toString();
        strProfesion = etProfesion.getText().toString();
        strCelular = etCelular.getText().toString();

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
        else
        {
            SessionManager session = new SessionManager(getApplicationContext());
            new HttpRequestActualizarUsuario().execute(strNombres, strApellidos, strProfesion, strCelular,session.getUserDetails().get("IdUsuario"));
        }
    }

    public void compartirEstaApp(View view) {
        Intent i=new Intent(android.content.Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(android.content.Intent.EXTRA_SUBJECT, "Te invito a usar ContactMe!");
        i.putExtra(android.content.Intent.EXTRA_TEXT, "Agrega contactos en solo segundos! Descagarla desde el Play Store como ContactMe http://bit.ly/2dat1Ft");
        startActivity(Intent.createChooser(i, "Compartir usando"));
    }

    public void ayudanosAMejorar(View view) {
        Intent intent = new Intent(this, activity_ayudanos.class);
        startActivity(intent);
    }

    public void verPoliticasPrivacidad(View view) {
        Intent intent = new Intent(this, activity_politicaseguridad.class);
        startActivity(intent);
    }

    public void mostrarOpciones(View view) {
        Intent intent = new Intent(this, activity_opciones.class);
        startActivity(intent);
    }

    public class HttpRequestObtenerDatosContacto extends AsyncTask<String,Void,Void> {
        String strIdUsuario = "";
        HttpURLConnection urlConnection = null;
        EN_Usuario objUsuario;

        @Override
        protected void onPreExecute() {
            findViewById(R.id.loadingPanelHome).setVisibility(View.VISIBLE);
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

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String response = convertStreamToString(in).trim();
                if (response.length()>0)
                {
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
                EditText txtNombres = (EditText) findViewById(R.id.txtNombres);
                txtNombres.setText(objUsuario.getNombres());

                EditText txtApellidos = (EditText) findViewById(R.id.txtApellidos);
                txtApellidos.setText(objUsuario.getApellidos());

                EditText txtCargo = (EditText) findViewById(R.id.txtCargo);
                txtCargo.setText(objUsuario.getCargo());

                EditText txtCelular = (EditText) findViewById(R.id.txtCelular);
                txtCelular.setText(objUsuario.getCelularFijo());
            }

            new HttpRequestAgregarUsuariosALibretaContactos().execute(strIdUsuario);
            super.onPostExecute(aVoid);
            findViewById(R.id.loadingPanelHome).setVisibility(View.GONE);
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

    public class HttpRequestActualizarUsuario extends AsyncTask<String,Void,Void> {
        String _strNombres, _strApellidos, _strProfesion, _strCelular, _strGUID;
        HttpURLConnection urlConnection = null;

        @Override
        protected void onPreExecute() {
            findViewById(R.id.loadingPanelHome).setVisibility(View.VISIBLE);
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
                        _strGUID = params[4];
                    }
                }
                URL url = new URL(getResources().getString(R.string.urlServicio) + "/Usuario.svc/actualizarUsuario");
                Log.i("response", getResources().getString(R.string.urlServicio) + "/Usuario.svc/actualizarUsuario");
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
                jsonObject.put("USERGUID", _strGUID);

                out.write(jsonObject.toString());
                out.close();

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String response = convertStreamToString(in).trim();

                if (response.length()>0)
                {
                    if(response.equals("true"))
                    {
                        usuarioActualizado = true;
                    }
                    if(response.equals("false"))
                    {
                        usuarioActualizado = false;
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
            findViewById(R.id.loadingPanelHome).setVisibility(View.GONE);
            if(usuarioActualizado)
            {
                Toast.makeText(getApplicationContext(), "Se actualizó correctamente", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(getApplicationContext(), "No se pudo actualizar", Toast.LENGTH_LONG).show();
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

    public class HttpRequestAgregarUsuariosALibretaContactos extends AsyncTask<String,Void,Void> {
        String strIdUsuario = "";
        HttpURLConnection urlConnection = null;
        ArrayList<EN_Usuario> objUsuarios = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            Log.i("SYNC", "INIT LIBRETA");

            try {
                if (params!=null)
                {
                    if(params.length > 0)
                    {
                        strIdUsuario = params[0];
                    }
                }

                URL url = new URL(getResources().getString(R.string.urlServicio) + "/Contacto.svc/actualizarContactosSinAgregar");
                Log.i("response", getResources().getString(R.string.urlServicio) + "/Contacto.svc/actualizarContactosSinAgregar");
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
            if(encontroContactos)
            {
                if(objUsuarios!=null) {
                    for (int i = 0; i < objUsuarios.size(); i++) {
                        EN_Usuario objUsuario = objUsuarios.get(i);
                        String DisplayName = objUsuario.getNombres() + " " + objUsuario.getApellidos();
                        String MobileNumber = objUsuario.getCelularFijo();
                        String HomeNumber = null;
                        String WorkNumber = null;
                        String emailID = objUsuario.getCorreo();
                        String company = "";
                        String jobTitle = objUsuario.getCargo();
                        ContactManager objContactManager = new ContactManager(DisplayName, MobileNumber, HomeNumber, WorkNumber, emailID,company, jobTitle, getApplicationContext());
                    }
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

    public void demo(View view) {
        Intent intent = new Intent(this, activity_compartircontactos.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() { }
}
