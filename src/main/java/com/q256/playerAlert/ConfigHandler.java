package com.q256.playerAlert;

import com.google.gson.Gson;

import java.io.*;
import java.util.HashSet;

public class ConfigHandler {
    private transient File configFile;
    private ConfigValues configValues;

    public ConfigHandler(File configFile) {
        this.configFile = configFile;

        try {
            if (configFile.exists()) {
                FileReader fileReader = new FileReader(configFile);
                this.configValues = new Gson().fromJson(fileReader, ConfigValues.class);
            }
            else {
                configValues = new ConfigValues();
                configFile.createNewFile();
                saveConfig();
            }
        } catch (IOException e){
            e.printStackTrace();
            Utils.sendClientMessage(" Could not load config file",true);
        }
    }

    private synchronized void saveConfig(){
        try {
            FileWriter fileWriter = new FileWriter(configFile);
            new Gson().toJson(configValues, fileWriter);
            fileWriter.close();
        } catch (IOException e){
            e.printStackTrace();
            Utils.sendClientMessage( " Could not write to config file", true);
        }
    }

    public synchronized HashSet<String> getBlockedPlayers(){
        return configValues.blockedPlayers;
    }

    public synchronized boolean isBlocked(String name){
        return configValues.blockedPlayers.contains(name.toLowerCase());
    }

    public synchronized void clearBlockedPlayers(){
        configValues.blockedPlayers.clear();
        saveConfig();
    }

    public synchronized boolean addPlayer(String name){
        boolean success = configValues.blockedPlayers.add(name.toLowerCase());
        saveConfig();
        return success;
    }

    public synchronized boolean removePlayer(String name){
        boolean success = configValues.blockedPlayers.remove(name.toLowerCase());
        saveConfig();
        return success;
    }

    public int getOverlayColor(){return configValues.overlayColor;}

    public int getOverlayXPos(){return configValues.overlayX;}

    public int getOverlayYPos(){return configValues.overlayY;}

    public void setOverlayColor(int color){
        configValues.overlayColor = color;
        saveConfig();
    }

    public void setOverlayXPos(int value){
        configValues.overlayX = value;
        saveConfig();
    }

    public void setOverlayYPos(int value){
        configValues.overlayY = value;
        saveConfig();
    }


    class ConfigValues {
        int overlayX = 5;
        int overlayY = 5;
        int overlayColor = 255<<16;
        HashSet<String> blockedPlayers = new HashSet<>();
    }
}


