package com.ying.cloud.lycoin.utils;

import java.io.*;
import java.net.*;
import java.util.Map;

public class HttpUtils {
    private static final String CHARSET = "UTF-8";

    public static String doGet(String strURL) {
        return doGet(strURL, CHARSET);
    }

    public static String doPost(String strURL, Map<String, String> map) {
        return doPost(strURL, map, CHARSET);
    }

    public static String doPost(String strURL, Map<String, String> map, String encoding) {
        try {
            URL url = new URL(strURL);
            return doPost(url, map, encoding);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

    }

    public static String doGet(URL url, String encoding) {
        InputStream in = null;
        InputStreamReader insr = null;
        BufferedReader reader = null;
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection)url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("accept-language", "zh_CN");
            conn.setRequestProperty("Charset", encoding);
            conn.setUseCaches(false);
            conn.setInstanceFollowRedirects(true);
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            conn.connect();

            in = conn.getInputStream();
            insr = new InputStreamReader(in, encoding);
            reader = new BufferedReader(insr);
            StringBuffer buff = new StringBuffer();
            String line = reader.readLine();
            while (line != null) {
                buff.append(line);
                line = reader.readLine();
            }

            conn.disconnect();
            String str1 = buff.toString();
            return str1;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        finally {
            try {
                if (reader != null)
                    reader.close();
            } catch (IOException localIOException4) {
            }
            try {
                if (insr != null)
                    insr.close();
            } catch (IOException localIOException5) {
            }
            try {
                if (in != null)
                    in.close();
            } catch (IOException localIOException6) {
            }
            if (conn != null)
                conn.disconnect();
        }
    }

    public static String buildParams(Map<String, String> map) {
        if ((map == null) || (map.isEmpty()))
            return "";
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (Map.Entry entry : map.entrySet()) {
            if (i > 0)
                sb.append("&");
            sb.append((String)entry.getKey());
            sb.append("=");
            sb.append((String)entry.getValue());
            i++;
        }

        return sb.toString();
    }

    public static String doPost(URL url, Map<String, String> map, String encoding) {
        BufferedReader reader = null;
        DataOutputStream out = null;
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection)url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setInstanceFollowRedirects(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Charset", encoding);
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            conn.connect();

            if ((map != null) && (!map.isEmpty())) {
                out = new DataOutputStream(conn.getOutputStream());
                String params = buildParams(map);
                out.write(params.getBytes());
                out.flush();
                out.close();
                out = null;
            }

            reader = new BufferedReader(
                    new InputStreamReader(conn
                            .getInputStream(), encoding));
            StringBuffer buff = new StringBuffer();
            String line = reader.readLine();
            while (line != null) {
                buff.append("\n" + line);
                line = reader.readLine();
            }

            reader.close();
            reader = null;
            conn.disconnect();
            conn = null;
            String str1 = buff.toString();
            return str1;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            try {
                if (reader != null)
                    reader.close();
            } catch (IOException localIOException3) {
            }
            try {
                if (out != null)
                    out.close();
            } catch (IOException localIOException4) {
            }
            if (conn != null)
                conn.disconnect();
        }
    }
    public static String doPost(String strURL, String params, String encoding) {

        BufferedReader reader = null;
        DataOutputStream out = null;
        HttpURLConnection conn = null;
        try {
            URL url = new URL(strURL);
            conn = (HttpURLConnection)url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setInstanceFollowRedirects(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Charset", encoding);
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            conn.connect();

            if ((params != null) && (!params.isEmpty())) {
                out = new DataOutputStream(conn.getOutputStream());

                out.write(params.getBytes());
                out.flush();
                out.close();
                out = null;
            }

            reader = new BufferedReader(
                    new InputStreamReader(conn
                            .getInputStream(), encoding));
            StringBuffer buff = new StringBuffer();
            String line = reader.readLine();
            while (line != null) {
                buff.append("\n" + line);
                line = reader.readLine();
            }

            reader.close();
            reader = null;
            conn.disconnect();
            conn = null;
            String str1 = buff.toString();
            return str1;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            try {
                if (reader != null)
                    reader.close();
            } catch (IOException localIOException3) {
            }
            try {
                if (out != null)
                    out.close();
            } catch (IOException localIOException4) {
            }
            if (conn != null)
                conn.disconnect();
        }
    }
    public static final String doGet(String strURL, String encoding) {
        try {
            URL url = new URL(strURL);
            return doGet(url, encoding);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static final String encode(String param) {
        return encode(param, "UTF-8");
    }

    public static final String decode(String param) {
        return decode(param, "UTF-8");
    }

    public static final String encode(String param, String encoding) {
        try {
            return URLEncoder.encode(param, encoding);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

    }

    public static final String decode(String param, String encoding) {
        try {
            return URLDecoder.decode(param, encoding);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

    }
}
