package kr.co.mash_up.a5afe.gcm;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import kr.co.mash_up.a5afe.alert.AlarmAlertActivity;
import kr.co.mash_up.a5afe.alert.AlarmAlertWakeLock;

public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = MyGcmListenerService.class.getSimpleName();

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);

        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }

        // [START_EXCLUDE]
        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */
        startWarning();
        // [END_EXCLUDE]
    }
    // [END receive_message]

    /**
     * 경보 시작
     */
    private void startWarning() {

        AlarmAlertWakeLock.acquireScreenCpuWakeLock(this);

        Intent alarmActivityIntent = new Intent(this, AlarmAlertActivity.class);
        alarmActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(alarmActivityIntent);
    }


}
