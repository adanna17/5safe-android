package kr.co.mash_up.a5afe.login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

import kr.co.mash_up.a5afe.R;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = LoginActivity.class.getSimpleName();
    private ISessionCallback mISessionCallback;

    /**
     * 로그인 버튼을 클릭 했을시 access token을 요청하도록 설정한다.
     *
     * @param savedInstanceState 기존 session 정보가 저장된 객체
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mISessionCallback = new ISessionCallback() {
            @Override
            public void onSessionOpened() {
                Log.v(TAG, "SessionOpen");
                Intent intent = new Intent(getApplicationContext(), KakaoSessionCheckActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onSessionOpenFailed(KakaoException exception) {
                if (exception != null) {
                    Logger.e(exception);
                }
                setContentView(R.layout.activity_login);
            }
        };

        Session.getCurrentSession().addCallback(mISessionCallback);
        if (!Session.getCurrentSession().checkAndImplicitOpen()) {
            setContentView(R.layout.activity_login);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(mISessionCallback);
    }
}
