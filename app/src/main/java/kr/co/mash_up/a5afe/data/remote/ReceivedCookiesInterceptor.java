package kr.co.mash_up.a5afe.data.remote;

import java.io.IOException;

import kr.co.mash_up.a5afe.common.Constants;
import kr.co.mash_up.a5afe.common.MosesApplication;
import kr.co.mash_up.a5afe.util.PreferencesUtils;
import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Response시 헤더에 있는 쿠키를 빼내어 Preferences에 저장한다.
 */
public class ReceivedCookiesInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());

        // 헤더에 Access Token 정보가 있으면 Preferences에 저장
        if (!originalResponse.header(Constants.ACCESS_TOKEN).isEmpty()) {
            PreferencesUtils
                    .putString(MosesApplication.getInstance(),
                            Constants.ACCESS_TOKEN,
                            originalResponse.header(Constants.ACCESS_TOKEN));
        }

        return originalResponse;
    }
}
