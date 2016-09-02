package kr.co.mash_up.a5afe.alert;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import kr.co.mash_up.a5afe.R;


public class AlarmAlertActivity extends AppCompatActivity {

    public static final String TAG = AlarmAlertActivity.class.getSimpleName();

    public static final String ALARM_SNOOZE_ACTION = "kr.co.mash_up.a5afe.ALARM_SNOOZE";
    public static final String ALARM_DISMISS_ACTION = "kr.co.mash_up.a5afe.ALARM_DISMISS";

    ImageView ivGif;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        AlarmAlertWakeLock.acquireScreenCpuWakeLock(AlarmAlertActivity.this);

        setContentView(R.layout.activity_alarm);

        loadGif();

        //Alarm start
        AlarmKlaxon.start(AlarmAlertActivity.this);
    }

    private void loadGif() {
        ivGif = (ImageView) findViewById(R.id.imageView_gif);
        Glide.with(this)
                .load(R.drawable.road)
                .asGif()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(ivGif);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        AlarmAlertWakeLock.releaseCpuLock();
    }

    @Override
    protected void onDestroy() {
        AlarmKlaxon.stop(AlarmAlertActivity.this);
        super.onDestroy();
    }
}
