package kr.co.mash_up.a5afe.data.remote;

import java.io.IOException;

import kr.co.mash_up.a5afe.common.Constants;
import kr.co.mash_up.a5afe.common.MosesApplication;
import kr.co.mash_up.a5afe.util.PreferencesUtils;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Http Request시  헤더에 쿠키를 넣어서 보낸다.
 */
public class AddCookiesInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();

        // Preference에서 cookie를 가져와 셋팅
        String accessToken = PreferencesUtils
                .getString(MosesApplication.getInstance(), Constants.PREF_ACCESS_TOKEN, "");
        builder.addHeader(Constants.ACCESS_TOKEN, accessToken);

        //Web, Android, IOS 구분을 위해 User-Agent 셋팅
        builder.removeHeader(Constants.USER_AGENT)
                .addHeader(Constants.USER_AGENT, "Android");

        return chain.proceed(builder.build());
    }
}
