package com.tfnsnproject.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import com.tfnsnproject.R;
import com.tfnsnproject.to.MediaCheckin;
import com.tfnsnproject.util.ApiClient;

import java.io.FileNotFoundException;

public class CheckinService extends IntentService {

    public static final String CHECKIN = "com.tfnsnproject.intent.CHECKIN";

    ApiClient apiClient = ApiClient.getInstance();

    public CheckinService() {
        super("CheckinService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        MediaCheckin checkin = (MediaCheckin) intent.getParcelableExtra(CHECKIN);
        try {
            setNetworkIndicator(true);
            apiClient.checkinWithMedia(checkin.getAuthToken(), checkin.getMessage(),
                    getContentResolver().openInputStream(checkin.getMedia()), 33d, 44d);
        } catch (FileNotFoundException e) {
        } finally {
            setNetworkIndicator(false);
        }

    }

    public void setNetworkIndicator(boolean state) {

        Context context = getApplicationContext();
        NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        if (state == false) {
            nm.cancel(444);
            return;
        }

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT);
        Notification n = new Notification(android.R.drawable.stat_notify_sync, null, System.currentTimeMillis());
        n.setLatestEventInfo(context, "SMR7", "Network Communication", contentIntent);
        n.flags |= Notification.FLAG_ONGOING_EVENT;
        n.flags |= Notification.FLAG_NO_CLEAR;
        nm.notify(444, n);
    }
}
