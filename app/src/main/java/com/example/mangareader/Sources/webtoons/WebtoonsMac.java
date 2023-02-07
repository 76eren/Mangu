package com.example.mangareader.Sources.webtoons;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class WebtoonsMac {

    private static String ss(String str, String str2) {
        String substring = str.substring(0, Math.min(255, str.length()));
        return substring + str2;
    }

    private static String doFinal(Mac mac, String str) {
        byte[] doFinal = mac.doFinal(str.getBytes());
        return Base64.getEncoder().encodeToString(doFinal);
    }

    public static String GetChapterList(String url) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {

        String key = "gUtPzJFZch4ZyAGviiyH94P99lQ3pFdRTwpJWDlSGFfwgpr6ses5ALOxWHOIT7R1";
        SecretKeySpec spec = new SecretKeySpec(key.getBytes(), "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(spec);

        long time = System.currentTimeMillis();

        String encode = URLEncoder.encode(doFinal(mac, ss(url, String.valueOf(time))), "utf-8");
        StringBuilder sb2 = new StringBuilder();
        sb2.append(url);
        if (url.contains("?")) {
            sb2.append("&");
        } else {
            sb2.append("?");
        }
        sb2.append("msgpad=").append(time);
        sb2.append("&md=").append(encode);

        return sb2.toString();

    }

}
