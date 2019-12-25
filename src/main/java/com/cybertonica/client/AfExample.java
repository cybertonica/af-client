package com.cybertonica.client;

import com.cybertonica.client.AfClient;
import com.cybertonica.client.AfOptions;
import com.eclipsesource.json.JsonObject;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * This is a simple example how to send createEvent API request, get scoring recommendation and
 * then send updateEvent API request.
 * <p>
 * Warning! that this reference implementation lacks some details important for production, like
 * good error handling, connection pooling or timeouts.
 * <p>
 * See Cybertonica integration manual for the details.
 *
 * Dependencies:
 *     <groupId>com.eclipsesource.minimal-json</groupId>
 *     <artifactId>minimal-json</artifactId>
 *     <version>0.9.4</version>
 */
public class AfExample {

    public static void main(String[] args)
            throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        // Network parameters.
        int port = 7499;
        String protocol = "https";

        // hostname will be different for production environment!
        String hostname = "test.cybertonica.com";

        // you can get these values in Settings tab in Cybertonica dashboard.
        String apiUser = "YOUR_API_USER";
        String apiKey = "YOUR_API_KEY";

        // Test data
        String channel = "payment";
        String subChannel = "example_subchannel";
        String cardnumber = "400012341234232";
        String issuerBIN = cardnumber.substring(0, 6);

        // Step 1. Score a payment.
        System.out.println("Sending createEvent request...");
        JsonObject responsePayment = AfClient.get(protocol, hostname, port)
                .createEvent(AfOptions.create(apiUser, apiKey, channel, subChannel, "NEW")
                        // see Integration manual for the details
                        .add("t", System.currentTimeMillis())
                        .add("ip", "192.168.102.56")
                        .add("tid", "0220a70ea167176ff9080bb76e523357a")  // session ID from  JavaScript module
                        .add("src_id", AfClient.SHA_256(cardnumber))
                        .add("src_parent", issuerBIN)
                        .add("dst_id", "9168212906")   // merchant ID
                        .add("dst_client_id", "Some Merchant inc.") // optional merchant name
                        .add("dst_parent", "aMerchantBank")
                        .add("mcc", "123")
                        .add("currency", 826) // GBP
                        .add("exp", 2)
                        .add("amount", 15000)
                        .add("exp_date", "12.18"));

        // Note: if you get HTTP error 401 - check apiUser and apiKey.
        System.out.println("Scoring recommendation:");
        System.out.println(responsePayment);
        // ALLOW - pass without 3DS
        // CHALLENGE - use 3DS
        // DENY - stop the payment
        System.out.println("Recommended action: " + responsePayment.get("action"));

        // Process the payment according to the recommendation...

        // Notify Cybertonica about payment's status.
        // Note, in API v2.2 you can use your extid provided in createEvent call OR 'id' from createEvent response.
        System.out.println("Sending updateEvent request...");
        JsonObject updateResponse = AfClient.get(protocol, hostname, port)
                .updateEvent(AfOptions.update(apiUser, apiKey, channel,
                        responsePayment.getString("id", null), AfOptions.FRAUD)
                        .add("t", System.currentTimeMillis())
                        .add("code", "1")   // transaction status code
                        .add("comment", "some comment")  // transaction status, etc. "3DS timeout"
                        .add("is_authed", 1));    // optional flag to show if 3DS used.
        System.out.println("updateEvent response:");
        System.out.println(updateResponse);
    }
}
