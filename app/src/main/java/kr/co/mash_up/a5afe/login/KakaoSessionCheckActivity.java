package kr.co.mash_up.a5afe.login;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.kakao.auth.ErrorCode;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.helper.log.Logger;

import kr.co.mash_up.a5afe.MainActivity;
import kr.co.mash_up.a5afe.data.ServerBoolResult;
import kr.co.mash_up.a5afe.data.remote.BackendHelper;
import kr.co.mash_up.a5afe.data.remote.ServerResultListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KakaoSessionCheckActivity extends AppCompatActivity {

    public static final String TAG = KakaoSessionCheckActivity.class.getSimpleName();

    private MyAccount mMyAccount;

    /**
     * 유효한 세션이 있다는 검증 후
     * me를 호출하여 가입 여부에 따라 가입 페이지를 그리던지 Main 페이지로 이동 시킨다.
     * Main으로 넘길지 가입 페이지를 그릴지 판단하기 위해 me를 호출한다.
     *
     * @param savedInstanceState 기존 session 정보가 저장된 객체
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMyAccount = MyAccount.getInstance();
        requestMe();
    }

    /**
     * 사용자의 상태를 알아 보기 위해 me API 호출을 한다.
     */
    protected void requestMe() {
        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                String message = "failed to get user info. msg=" + errorResult;
                Logger.d(message);

                ErrorCode result = ErrorCode.valueOf(errorResult.getErrorCode());
                if (result == ErrorCode.CLIENT_ERROR_CODE) {
                    Log.v(TAG, "네트워크 불안정");
                    finish();
                } else {
                    redirectLoginActivity();
                }
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                Log.v(TAG, "세션 닫힘");
                redirectLoginActivity();
            }

            @Override
            public void onSuccess(UserProfile userProfile) {
                mMyAccount.setKakaoId(String.valueOf(userProfile.getId()));

                //Todo: Presenter로 옮긴다.
                BackendHelper backendHelper = BackendHelper.getInstance();
                backendHelper.registerUser(mMyAccount.getKakaoId(), new ServerResultListener() {
                    @Override
                    public void onSuccess() {
                        redirectMainActivity();
                    }

                    @Override
                    public void onFailure() {
                        redirectMainActivity();
                    }
                });
            }

            @Override
            public void onNotSignedUp() {
                Log.v(TAG, "회원가입이 필요합니다.");
            }
        });
    }

    private void redirectMainActivity() {
        startActivity(new Intent(KakaoSessionCheckActivity.this, MainActivity.class));
        finish();
    }

    private void redirectLoginActivity() {
        Toast.makeText(getApplicationContext(), "로그인에 실패하셨습니다.", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(KakaoSessionCheckActivity.this, LoginActivity.class));
        finish();
    }

}
