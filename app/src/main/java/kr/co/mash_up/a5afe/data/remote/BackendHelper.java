package kr.co.mash_up.a5afe.data.remote;


import android.util.Log;

import kr.co.mash_up.a5afe.BuildConfig;
import kr.co.mash_up.a5afe.data.ServerBoolResult;
import kr.co.mash_up.a5afe.login.MyAccount;
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

public class BackendHelper {

    public static final String TAG = BackendHelper.class.getSimpleName();
    private static final String BASE_URL = "http://172.20.10.2:3000/";

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
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        service = retrofit.create(BackendService.class);
    }

    private OkHttpClient makeOkHttpClient(HttpLoggingInterceptor httpLoggingInterceptor) {
        return new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .addInterceptor(new MosesHttpInterceptor())
                .build();
    }

    private HttpLoggingInterceptor makeLoggingInterceptor() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor()
                .setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY
                        : HttpLoggingInterceptor.Level.NONE);
        return logging;
    }

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
