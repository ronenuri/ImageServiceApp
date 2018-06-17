package imageservice.imageserviceapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startServiceBtn(View view) {
        Intent intent = new Intent(this, ImageService.class);
        startService(intent);
    }
\\check
    public void stopServiceBtn(View view) {
        Intent intent = new Intent(this, ImageService.class);
        stopService(intent);
    }
}
