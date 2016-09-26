package demo.java.com.myapplication2.CustomControls.AdapterContactosCercanos;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
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
import java.util.List;

import demo.java.com.myapplication2.EN_Usuario;
import demo.java.com.myapplication2.R;
import demo.java.com.myapplication2.SessionManager;

/**
 * Created by MARTIN on 19/08/2016.
 */
public class CustomAdapter extends BaseAdapter {

    Context context;
    List<RowItem> rowItem;
    Boolean usuarioAgregado = false;
    Boolean usuarioObtenido = false;

    CustomAdapter(Context context, List<RowItem> rowItem) {
        this.context = context;
        this.rowItem = rowItem;

    }

    @Override
    public int getCount() {

        return rowItem.size();
    }

    @Override
    public Object getItem(int position) {

        return rowItem.get(position);
    }

    @Override
    public long getItemId(int position) {

        return rowItem.indexOf(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.list_item, null);
        }

        TextView txtTitle = (TextView) convertView.findViewById(R.id.title);
        Button btn = (Button) convertView.findViewById(R.id.btnColor);

        final RowItem row_pos = rowItem.get(position);
        // setting the image resource and title

        txtTitle.setText(row_pos.getTitle());
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SessionManager session = new SessionManager(context.getApplicationContext());
                String IdUsuario = session.getUserDetails().get("IdUsuario");
                String IdContacto = row_pos.getIdItem() + "";
                new HttpRequest().execute(IdUsuario, IdContacto);
            }
        });

        return convertView;

    }

    public class HttpRequest extends AsyncTask<String,Void,Void> {
        String strIdUsuario = "";
        String strIdContacto ="";
        HttpURLConnection urlConnection = null;
        String strUrl = "";


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            Log.i("SYNC", "INIT");
            EN_Usuario objUsuario;
            try {
                if (params!=null)
                {
                    if(params.length > 0)
                    {
                        strIdUsuario = params[0];
                        strIdContacto = params[1];
                    }
                }
                URL url = new URL(context.getResources().getString(R.string.urlServicio) + "/Contacto.svc/guardarContacto");
                Log.i("response", context.getResources().getString(R.string.urlServicio) + "/Contacto.svc/guardarContacto");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setConnectTimeout(10000);
                urlConnection.setReadTimeout(20000);
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json");

                OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("IdUsuario", strIdUsuario);
                jsonObject.put("IdContacto", strIdContacto);
                out.write(jsonObject.toString());
                out.close();

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String response = convertStreamToString(in).trim();
                if (response.length()>0)
                {
                    JSONObject x = new JSONObject(response);
                    objUsuario = EN_Usuario.fromJson(x);
                    if (objUsuario!=null) {
                        usuarioAgregado = true;
                    }else
                    {
                        usuarioAgregado = false;
                    }
                }
                else
                {
                    usuarioAgregado = false;
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
            if(usuarioAgregado)
            {
                //new HttpRequestObtenerDatosContacto().execute(strIdContacto);
            }
            else
            {
                Toast.makeText(context.getApplicationContext(),"Ocurrió un error", Toast.LENGTH_LONG).show();
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
