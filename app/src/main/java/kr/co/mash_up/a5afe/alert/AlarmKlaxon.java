package kr.co.mash_up.a5afe.alert;

import android.Manifest;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.RawRes;
import android.support.annotation.RequiresPermission;
import android.util.Log;

import java.io.IOException;

import kr.co.mash_up.a5afe.R;

/**
 * 알람소리, 진동 울리는 역할
 */
public class AlarmKlaxon {

    public static final String TAG = AlarmKlaxon.class.getSimpleName();

    // (지연시간, 진동, 쉼, 진동, 쉼, ...)
    private static final long[] sVibratePattern = new long[]{0, 2000, 200, 2000, 200};  //진동 패턴

    private static AudioAttributes VIBRATION_ATTRIBUTES;

    private static boolean startedAlarm = false;
    private static boolean startedVibrator = false;

    private static MediaPlayer sMediaPlayer = null;

    /**
     * 알림 소리 중지
     *
     * @param context
     */
    public static void stop(Context context) {
        Log.d(TAG, " stop()");

        if (startedAlarm) {
            startedAlarm = false;

            // stop audio playing
            if (sMediaPlayer != null) {
                sMediaPlayer.stop();
                AudioManager audioManager = (AudioManager)
                        context.getSystemService(Context.AUDIO_SERVICE);
                audioManager.abandonAudioFocus(null);
                sMediaPlayer.release();
                sMediaPlayer = null;
            }

            // stop vibrator
            stopVibrator(context);
        }
    }

    /**
     * 알림 소리 시작
     *
     * @param context
     */
    public static void start(final Context context) {
        Log.d(TAG, " start()");

        // Make sure we are stop before starting
        stop(context);

        sMediaPlayer = new MediaPlayer();
        sMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.e(TAG, "AlarmKlaxon mediaPlayer error");
                AlarmKlaxon.stop(context);
                return true;
            }
        });

        try {
            setDataSourceFromResource(context, sMediaPlayer, R.raw.emergency006);
            startAlarm(context, sMediaPlayer);

        } catch (Exception e) {
            Log.d(TAG, " using the fallback ringtone");

            try {
                sMediaPlayer.reset();
                setDataSourceFromResource(context, sMediaPlayer, R.raw.emergency006);
                startAlarm(context, sMediaPlayer);
            } catch (Exception e2) {
                Log.e(TAG, " failed to play fallback ringtone", e2);
            }
        }

        //진동 울리는가
        startVibrator(context);

        startedAlarm = true;
    }

    /**
     * 진동 시작
     *
     * @param context
     */
    @RequiresPermission(value = Manifest.permission.VIBRATE)
    public static void startVibrator(Context context) {

        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            VIBRATION_ATTRIBUTES = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build();

            vibrator.vibrate(sVibratePattern, 0, VIBRATION_ATTRIBUTES);
        } else {
            vibrator.vibrate(sVibratePattern, 0);
        }

        startedVibrator = true;
    }

    /**
     * 진동 시작
     *
     * @param context
     * @param millisecond 진동 울릴 밀리초
     */
    public static void startVibrator(Context context, long millisecond) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(millisecond);
    }

    /**
     * 진동 중지
     *
     * @param context
     */
    public static void stopVibrator(Context context) {

        if (startedVibrator) {
            // stop vibrator
            ((Vibrator) (context.getSystemService(Context.VIBRATOR_SERVICE))).cancel();
            startedVibrator = false;
        }
    }

    private static void startAlarm(Context context, MediaPlayer player) throws IOException {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        //do not play alarms if stream volume is 0 (typically because ringer mode is silent)
        if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
            player.setAudioStreamType(AudioManager.STREAM_ALARM);
            player.setLooping(true);
            player.prepare();
            audioManager.requestAudioFocus(null, AudioManager.STREAM_ALARM, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
            player.start();
        }
    }

    /**
     * 소리 파일 load
     *
     * @param context res에 접근할 context
     * @param player  소리 재생할 미디어 플레이어
     * @param res     소리 파일 id
     * @throws IOException
     */
    private static void setDataSourceFromResource(@NonNull Context context, @NonNull MediaPlayer player, @RawRes int res) throws IOException {

        AssetFileDescriptor afd = context.getResources().openRawResourceFd(res);
        if (afd != null) {
            player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
        }
    }
}
