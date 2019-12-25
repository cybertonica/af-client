AFClient
===================================

To connect the test environment replace "hostName", "team" and "secret" with the actual data (provided on a request).

```java
AfClient.get("https", "hostName", 7499).createEvent(AfOptions.create("team", "secret", "payment"))
```

Then set the request parameters (see omnireact-integration.pdf for the details):
```java
                        .add("t", System.currentTimeMillis())
                        .add("src_id", AfClient.SHA_256("4000123412341233")))
                        .add("src_parent", "400012")
                        .add("dst_id", 9168212901L)
                        .add("dst_parent", "MTS")
                        .add("amount", 10201)
                        .add("exp", 2)
                        .add("currency", 643));
```


Possible responses:
JSON
```
"{
    "id":"eve_payment:72431e42-ec2d-4ea3-9d50-0445710ee759",
    "channel":"payment",
    "rules":["Default"],
    "action":"CHALLENGE",
    "score":0,
    "tags":[],
    "queues":[],
    "comments":[]
}"
```
Bad signature
```HTTP/1.1 401 Unauthorized```

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
    
        JsonObject responsePayment1 = AfClient.get("https", "hostName", 7499)
                .createEvent(AfOptions.create("team", "secret", "payment")
                        .add("t", System.currentTimeMillis())                    
                        .add("ip", "192.168.102.56")
                        .add("tid", "0220a70ea167176ff9080bb76e523357a")
                        .add("src_id", AfClient.SHA_256("400012341234232"))
                        .add("src_parent", "400012")
                        .add("dst_id", 9168212906L)
                        .add("dst_parent", "MTS")
                        .add("mcc", "123")
                        .add("amount", 10200)
                        .add("exp", 2)                    
                        .add("exp_date", "12.18")
                        .add("currency", 643));
        System.out.println(responsePayment1);        

        JsonObject responsePayment = AfClient.get("https", "hostName", 7499)
                .createEvent(AfOptions.create("team", "secret", "payment", AfOptions.OK)
                        .add("src_id", AfClient.SHA_256("400012341234232"))
                        .add("src_parent", "400012")
                        .add("dst_id", 9168212906L)
                        .add("dst_parent", "MTS")
                        .add("amount", 10200)
                        .add("exp", 2)                    
                        .add("exp_date", "12.18")
                        .add("currency", 643));
        System.out.println(responsePayment);

        JsonObject updateResponse = AfClient.get("https", "hostName", 7499)
                .updateEvent(AfOptions.update(team, sign, "payment",
                        responsePayment.getString("id", null), AfOptions.FRAUD)
                        .add("code", "1")
                        .add("comment", "some comment")
                        .add("t", System.currentTimeMillis())
                        .add("is_authed", 1));
        System.out.println(updateResponse);

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
