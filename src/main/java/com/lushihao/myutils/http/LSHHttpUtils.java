package com.lushihao.myutils.http;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public final class LSHHttpUtils {

    /**
     * 支持的Http method
     */
    public enum HttpMethod {
        POST, DELETE, GET, PUT, HEAD
    }

    /**
     * 请求
     *
     * @param strURL
     * @param connectTimeout
     * @param readTimeout
     * @param method
     * @return
     */
    public static String request(String strURL, int connectTimeout, int readTimeout, HttpMethod method) {
        URL url = null;
        HttpURLConnection httpConn = null;
        StringBuffer buffer = null;
        try {
            url = new URL(strURL);
            httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setRequestMethod(method.toString());
            httpConn.setConnectTimeout(connectTimeout);
            httpConn.setReadTimeout(readTimeout);
            httpConn.connect();

            BufferedReader reader = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
            String line;
            buffer = new StringBuffer();
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            reader.close();
            httpConn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return buffer.toString();
    }
}