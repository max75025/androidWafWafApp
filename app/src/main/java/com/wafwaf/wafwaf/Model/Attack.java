package com.wafwaf.wafwaf.Model;

import android.media.Image;

import com.wafwaf.wafwaf.R;
import com.wafwaf.wafwaf.util.UnixTime;

public class Attack {
    private String country;
    private String ip;
    private long startAttackTime;
    private long endAttackTime;
    private String types;
    /*String type2;*/
    private int imgId;
    private String account;
    private String apiKey;



    public Attack(){
    }


    public void setImgId(String types){
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

    public Attack(String country, String ip, int startAttackTime, int endAttackTime, String types, String account, String apiKey) {
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
        return UnixTime.toDate(startAttackTime);
    }

    public long getStartAttackTimeUnix(){return startAttackTime;}

    public void setStartAttackTime(long startAttackTime) {
        this.startAttackTime = startAttackTime;
    }


    public String getEndAttackTime() {
        return UnixTime.toDate(endAttackTime);
    }

    public long getEndAttackTimeUnix() {
        return endAttackTime;
    }


    public void setEndAttackTime(long endAttackTime) {
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
