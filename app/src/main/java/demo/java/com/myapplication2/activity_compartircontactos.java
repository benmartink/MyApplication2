package demo.java.com.myapplication2;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class activity_compartircontactos extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compartircontactos);

    }

    public void demo(View view) {

        new funcionAsincrona().execute();
    }

    private class funcionAsincrona extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            Log.i("SYNC", "INIT");

            try {

                // Instantiate the custom HttpClient
                DefaultHttpClient client = new MyHttpClient(getApplicationContext());

                try {
                    HttpGet get = new HttpGet("https://192.168.1.50/Ubicacion.svc/obtenerUbicaciones/-11,9825696/-77,0946649/1,5/2");
// Execute the GET call and obtain the response
                    HttpResponse getResponse = client.execute(get);
                    HttpEntity responseEntity = getResponse.getEntity();

                }catch (Exception e) {
                    Log.e("YOUR_APP_LOG_TAG", "I got an error", e);
                    e.printStackTrace();
                    //Log.i("error", e.getMessage());
                    //Log.i("detalle error", String.valueOf(e.getStackTrace()));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

}
