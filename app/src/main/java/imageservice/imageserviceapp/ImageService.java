package imageservice.imageserviceapp;

import android.app.NotificationChannel;
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
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

public class ImageService extends Service {

    private BroadcastReceiver receiver;
    private final IntentFilter filter = new IntentFilter();
    private boolean firstTransfer;
    // Static member for simply telling if the service is running or not
    public static boolean serviceRunning = false;

    @Override
    public void onCreate() {
        super.onCreate();
        this.firstTransfer = true;
        serviceRunning = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Service Started!", Toast.LENGTH_LONG).show();
        // Add wifi connection and change filter
        filter.addAction("android.net.wifi.supplicant.CONNECTION_CHANGE");
        filter.addAction("android.net.wifi.STATE_CHANGE");
        // Define a new reciver for wifi connection status change
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
        // Register the receiver for every wifi connected signal
        this.registerReceiver(this.receiver, this.filter);
        return START_STICKY;
    }

    private void startTransfer() {
        Toast.makeText(this, "Wi-fi connected.\n Transferring photos...", Toast.LENGTH_SHORT).show();
        // Get all of the phones pics
        File dcim = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera");
        final File[] pics = dcim.listFiles();
        if ((pics != null) && firstTransfer) {
            this.firstTransfer = false;
            // Setting up our progress bar
            final NotificationManager NM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            final NotificationChannel channel;
            // Creating a channel if api is 26 or higher
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                channel = new NotificationChannel("default", "default", NotificationManager.IMPORTANCE_DEFAULT);
                NM.createNotificationChannel(channel);
            }
            final NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");
            builder.setSmallIcon(R.drawable.ic_launcher_foreground);
            builder.setContentTitle("Transferring photos...");
            builder.setContentText("Transfer in progress");
            builder.setDefaults(0);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    TCPClient client = new TCPClient();
                    int count = 0;
                    String fileSizeAndName;
                    for (File pic : pics) {
                        try {
                            // Turn our image into a byte array from bitmap
                            FileInputStream fis = new FileInputStream(pic);
                            Bitmap bm = BitmapFactory.decodeStream(fis);
                            byte[] imgByte = getBytesFromBitmap(bm);
                            fileSizeAndName = String.valueOf(imgByte.length) + " " + pic.getName() + " ";
                            // Send the photos size and name to the pc ImageService
                            client.sendData(fileSizeAndName.getBytes());
                            Thread.sleep(100);
                            // Send the actual photo to the pc ImageService
                            client.sendData(imgByte);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                         // Updating the progress with each image sent
                        count++;
                        builder.setProgress(pics.length, count, false);
                        NM.notify(1, builder.build());
                    }
                    // Lastly notifying the end of the transfer and closing our socket
                    builder.setContentText("Transfer complete!");
                    builder.setProgress(0, 0, false);
                    NM.notify(1, builder.build());
                    client.closeClient();
                    firstTransfer = true;
                }
            }).start();
        }
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service Stopped :(", Toast.LENGTH_LONG).show();
        // Stopping our service and announcing it stopped
        serviceRunning = false;
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // Getting a byte array from a given bitmap
    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // Compressing it using PNG format
        bitmap.compress(Bitmap.CompressFormat.PNG, 70, stream);
        return stream.toByteArray();
    }
}
