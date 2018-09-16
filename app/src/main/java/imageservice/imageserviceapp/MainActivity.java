package imageservice.imageserviceapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {

    private final int REQUEST_PERMISSION = 1;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // If the service is running
        if (ImageService.serviceRunning) {
            // We won't enable the start service button upon creation
            Button startBtn = findViewById(R.id.startServiceBtn);
            startBtn.setEnabled(false);
        } else {
            // Otherwise not enabling the stop service button
            Button stopBtn = findViewById(R.id.stopServiceBtn);
            stopBtn.setEnabled(false);
        }

        context = this;
        // Checking for storage permissions, asking if there are none
        if (ContextCompat.checkSelfPermission(context, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        int storagePermission = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            storagePermission = ContextCompat.checkSelfPermission(context, READ_EXTERNAL_STORAGE);
        }
        if (storagePermission == PackageManager.PERMISSION_GRANTED) {
            finish();
            startActivity(getIntent());
        } else {
            finish();
        }
    }

    public void startServiceBtn(View view) {
        Intent intent = new Intent(this, ImageService.class);
        // Disabling the start service button upon activation
        Button startBtn = findViewById(R.id.startServiceBtn);
        startBtn.setEnabled(false);
        // Enabling the stop service button only
        Button stopBtn = findViewById(R.id.stopServiceBtn);
        stopBtn.setEnabled(true);
        startService(intent);
    }

    public void stopServiceBtn(View view) {
        Intent intent = new Intent(this, ImageService.class);
        // Enabling the start service button upon closing the service
        Button startBtn = findViewById(R.id.startServiceBtn);
        startBtn.setEnabled(true);
        // Disabling the stop service button upon closing the service
        Button stopBtn = findViewById(R.id.stopServiceBtn);
        stopBtn.setEnabled(false);
        stopService(intent);
    }
}

