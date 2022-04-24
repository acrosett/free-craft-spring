package com.freecraft.freecraftbackend.service;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class LogService {
    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    private String LOG_FILE = "/home/minecraft/site_logs.txt";
    //private String LOG_FILE = "";

    public void log(String msg){
        Date date = new Date();
        try {
            Files.write(Paths.get(LOG_FILE), (formatter.format(date) +" " + msg+"\n").getBytes(), StandardOpenOption.APPEND);
            System.out.println(formatter.format(date) +" " + msg+"\n");
        }catch (IOException e) {

            System.out.println(formatter.format(date) +" " + e.getMessage()+"\n");
       }
    }

    public void logError(String msg){
        log("[Error]: "+msg);
    }
    public void logInfo(String msg){
        log("[Info]: "+msg);
    }
    public void logWarning(String msg){log("[Warning]: "+msg);}
}
