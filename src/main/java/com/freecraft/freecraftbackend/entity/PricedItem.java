package com.freecraft.freecraftbackend.entity;

import lombok.Data;

@Data
public class PricedItem extends Item {

    double amount;

    public static PricedItem convert(Item it, double price){
        PricedItem ret = new PricedItem();
        ret.setCurrency(it.getCurrency());
        ret.setDescription(it.getDescription());
        ret.setName(it.getName());
        ret.setProduct(it.getProduct());
        ret.setQuantity(it.getQuantity());
        ret.setAmount(price*100);
        return ret;
    }
}


