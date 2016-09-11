package demo.java.com.myapplication2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class activity_opciones extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opciones);
    }

    public void regresarHome(View view) {
        finish();
    }

    public void cerrarSesion(View view) {
        SessionManager session = new SessionManager(getApplicationContext());
        session.logoutUser();
    }
}
