package demo.java.com.myapplication2;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Session Manager
        SessionManager session = new SessionManager(getApplicationContext());
        boolean sesionIniciada = session.isLoggedIn();
        if(sesionIniciada)
        {
            Intent intent = new Intent(this, activity_home.class);
            startActivity(intent);
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
    }

    public void abrirIniciarSesion(View view) {
        Intent intent = new Intent(this, activity_sign_in.class);
        startActivity(intent);
    }

    public void abrirRegistrate(View view) {
        Intent intent = new Intent(this, activity_sign_up.class);
        startActivity(intent);
    }
}
