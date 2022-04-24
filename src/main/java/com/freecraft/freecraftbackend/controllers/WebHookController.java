package com.freecraft.freecraftbackend.controllers;

import com.freecraft.freecraftbackend.manager.ShopTransactionManager;
import com.freecraft.freecraftbackend.service.LogService;
import com.freecraft.freecraftbackend.service.RestService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class WebHookController {

    Logger logger = LoggerFactory.getLogger(FaqController.class);

    @Autowired
    ShopTransactionManager shopTransactionManager;

    @Autowired
    private LogService logService;

    @Autowired
    RestService restService;

    @PostMapping("/api/hooke85a356555ae6eca9e6b")
    public ResponseEntity notify(@RequestBody() String body) {
        logger.info("webhook " + body);
        logService.logInfo("webhook " + body);

        JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
        if(shopTransactionManager.checkSession(jsonObject.get("data").getAsJsonObject().get("object").getAsJsonObject().get("transaction_details").getAsJsonObject().get("session").getAsInt())){
            shopTransactionManager.performTransactions();
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/api/hooke85a356555ae6eca9e6b")
    public ResponseEntity test() {
        logger.info("webhook TESTED");
        logService.logInfo("webhook TESTED");
        return new ResponseEntity<>(HttpStatus.OK);
    }

//    @GetMapping("/api/session")
//    public ResponseEntity<String> testSession() {
//
//        shopTransactionManager.performTransactions();
//        return new ResponseEntity<>(HttpStatus.OK);
//
//    }



}