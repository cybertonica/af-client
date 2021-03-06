package com.cybertonica.client;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Scanner;


@SuppressWarnings("unused")
public class AfClient {

    private HttpURLConnection connection;
    private String url;

    private AfClient() {
    }

    private AfClient(String url) {
        this.url = url;
    }

    private AfClient(String protocol, String host, int port) {
        this.url = String.format("%s://%s:%s/api/v2.2/events/", protocol, host, port);
    }

    public static AfClient get(String url) {
        return new AfClient(url);
    }

    public static AfClient get(String host, int port) {
        return new AfClient("https", host, port);
    }

    public static AfClient get(String protocol, String host, int port) {
        return new AfClient(protocol, host, port);
    }

    public JsonObject createEvent(AfOptions params) throws IOException, InvalidKeyException, NoSuchAlgorithmException {
        URL url = new URL(this.url + params.getQuery());
        openConnection(url, params);
        System.out.println(url.toString());
        return postCreate(params.toString());
    }

    public JsonObject updateEvent(AfOptions params) throws IOException, InvalidKeyException, NoSuchAlgorithmException {
        URL url = new URL(this.url + params.getQuery());
        System.out.println(url.toString());
        openConnection(url, params);
        return postUpdate(params.toString());
    }

    public String sign(AfOptions options) throws NoSuchAlgorithmException, InvalidKeyException {
        return sign(options.toString(), options.apiKey);
    }

    private String sign(String json, String secret) throws NoSuchAlgorithmException, InvalidKeyException {
        javax.crypto.spec.SecretKeySpec ks = new javax.crypto.spec.SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA1");
        javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA1");
        mac.init(ks);
        byte[] result = mac.doFinal(json.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(result);
    }

    private JsonObject postCreate(String json) throws IOException {
        byte[] postData = json.getBytes(StandardCharsets.UTF_8);
        connection.setRequestProperty("Content-Length", Integer.toString(postData.length));
        DataOutputStream wr = null;
        try {
            wr = new DataOutputStream(connection.getOutputStream());
            wr.write(postData);
        } catch (Exception ex) {
            if (wr != null) {
                wr.close();
            }
        }
        Reader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
        Scanner s = new Scanner(in).useDelimiter("\\A");
        String jsonStr = s.hasNext() ? s.next() : "";
        return Json.parse(jsonStr).asObject();
    }

    private JsonObject postUpdate(String json) throws IOException {
        byte[] postData = json.getBytes(StandardCharsets.UTF_8);
        connection.setRequestProperty("Content-Length", Integer.toString(postData.length));
        DataOutputStream wr = null;
        try {
            wr = new DataOutputStream(connection.getOutputStream());
            wr.write(postData);
        } catch (Exception ex) {
            if (wr != null) {
                wr.close();
            }
        }
        Reader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
        Scanner s = new Scanner(in).useDelimiter("\\A");
        String jsonStr = s.hasNext() ? s.next() : "";
        return new JsonObject().add("", jsonStr);
    }

    private void openConnection(URL url, AfOptions options) throws IOException, InvalidKeyException, NoSuchAlgorithmException {
        connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setInstanceFollowRedirects(false);
        connection.setRequestMethod(options.type);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("charset", StandardCharsets.UTF_8.name());
        connection.setRequestProperty("X-AF-Team", options.apiUser);
        connection.setRequestProperty("X-AF-Signature", sign(options));
        connection.setUseCaches(false);
    }

    public static String SHA_256(String src) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(src.getBytes(StandardCharsets.UTF_8));
        byte[] digest = md.digest();

        StringBuilder hexString = new StringBuilder();
        for (byte aDigest : digest) {
            hexString.append(Integer.toHexString(0xFF & aDigest));
        }
        return hexString.toString();
    }

    public static String SHA_256_salted(String src, String salt) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
        String algorithm = "HmacSHA256";  // OPTIONS= HmacSHA512, HmacSHA256, HmacSHA1, HmacMD5
        Mac sha256_hmac = Mac.getInstance(algorithm);
        SecretKeySpec secret_key = new SecretKeySpec(salt.getBytes("UTF-8"), algorithm);
        sha256_hmac.init(secret_key);
        String hash = Base64.getEncoder().encodeToString(sha256_hmac.doFinal(src.getBytes("UTF-8")));
        System.out.println(hash);
        return hash;
    }
}
