package kr.co.mash_up.a5afe.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

import kr.co.mash_up.a5afe.data.ServerBoolResult;
import kr.co.mash_up.a5afe.data.remote.BackendHelper;
import kr.co.mash_up.a5afe.login.MyAccount;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 앱 프로세스 실행 여부에 관계없이 받아야 한다.
 * 자신이 받은 위치 정보를 서버에 전송
 */
public class LocationReceiver extends BroadcastReceiver {

    private static final String TAG = LocationReceiver.class.getSimpleName();

    public LocationReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //인텐트에 위치정보 데이터가 있으면 사용
        Location location = intent.getParcelableExtra(LocationManager.KEY_LOCATION_CHANGED);
        if (location != null) {
            onLocationReceived(context, location);
            return;
        }

        //위치 제공자의 사용 가능 여부가 있으면 사용
        if (intent.hasExtra(LocationManager.KEY_PROVIDER_ENABLED)) {
            boolean enabled = intent.getBooleanExtra(LocationManager.KEY_PROVIDER_ENABLED, false);
            onProviderEnabledChanged(enabled);
        }
    }

    /**
     * 위치 제공자 이름, 위도, 경로를 로그에 기록
     *
     * @param context
     * @param loc
     */
    protected void onLocationReceived(Context context, Location loc) {
        String provider = loc.getProvider();
        double latitude = loc.getLatitude();  //위도
        double longitude = loc.getLongitude();  //경도

        Log.d(TAG, this + " Got location from " + provider + " " + latitude + ", " + longitude);
//        Toast.makeText(context, provider + " " + latitude + ", " + longitude, Toast.LENGTH_SHORT).show();

        //서버에 전송
        BackendHelper backendHelper = BackendHelper.getInstance();
        String kakaoId = MyAccount.getInstance().getKakaoId();
        Call<ServerBoolResult> call = backendHelper.sendCoordinate(kakaoId, latitude, longitude);
        call.enqueue(new Callback<ServerBoolResult>() {
            @Override
            public void onResponse(Call<ServerBoolResult> call, Response<ServerBoolResult> response) {
                Log.d("Coordinate", "Coordinate send result success");
            }

            @Override
            public void onFailure(Call<ServerBoolResult> call, Throwable t) {
                Log.e("Coordinate", " " + t.getMessage());
            }
        });
    }

    protected void onProviderEnabledChanged(boolean enabled) {
        Log.d(TAG, "Provider " + (enabled ? "enabled" : "disabled"));
    }
}
