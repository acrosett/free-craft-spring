package com.freecraft.freecraftbackend.controllers;


import com.freecraft.freecraftbackend.repo.FaqItemRepository;
import com.freecraft.freecraftbackend.service.minecraftquery.MCQuery;
import com.freecraft.freecraftbackend.service.minecraftquery.QueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;


@Controller
public class FaqController {

    @Autowired
    private FaqItemRepository faqItemRepository;

    Logger logger = LoggerFactory.getLogger(FaqController.class);

    @PutMapping("/api/faq")
    public ResponseEntity addWeight(@RequestBody() String name) {
        faqItemRepository.incrementByName(name);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/api/faq")
    @ResponseBody
    public HashMap<String, Integer> getWeights() {
        HashMap<String,Integer> returnMap = new HashMap();
        faqItemRepository.findAll().forEach(i -> {
            returnMap.put(i.getName(), i.getWeight());
        });
        return returnMap;
    }

    @GetMapping("/api/status")
    @ResponseBody
    public HashMap<String, HashMap<String, Integer>> getStatus() {
        HashMap<String, HashMap<String, Integer>> returnMap = new HashMap();
        HashMap<String,Integer> returnMap2 = new HashMap();

        MCQuery mcQuery = new MCQuery("free-craft.fr", 25565);

        if(mcQuery != null) {
            QueryResponse response = mcQuery.basicStat();

            if(response != null) {
                returnMap2.put("online", response.getOnlinePlayers());
                returnMap2.put("max", response.getMaxPlayers());
                returnMap.put("players", returnMap2);
            }
        }

        return returnMap;
    }

}