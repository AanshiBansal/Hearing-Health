package com.example.hearinghealth;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationService extends IntentService {
    /**
     * A constructor is required, and must call the super <code><a href="/reference/android/app/IntentService.html#IntentService(java.lang.String)">IntentService(String)</a></code>
     * constructor with a name for the worker thread.
     */

   NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "376")
            //.setSmallIcon(R.drawable.notification_icon)
            .setContentTitle("Too much Noise")
            .setContentText("It is bad for you")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true);

    public NotificationService() {
        super("Notification Service");
    }

    /**
     * The IntentService calls this method from the default worker thread with
     * the intent that started the service. When this method returns, IntentService
     * stops the service, as appropriate.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        // Normally we would do some work here, like download a file.
        // For our sample, we just sleep for 5 seconds.
        try {
            Thread.sleep(5000);
            Log.d("BGService","Check");
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(3, builder.build());
        } catch (InterruptedException e) {
            // Restore interrupt status.
            Thread.currentThread().interrupt();
        }
    }
}
