package kr.co.mash_up.a5afe.data.remote;


import kr.co.mash_up.a5afe.data.ServerBoolResult;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface BackendService {

    @FormUrlEncoded
    @POST("users/login")
    Call<ServerBoolResult> registerUser(
            @Field("user_kakao_id") String kakaoId
    );

    @FormUrlEncoded
    @POST("users/location")
    Call<ServerBoolResult> sendCoordinate(
            @Field("user_kakao_id") String kakaoId,
            @Field("latitude") double latitude,
            @Field("longitude") double longitude
    );

    @FormUrlEncoded
    @POST("users/device")
    Call<ServerBoolResult> sendRegistration(
            @Field("user_kakao_id") String kakaoId,
            @Field("device_id") String token
    );
}
