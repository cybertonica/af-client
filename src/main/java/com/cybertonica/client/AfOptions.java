package com.cybertonica.client;

import com.eclipsesource.json.JsonObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@SuppressWarnings("unused")
public class AfOptions {

    public final static String CREATE = "POST";
    public final static String UPDATE = "PUT";

    public final static String OK = "OK";
    public final static String FRAUD = "FRAUD";
    public final static String FAILED = "FAILED";

    final String apiUser;
    final String apiKey;
    final String channel;
    final String type;
    final Optional<String> updateId;
    private JsonObject params = new JsonObject();
    private final Map<String, String> query = new HashMap<>(2);

    private AfOptions(String apiUser, String apiKey, String channel, String type,
                      Optional<String> subChannel, Optional<String> status, Optional<String> updateId) {
        this.apiUser = apiUser;
        this.apiKey = apiKey;
        this.channel = channel;
        this.type = type;
        this.updateId = updateId;
        subChannel.ifPresent(s -> query.put("subChannel", s));
        status.ifPresent(s -> query.put("status", s));
    }

    public static AfOptions create(String apiUser, String apiKey, String channel) {
        return new AfOptions(apiUser, apiKey, channel, CREATE, Optional.empty(), Optional.empty(), Optional.empty());
    }

    public static AfOptions create(String apiUser, String apiKey, String channel, String subChannel, String status) {
        return new AfOptions(apiUser, apiKey, channel, CREATE, Optional.of(subChannel), Optional.of(status), Optional.empty());
    }

    public static AfOptions create(String apiUser, String apiKey, String channel, String status) {
        return new AfOptions(apiUser, apiKey, channel, CREATE, Optional.empty(), Optional.of(status), Optional.empty());
    }

    public static AfOptions update(String apiUser, String apiKey, String channel, String updateId, String status) {
        return new AfOptions(apiUser, apiKey, channel, UPDATE, Optional.empty(), Optional.of(status), Optional.of(updateId));
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

    public String getQuery() {
        StringBuilder sb = new StringBuilder(channel + "/" + (UPDATE.equals(type) && updateId.isPresent() ? updateId.get() : ""));
        if (!query.isEmpty()) {
            sb.append("?");
            for (Map.Entry<String, String> entry : query.entrySet()) {
                sb.append(String.format("%s=%s&", entry.getKey(), entry.getValue()));
            }
            return sb.substring(0, sb.length() - 1);
        } else return sb.toString();
    }
}
