package kr.co.mash_up.a5afe.data.remote;


import kr.co.mash_up.a5afe.data.ServerBoolResult;

public interface ServerResultListener {
    void onSuccess();
    void onFailure();
}
