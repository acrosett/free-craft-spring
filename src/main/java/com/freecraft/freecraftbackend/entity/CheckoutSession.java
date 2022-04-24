package com.freecraft.freecraftbackend.entity;

import lombok.Data;

import java.util.Map;

@Data
public class CheckoutSession {

    int id;

    String cancel_url;

    Map<String,Map<String,String>> custom;

    Map<String,String> metadata;

    Item[] items;

    String success_url;

    String locale = "fr";

    String permalink;

    String[] payment_methods = {"card", "paypal"};

    String status;

}
