package com.freecraft.freecraftbackend.controllers;

import com.freecraft.freecraftbackend.dto.ResponseStatusDto;
import com.freecraft.freecraftbackend.manager.PseudoManager;
import com.freecraft.freecraftbackend.manager.ShopTransactionManager;
import com.google.common.util.concurrent.RateLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@Controller
public class PseudoController {

    @Autowired
    private PseudoManager pseudoManager;

    @Autowired
    private ShopTransactionManager transactionManager;

    final RateLimiter statusLimiter = RateLimiter.create(1.0);
    final RateLimiter linkLimiter = RateLimiter.create(1.0);
    final RateLimiter cancelLimiter = RateLimiter.create(1.0);

    @GetMapping("/api/shop")
    public ResponseEntity<ResponseStatusDto> getStatus(@RequestParam() String pseudo) {

        if(statusLimiter.tryAcquire(1, 10, TimeUnit.MILLISECONDS)){
            ResponseStatusDto ret = pseudoManager.getStatus(pseudo);

            if(ret == null){
                return new ResponseEntity<ResponseStatusDto>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<ResponseStatusDto>(ret, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(HttpStatus.TOO_MANY_REQUESTS);
        }


    }

    @GetMapping("/api/link")
    @ResponseBody
    public String getLink(@RequestParam() String uuid, @RequestParam() String product) {

        if(linkLimiter.tryAcquire(1, 10, TimeUnit.MILLISECONDS)){
            String ret = transactionManager.getLinkForProduct(uuid, product);
            return ret;
        }else{
            return null;
        }

    }

    @GetMapping("/api/memberprice")
    @ResponseBody
    public String getMemberPrice() {
        Double price = transactionManager.getMembershipPrice();

        if(price == -1){
            return null;
        }
        return price.toString()+"€ par mois";
    }

    @GetMapping("/api/cancel")
    @ResponseBody
    public String cancelMembership(@RequestParam() String uuid) {
        if(cancelLimiter.tryAcquire(1, 10, TimeUnit.MILLISECONDS)) {
            String ret = transactionManager.cancelMembership(uuid);
            return ret;
        }else{
            return null;
        }
    }


}