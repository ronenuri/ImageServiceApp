package imageservice.imageserviceapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
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

    public void stopServiceBtn(View view) {
        Intent intent = new Intent(this, ImageService.class);
        stopService(intent);
    }

    public void displayNotification(View view) {
        final int notify_id = 1;
        final NotificationManager NM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //final NotificationChannel channel = new NotificationChannel("default","default",NotificationManager.IMPORTANCE_DEFAULT );
        //NM.createNotificationChannel(channel);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1");
        builder.setSmallIcon(R.drawable.ic_launcher_foreground);
        builder.setContentTitle("Transferring photos");
        builder.setContentText("Transfer in progress");
        new Thread(new Runnable() {
            @Override
            public void run() {
                int j;
                for (j = 0; j <= 100; j += 5) {
                    builder.setProgress(100, j, false);
                    NM.notify(notify_id, builder.build());
                    try {
                        Thread.sleep(2 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                builder.setProgress(0, 0, false);
                builder.setContentText("Done");
                NM.notify(notify_id, builder.build());
            }
        }).start();
    }
}

