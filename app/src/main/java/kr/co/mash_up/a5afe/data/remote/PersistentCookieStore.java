package kr.co.mash_up.a5afe.data.remote;

import android.content.Context;
import android.text.TextUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import kr.co.mash_up.a5afe.util.PreferencesUtils;

/**
 * Sample
 */
public class PersistentCookieStore implements CookieStore {

    public static final String TAG = PersistentCookieStore.class.getSimpleName();
    private static final String COOKIE_NAME_PREFIX = "cookie_okhttp_";

    private final HashMap<String, ConcurrentHashMap<String, HttpCookie>> cookies;
    private final Context mContext;

    public PersistentCookieStore(Context context) {
        mContext = context;
        cookies = new HashMap<>();

        // Load any previously stored cookies into the store
        Map<String, ?> prefsMap = PreferencesUtils.getAll(mContext);  //키를 전부 받아온다.
        for (Map.Entry<String, ?> entry : prefsMap.entrySet()) {
            if (entry.getValue() != null
                    && entry.getValue().toString().startsWith(COOKIE_NAME_PREFIX)) {
                String[] cookieNames = TextUtils.split(entry.getValue().toString(), ",");
                for (String name : cookieNames) {
                    String encodedCookie = PreferencesUtils.getString(mContext, COOKIE_NAME_PREFIX + name, null);
                    if (encodedCookie != null) {
                        HttpCookie decodeCookie = decodeCookie(encodedCookie);
                        if (decodeCookie != null) {
                            if (!cookies.containsKey(entry.getKey()))
                                cookies.put(entry.getKey(), new ConcurrentHashMap<String, HttpCookie>());
                            cookies.get(entry.getKey()).put(name, decodeCookie);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void add(URI uri, HttpCookie httpCookie) {
        String name = getCookieToken(uri, httpCookie);

        // Save cookie into local store, or remove if expired
        if (!httpCookie.hasExpired()) {
            if (!cookies.containsKey(uri.getHost()))
                cookies.put(uri.getHost(), new ConcurrentHashMap<String, HttpCookie>());
            cookies.get(uri.getHost()).put(name, httpCookie);
        } else {
            if (cookies.containsKey(uri.toString()))
                cookies.get(uri.getHost()).remove(name);
        }

        // Save cookie into persistent store
        PreferencesUtils.putString(mContext, uri.getHost(), TextUtils.join(",", cookies.get(uri.getHost()).keySet()));
        PreferencesUtils.putString(mContext, COOKIE_NAME_PREFIX + name, encodeCookie(new SerializableHttpCookie(httpCookie)));
    }

    @Override
    public List<HttpCookie> get(URI uri) {
        ArrayList<HttpCookie> ret = new ArrayList<>();
        if (cookies.containsKey(uri.getHost()))
            ret.addAll(cookies.get(uri.getHost()).values());
        return ret;
    }

    @Override
    public List<HttpCookie> getCookies() {
        ArrayList<HttpCookie> ret = new ArrayList<>();
        for (String key : cookies.keySet()) {
            ret.addAll(cookies.get(key).values());
        }
        return ret;
    }

    @Override
    public List<URI> getURIs() {
        ArrayList<URI> ret = new ArrayList<>();
        for (String key : cookies.keySet()) {
            try {
                ret.add(new URI(key));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    @Override
    public boolean remove(URI uri, HttpCookie httpCookie) {
        String name = getCookieToken(uri, httpCookie);

        if (cookies.containsKey(uri.getHost()) && cookies.get(uri.getHost()).containsKey(name)) {
            cookies.get(uri.getHost()).remove(name);

            if (PreferencesUtils.contains(mContext, COOKIE_NAME_PREFIX + name)) {
                PreferencesUtils.remove(mContext, COOKIE_NAME_PREFIX + name);
            }
            PreferencesUtils.putString(mContext, uri.getHost(), TextUtils.join(",", cookies.get(uri.getHost()).keySet()));

            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean removeAll() {
        PreferencesUtils.clear(mContext);
        cookies.clear();
        return true;
    }

    protected String getCookieToken(URI uri, HttpCookie httpCookie) {
        return httpCookie.getName() + httpCookie.getDomain();
    }


    /**
     * Cookie object into String
     *
     * @param cookie cookie to be encoded, can be null
     * @return cookie encoded as String
     */
    protected String encodeCookie(SerializableHttpCookie cookie) {
        if (cookie == null)
            return null;

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(os);
            objectOutputStream.writeObject(cookie);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return byteArrayToHexString(os.toByteArray());
    }

    /**
     * Returns cookie decoded from cookie string
     *
     * @param cookieString string of cookie as returned from http request
     * @return decoded cookie or null if exception occured
     */
    protected HttpCookie decodeCookie(String cookieString) {
        byte[] bytes = hexStringToByteArray(cookieString);
        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        HttpCookie cookie = null;

        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(is);
            cookie = ((SerializableHttpCookie) objectInputStream.readObject()).getHttpCookie();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return cookie;
    }

    /**
     * Using some super basic byte array &lt;-&gt; hex conversions so we don't have to rely on any
     * large Base64 libraries. Can be overridden if you like!
     *
     * @param bytes byte array to be converted
     * @return string containing hex values
     */
    protected String byteArrayToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte element : bytes) {
            int v = element & 0xff;
            if (v < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString().toUpperCase(Locale.US);
    }

    /**
     * Converts hex values from strings to byte arra
     *
     * @param hexString string of hex-encoded values
     * @return decoded byte array
     */
    protected byte[] hexStringToByteArray(String hexString) {
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }
        return data;
    }
}
