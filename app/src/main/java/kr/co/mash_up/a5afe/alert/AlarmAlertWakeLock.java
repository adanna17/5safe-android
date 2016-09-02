package kr.co.mash_up.a5afe.alert;

import android.Manifest;
import android.content.Context;
import android.os.PowerManager;
import android.support.annotation.RequiresPermission;

/**
 * 잠들어있는 cpu, screen 깨우는 클래스
 */
public class AlarmAlertWakeLock {

    private static PowerManager.WakeLock sWakeLock = null;

    // cpu lock
    @RequiresPermission(value = Manifest.permission.WAKE_LOCK)  //필요한 퍼미션 명시
    public static void acquireCpuWakeLock(Context context){
        if(sWakeLock != null){
            return;
        }

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        sWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "wakelock new");
        sWakeLock.acquire();
    }

    // cpu, screen lock
    @RequiresPermission(value = Manifest.permission.WAKE_LOCK)
    public static void acquireScreenCpuWakeLock(Context context){
        if(sWakeLock != null){
            return;
        }

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        sWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "wakelock screen cpu");
//        sWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "wakelock screen cpu");

        sWakeLock.acquire();
    }

    // lock cancel
    public static void releaseCpuLock(){
        if(sWakeLock != null){
            sWakeLock.release();
            sWakeLock = null;
        }
    }
}
