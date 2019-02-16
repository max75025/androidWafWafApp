package com.wafwaf.wafwaf;

import android.media.Image;

import java.util.ArrayList;
import java.util.List;

public class Attack {
    String country;
    String ip;
    String startAttackTime;
    String endAttackTime;
    String types;
    /*String type2;*/
    int imgId;
    String account;
    String apiKey;
    Attack(){
    }


    void setImgId(String types){
        if (types.indexOf(';')!=types.lastIndexOf(';')){
            this.imgId = R.drawable.mixed_attack;
            return;
        }
        if (types.contains("clientside")){
            this.imgId = R.drawable.clientside;
            return;
        }
        if (types.contains("database")){
            this.imgId = R.drawable.database;
            return;
        }
        if (types.contains("serverside")){
            this.imgId = R.drawable.serverside;
            return;
        }
        if (types.contains("basicrisk")){
            this.imgId = R.drawable.basicrisk;
            return;
        }
        if (types.contains("criticalrisk")){
            this.imgId = R.drawable.criticalrisk;
            return;
        }
        if (types.contains("fatalrisk")){
            this.imgId = R.drawable.fatalrisk;
            return;
        }

    }

    Attack(String country, String ip, String startAttackTime, String endAttackTime, String types, String account, String apiKey) {
        this.country = country;
        this.ip = ip;
        this.startAttackTime = startAttackTime;
        this.endAttackTime = endAttackTime;
        this.types = types;
        /*this.type2 = type2;*/
        setImgId(types);
        this.account = account;
        this.apiKey = apiKey;

    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getStartAttackTime() {
        return startAttackTime;
    }

    public void setStartAttackTime(String startAttackTime) {
        this.startAttackTime = startAttackTime;
    }

    public String getEndAttackTime() {
        return endAttackTime;
    }

    public void setEndAttackTime(String endAttackTime) {
        this.endAttackTime = endAttackTime;
    }

    public String getTypes() {
        return types;
    }

    public void setTypes(String types) {
        this.types = types;
    }

    public int getImgId() {
        return imgId;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

}
