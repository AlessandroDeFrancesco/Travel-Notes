package com.capraraedefrancescosoft.progettomobidev.notifications;

/**
 * Created by Gianpaolo Caprara on 9/15/2016.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.capraraedefrancescosoft.progettomobidev.R;
import com.capraraedefrancescosoft.progettomobidev.activities.WallActivity;
import com.capraraedefrancescosoft.progettomobidev.models.JournalID;
import com.facebook.Profile;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    SharedPreferences sharedPref;

    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

//        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        sharedPref = getBaseContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        System.out.println("Notify : " + sharedPref.getBoolean(getString(R.string.preference_notifications), true));
        if (sharedPref.getBoolean(getString(R.string.preference_notifications), true)) {
            String messageType = remoteMessage.getData().get("type");
            if (messageType.equals("newJournal")) {
                sendNotification(remoteMessage.getData().get("body"), remoteMessage.getData().get("topic"), remoteMessage.getData().get("journal_name"), remoteMessage.getData().get("owner_id"));
            } else if (messageType.equals("newElement")){
                if(!remoteMessage.getData().get("owner_id_element").equals(Profile.getCurrentProfile().getId())) {
                    // manda la notifica se non e' quello che l'ha scritta
                    sendNotification(remoteMessage.getData().get("body"), remoteMessage.getData().get("topic"), remoteMessage.getData().get("journal_name"), remoteMessage.getData().get("owner_id"));
                    // manda il broadcast per aggiornare l'activity, se l'app e' aperta
                    Intent intent = new Intent(WallActivity.FIREBASE_MESSAGE_RECEIVED);
                    JournalID journalID = new JournalID(Long.parseLong(remoteMessage.getData().get("owner_id")), remoteMessage.getData().get("journal_name"));
                    intent.putExtra(WallActivity.EXTRA_JOURNAL, journalID);
                    getApplicationContext().sendBroadcast(intent);
                }
            }
        }
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String messageBody, String topic, String journal_name, String owner_id) {
        Intent intent = new Intent(this, WallActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(WallActivity.EXTRA_JOURNAL, new JournalID(Long.parseLong(owner_id), journal_name));
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Travel Notes")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (topic != null) {
            FirebaseMessaging.getInstance().subscribeToTopic(topic);
            System.out.println("Sottoscritto al topic " + topic);
        }
        notificationManager.notify(0, notificationBuilder.build());

    }
}
