package imageservice.imageserviceapp;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {

    private final int REQUEST_PERMISSION = 1;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        // Checking for storage permissions, asking if there are none
        if (ContextCompat.checkSelfPermission(context, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        int storagePermission = ContextCompat.checkSelfPermission(context, READ_EXTERNAL_STORAGE);
        if (storagePermission == PackageManager.PERMISSION_GRANTED) {
            finish();
            startActivity(getIntent());
        } else {
            finish();
        }
    }

    public void startServiceBtn(View view) {
        Intent intent = new Intent(this, ImageService.class);
        startService(intent);
    }

    public void stopServiceBtn(View view) {
        Intent intent = new Intent(this, ImageService.class);
        stopService(intent);
    }
}

