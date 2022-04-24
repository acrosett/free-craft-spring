package com.freecraft.freecraftbackend.service;

import com.freecraft.freecraftbackend.dto.MojangPlayerDto;
import com.freecraft.freecraftbackend.entity.CheckoutSession;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Subscription;
import com.stripe.model.SubscriptionCollection;
import com.stripe.param.SubscriptionUpdateParams;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class RestService {

    private final RestTemplate restTemplate;

    public RestService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public MojangPlayerDto getUUID(String pseudo) {
        if(pseudo.length() > 16){
            return null;
        }
        String url = "https://api.mojang.com/users/profiles/minecraft/" + pseudo;
        MojangPlayerDto dto = this.restTemplate.getForObject(url, MojangPlayerDto.class);
        String UUID = null;
        if(dto != null) {
            UUID = dto.getId();
        }
        if(UUID != null) {
            dto.setId(UUID.substring(0, 8) + '-' + UUID.substring(8, 12) + '-' + UUID.substring(12, 16) + '-' + UUID.substring(16, 20) + '-' + UUID.substring(20, 32));
        }
            return dto;
    }

    String QUADERNO_API_URL = "https://xxxx.quadernoapp.com/api";
    String QUADERNO_API_PASSWORD = "xxxxxxx";
    String STRIPE_API_KEY = "xxxxxxxx";


    public ResponseEntity<CheckoutSession> createSession(CheckoutSession session) {

        HttpHeaders headers = createHeaders(QUADERNO_API_PASSWORD,"x");
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));


        HttpEntity<CheckoutSession> entity = new HttpEntity<CheckoutSession>(session, headers);

        String url = QUADERNO_API_URL + "/checkout/sessions.json";

        ResponseEntity<CheckoutSession> ret = restTemplate.exchange(url, HttpMethod.POST, entity, CheckoutSession.class);

        return ret;
    }

    public ResponseEntity<CheckoutSession[]> getCompletedSessions(){
        HttpHeaders headers = createHeaders(QUADERNO_API_PASSWORD,"x");
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<CheckoutSession> entity = new HttpEntity<CheckoutSession>(headers);

        String url = QUADERNO_API_URL + "/checkout/sessions.json?status=completed";

        ResponseEntity<CheckoutSession[]> ret = restTemplate.exchange(url, HttpMethod.GET,entity, CheckoutSession[].class );

        return ret;
    }

    public ResponseEntity<CheckoutSession> getSession(String id){
        HttpHeaders headers = createHeaders(QUADERNO_API_PASSWORD,"x");
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        String url = QUADERNO_API_URL + "/checkout/sessions/"+ id +".json";

        HttpEntity<CheckoutSession> entity = new HttpEntity<CheckoutSession>(headers);

        ResponseEntity<CheckoutSession> ret = restTemplate.exchange(url, HttpMethod.GET,entity, CheckoutSession.class);

        return ret;
    }


    public SubscriptionCollection getSubscriptions(){
        Stripe.apiKey = STRIPE_API_KEY;

        Map<String, Object> params = new HashMap<>();
        params.put("status", "active");
        SubscriptionCollection subscriptions = null;
        try {
            subscriptions = Subscription.list(params);
        } catch (StripeException e) {
            e.printStackTrace();
        }

        return subscriptions;
    }

    public String cancelSubscription(Subscription s){
        Stripe.apiKey = STRIPE_API_KEY;

        try {

            SubscriptionUpdateParams params =
                    SubscriptionUpdateParams.builder()
                            .setCancelAtPeriodEnd(true)
                            .build();

            Subscription a = s.update(params);
            if(a.getCancelAtPeriodEnd()) {
                return null;
            }
        } catch (StripeException e) {
            e.printStackTrace();
            return e.getMessage();
        }

        return "error";
    }



    HttpHeaders createHeaders(String username, String password){
        return new HttpHeaders() {{
            String auth = username + ":" + password;
            byte[] encodedAuth = Base64.encodeBase64(
                    auth.getBytes(Charset.forName("US-ASCII")) );
            String authHeader = "Basic " + new String( encodedAuth );
            set( "Authorization", authHeader );
        }};
    }

}