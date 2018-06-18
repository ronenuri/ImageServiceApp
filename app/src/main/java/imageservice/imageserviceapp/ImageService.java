package imageservice.imageserviceapp;

import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

public class ImageService extends Service {

    private BroadcastReceiver receiver;
    private final IntentFilter filter = new IntentFilter();
    private TCPClient client;

    @Override
    public void onCreate() {
        super.onCreate();
        this.client = new TCPClient();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Service Started!", Toast.LENGTH_LONG).show();
        // Add wifi connection and change filter
        filter.addAction("android.net.wifi.supplicant.CONNECTION_CHANGE");
        filter.addAction("android.net.wifi.STATE_CHANGE");
        // Definge new reciver
        this.receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (networkInfo != null) {
                    if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                        if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                            startTransfer();
                        }
                    }
                }
            }
        };
        // Register the reciver for every wifi connected signal
        this.registerReceiver(this.receiver, this.filter);
        return START_STICKY;
    }

    private void startTransfer() {
        String fileSizeAndName;
        int count = 0;
        Toast.makeText(this, "Wi-fi connected.\n Transferring photos...", Toast.LENGTH_SHORT).show();
        // Get all of the phones pics
        File dcim = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File[] pics = dcim.listFiles();
        if (pics != null) {
            // Setting up our progress bar
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");
            builder.setContentTitle("Transferring Pictures")
                    .setContentText("Download in progress")
                    .setPriority(NotificationCompat.PRIORITY_LOW);
            builder.setProgress(pics.length, count, false);
            notificationManager.notify(1,builder.build());
            for (File pic : pics) {
//                try {
//                    // Turn our image into a byte array from bitmap
//                    FileInputStream fis = new FileInputStream(pic);
//                    Bitmap bm = BitmapFactory.decodeStream(fis);
//                    byte[] imgByte = getBytesFromBitmap(bm);
//                    fileSizeAndName = String.valueOf(imgByte.length) + " " + pic.getName();
//                    // Send the photos size and name to the pc ImageService
//                    this.client.sendData(fileSizeAndName.getBytes());
//                    Thread.sleep(100);
//                    // Send the actuall photo to the pc ImageService
//                    this.client.sendData(imgByte);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
                // Updating and notifying the progress each image
                count++;
                builder.setProgress(pics.length, count, false);
                notificationManager.notify(1,builder.build());
            }
            builder.setContentText("Transfer complete!")
                    .setProgress(0, 0, false);
            notificationManager.notify(1, builder.build());
        }
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service Stopped :(", Toast.LENGTH_LONG).show();
        this.client.closeClient();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 70, stream);
        return stream.toByteArray();
    }
}
