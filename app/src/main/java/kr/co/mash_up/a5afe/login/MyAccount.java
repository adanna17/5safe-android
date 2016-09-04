package kr.co.mash_up.a5afe.login;


public class MyAccount {

    private static MyAccount instance;

    private String kakaoId = "";

    public static MyAccount getInstance() {
        if (instance == null) {
            instance = new MyAccount();
        }
        return instance;
    }

    public String getKakaoId() {
        return kakaoId;
    }

    public void setKakaoId(String kakaoId) {
        this.kakaoId = kakaoId;
    }
}
