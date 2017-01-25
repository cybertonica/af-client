AFClient
===================================

To connect the test environment replace "localhost", "user" and "secret" with the actual data (provided on a request).

```java
AfClient.get("http", "host", 7499).createEvent(AfOptions.create("user", "secret"))
```

Then set the request parameters (see omnireact-integration.pdf for the details):
```java
                        .add("channel", "money_transfer")
                        .add("sub_channel", "test_subchannel2")
                        .add("src_id", AfClient.SHA_256("4000123412341233")))
                        .add("src_parent", "VISA")
                        .add("src_partner", "src_partner")
                        .add("dst_id", 9168212901L)
                        .add("dst_parent", "MTS")
                        .add("dst_partner", "dst_partner")
                        .add("amount", 10201)
                        .add("exp", 2)
                        .add("currency", 643));
```


Possible JSON responses:
```json
{"code":203,"comment":"Bad signature"}
```
```json
"{\"tx_id\":\"aq_tx:560c178ce4b099255ca27a7c\",\"action\":\"ALLOW\",\"risk_score\":0,\"rule_score\":0,\"reason\":\"ALLOW\\tdefault strategy\\t \",\"hierarchy\":{\"ML\":{}}}"
```


A full example:

```java
import com.cybertonica.client.AfClient;
import com.cybertonica.client.AfOptions;
import com.eclipsesource.json.JsonObject;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class Example {

    public static void main(String[] args)
            throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        JsonObject responseGlobal = AfClient.get("http", "host", 7499).createEvent(
                AfOptions.create("user", "secret")
                        .add("channel", "p2p_money_transfer")
                        .add("sub_channel", "sub_channel")
                        .add("src_id", AfClient.SHA_256("4000123412341233"))
                        .add("src_parent", "VISA")
                        .add("dst_id", 9168212901L)
                        .add("dst_parent", "MTS")
                        .add("amount", 10201)
                        .add("exp", 2)
                        .add("exp_date", "1/25")
                        .add("operation_type", "input")
                        .add("currency", 643));
        System.out.println(responseGlobal);

        JsonObject responseAcquiring = AfClient.get("http://localhost:7499/api/v2/createEvent")
                .createEvent(AfOptions.create("user", "secret")
                        .add("channel", "acquiring")
                        .add("sub_channel", "sub_channel")
                        .add("src_id", AfClient.SHA_256("400012341234232"))
                        .add("src_parent", "VISA")
                        .add("dst_id", 9168212906L)
                        .add("dst_parent", "MTS")
                        .add("amount", 10200)
                        .add("exp", 2)
                        .add("src_bin", 400012)
                        .add("ip", "192.168.102.56")
                        .add("cookie", "some_cookie")
                        .add("mcc", "123")
                        .add("exp_date", "12/18")
                        .add("currency", 643));
        System.out.println(responseAcquiring);

        JsonObject updateCharge = AfClient.get("http", "host", 7499).updateEvent(
                AfOptions.create("user", "secret")
                        .add("tx_id", responseGlobal.get("tx_id").asString())
                        .add("status", "OK")
                        .add("is_authed", 1)
                        .add("code", "123")
                        .add("comment", "some_comment"));
        System.out.println(updateCharge);

    }
}
```

Current version:
```xml
    <groupId>com.cybertonica.client</groupId>
    <artifactId>AFClient</artifactId>
    <version>0.2</version>
```

Depends on:
```xml
    <groupId>com.eclipsesource.minimal-json</groupId>
    <artifactId>minimal-json</artifactId>
    <version>0.9.4</version>
```
