package kr.co.mash_up.a5afe;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.RequiresPermission;
import android.util.Log;

/**
 * LocationManager와의 소통과 현재의 이동에 관한 더 자세한 것을 관리하기 위해서
 * 만든 싱글톤 클래스
 */
public class RunManager {

    private static final String TAG = RunManager.class.getSimpleName();

    public static final String ACTION_LOCATION = "kr.co.mash_up.a5afe.ACTION_LOCATION";  //앱에서 수신하기 위한 커스텀 액션

    private static final String TEST_PROVIDER = "test_provider";

    private static RunManager instance;

    private Context mContext;
    private LocationManager mLocationManager;

    @RequiresPermission(value = Manifest.permission.ACCESS_FINE_LOCATION)
    public static RunManager getInstance(Context context) {
        if (instance == null) {
            synchronized (RunManager.class) {
                if (instance == null) {
                    instance = new RunManager(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    private RunManager(Context context) {
        mContext = context;
        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        //Todo: Permission Check 추가
    }

    public void startLocationUpdates() {
        String provider = LocationManager.GPS_PROVIDER;

        //만일 테스트 제공자가 있고 동작 가능하면 사용
        if (mLocationManager.getProvider(TEST_PROVIDER) != null &&
                mLocationManager.isProviderEnabled(TEST_PROVIDER)) {
            provider = TEST_PROVIDER;
        }
        Log.d(TAG, " Using provider " + provider);

        //만일 마지막 인식 위치가 있으면 그것을 알아내어 브로드캐스팅
        Location lastKnown = mLocationManager.getLastKnownLocation(provider);
        if (lastKnown != null) {
            //시간을 현재로 재설정
            lastKnown.setTime(System.currentTimeMillis());
            broadcastLocation(lastKnown);
        }

        //LocationManager에게 위치 갱신 정보를 요청
        PendingIntent pi = getLocationPendingIntent(true);
        mLocationManager.requestLocationUpdates(provider, 0, 0, pi);
    }


    private void broadcastLocation(Location location) {
        Intent broadcastIntent = new Intent(ACTION_LOCATION);
        broadcastIntent.putExtra(LocationManager.KEY_LOCATION_CHANGED, location);
        mContext.sendBroadcast(broadcastIntent);
    }

    public void stopLocationUpdates() {
        PendingIntent pi = getLocationPendingIntent(false);
        if (pi != null) {
            mLocationManager.removeUpdates(pi);
            pi.cancel();
        }
    }

    /**
     * 위치 갱신 정보가 발생할 때 브로드캐스트되는 Intent를 생성
     * App에서 이벤트를 식별하기 위해 커스텀 액션 이름을 사용
     *
     * @param shouldCreate 시스템에서 새로운 PendingIntent를 생성해야 하는지의 여부를
     *                     PendingIntent.getBroadcast()에 (flags를 통해서) 알려준다.
     * @return
     */
    private PendingIntent getLocationPendingIntent(boolean shouldCreate) {
        Intent broadcastIntent = new Intent(ACTION_LOCATION);
        int flags = shouldCreate ? 0 : PendingIntent.FLAG_NO_CREATE;
        return PendingIntent.getBroadcast(mContext, 0, broadcastIntent, flags);
    }

    public void startTrackingRun() {


        startLocationUpdates();
    }

}
