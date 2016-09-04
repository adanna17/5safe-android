package kr.co.mash_up.a5afe.data.remote;


import kr.co.mash_up.a5afe.BuildConfig;
import kr.co.mash_up.a5afe.data.ServerBoolResult;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class BackendHelper {

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

    public Call<ServerBoolResult> registerUser(String kakaoId) {
        return service.registerUser(kakaoId);
    }

    public Call<ServerBoolResult> sendCoordinate(String kakaoId,
                                                 double latitude,
                                                 double longitude) {
        return service.sendCoordinate(kakaoId, latitude, longitude);
    }

    public Call<ServerBoolResult> sendRegistration(String kakaoId,
                                                   String token) {
        return service.sendRegistration(kakaoId, token);
    }
}
