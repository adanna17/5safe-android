package kr.co.mash_up.a5afe.data.remote;


import android.util.Log;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.List;
import java.util.concurrent.TimeUnit;

import kr.co.mash_up.a5afe.BuildConfig;
import kr.co.mash_up.a5afe.common.Constants;
import kr.co.mash_up.a5afe.common.MosesApplication;
import kr.co.mash_up.a5afe.data.ServerBoolResult;
import kr.co.mash_up.a5afe.login.MyAccount;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

//Todo: KaKao Id의 역할을 암호화된 토큰으로 변경
public class BackendHelper {

    public static final String TAG = BackendHelper.class.getSimpleName();

    private static BackendHelper instance;
    private BackendService service;

    public static BackendHelper getInstance() {
        if (instance == null) {
            synchronized (BackendHelper.class) {
                if (instance == null) {
                    instance = new BackendHelper();
                }
            }
        }
        return instance;
    }

    private BackendHelper() {
        OkHttpClient okHttpClient = makeOkHttpClient(makeLoggingInterceptor());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        service = retrofit.create(BackendService.class);
    }

    /**
     * OkHttpClient 생성
     *
     * @param httpLoggingInterceptor http Logging Interceptor
     * @return 설정이 끝난 OkHttpClient
     */
    private OkHttpClient makeOkHttpClient(HttpLoggingInterceptor httpLoggingInterceptor) {
        return new OkHttpClient.Builder()
                .connectTimeout(Constants.CONNECT_TIMEOUT, TimeUnit.SECONDS)  //연결 타임아웃 설정
                .readTimeout(Constants.READ_TIMEOUT, TimeUnit.SECONDS)  //읽 타임아웃 설정
                .writeTimeout(Constants.WRITE_TIMEOUT, TimeUnit.SECONDS)  //쓰기 타임아웃 설정
                /*
                세션을 유지하는 쿠키관리 방법이 기존의 Interceptor를 사용하는 방식에서
                cookieJar를 이용해 CookieManager에 위임하도록 변경
                 */
                .cookieJar(new JavaNetCookieJar(makeCookieManager()))  //쿠키매니저 설정
                .addInterceptor(httpLoggingInterceptor)  //http 로깅 설정
                .addInterceptor(new MosesHttpInterceptor())
                .build();
    }

    /**
     * create http Logging Interceptor
     *
     * @return created http Logging Interceptor
     */
    private HttpLoggingInterceptor makeLoggingInterceptor() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor()
                .setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY
                        : HttpLoggingInterceptor.Level.NONE);
        return logging;
    }

    /**
     * create CookieManager
     *
     * @return Cookie Store, Policy 설정된 CookieManager
     */
    private CookieManager makeCookieManager() {
        return new CookieManager(
                new PersistentCookieStore(MosesApplication.getInstance()),
                CookiePolicy.ACCEPT_ALL);
    }

    /**
     * KaKao Oauth로 인증된 User 등록
     *
     * @param kakaoId  인증된 KaKaoId
     * @param callback 결과 콜백
     */
    public void registerUser(String kakaoId, final ServerResultListener callback) {
        Call<ServerBoolResult> call = service.registerUser(kakaoId);
        call.enqueue(new Callback<ServerBoolResult>() {
            @Override
            public void onResponse(Call<ServerBoolResult> call, Response<ServerBoolResult> response) {
                if (response.body().isbResult()) {
                    callback.onSuccess();
                } else {
                    callback.onFailure();
                }
            }

            @Override
            public void onFailure(Call<ServerBoolResult> call, Throwable t) {
                Log.e(TAG + " register user ", t.getMessage());
            }
        });
    }

    /**
     * 좌표(위도, 경도) 전송
     *
     * @param kakaoId   인증된 KaKaoId
     * @param latitude  위도
     * @param longitude 경도
     * @param callback  결과 콜백
     */
    public void sendCoordinate(String kakaoId,
                               double latitude,
                               double longitude,
                               final ServerResultListener callback) {
        Call<ServerBoolResult> call = service.sendCoordinate(kakaoId, latitude, longitude);
        call.enqueue(new Callback<ServerBoolResult>() {
            @Override
            public void onResponse(Call<ServerBoolResult> call, Response<ServerBoolResult> response) {
                if (response.body().isbResult()) {
                    callback.onSuccess();
                } else {
                    callback.onFailure();
                }
            }

            @Override
            public void onFailure(Call<ServerBoolResult> call, Throwable t) {
                Log.e(TAG + " Coordinate ", t.getMessage());
            }
        });
    }

    /**
     * GCM 인증 토큰 전송
     *
     * @param kakaoId  인증된 KaKaoId
     * @param token    GCM 인증 토큰
     * @param callback 결과 콜백
     */
    public void sendRegistration(String kakaoId,
                                 String token,
                                 final ServerResultListener callback) {
        Call<ServerBoolResult> call = service.sendRegistration(kakaoId, token);
        call.enqueue(new Callback<ServerBoolResult>() {
            @Override
            public void onResponse(Call<ServerBoolResult> call, Response<ServerBoolResult> response) {
                if (response.body().isbResult()) {
                    callback.onSuccess();
                } else {
                    callback.onFailure();
                }
            }

            @Override
            public void onFailure(Call<ServerBoolResult> call, Throwable t) {
                Log.e(TAG + " token ", t.getMessage());
            }
        });
    }
}
