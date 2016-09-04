package kr.co.mash_up.a5afe.data.remote;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


public class MosesHttpInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request().newBuilder()
                .header("Accept", "application/json")
                .method(chain.request().method(), chain.request().body())
                .build();

        return chain.proceed(request);
    }
}
