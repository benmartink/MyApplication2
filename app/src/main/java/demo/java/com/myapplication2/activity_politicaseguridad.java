package demo.java.com.myapplication2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class activity_politicaseguridad extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_politicaseguridad);
    }

    public void regresarHome(View view) {
        finish();
    }

    @Override
    public void onBackPressed() { }
}
