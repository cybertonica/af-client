package com.cybertonica.client;

import com.eclipsesource.json.JsonObject;

/**
 * Created by oem.
 */
@SuppressWarnings("unused")
public class AfOptions {

    private JsonObject params = new JsonObject();
    String user = "";
    String signature = "";

    private AfOptions() {
    }

    private AfOptions(String user, String signature) {
        this.user = user;
        this.signature = signature;
    }

    public static AfOptions create(String user, String signature) {
        return new AfOptions(user, signature);
    }

    public AfOptions set(JsonObject params) {
        this.params = params;
        return this;
    }

    public AfOptions add(String key, String value) {
        params.add(key, value);
        return this;
    }

    public AfOptions add(String key, long value) {
        params.add(key, value);
        return this;
    }

    @Override
    public String toString() {
        return params.toString();
    }
}
