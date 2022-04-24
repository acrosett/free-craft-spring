package com.freecraft.freecraftbackend.entity;

import lombok.Data;

@Data
public class Item {

    String currency = "EUR";

    String description;

    String name;

    String product;

    int quantity = 1;
}
