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

    Attack(String country, String ip, String startAttackTime, String endAttackTime, String types, String account) {
        this.country = country;
        this.ip = ip;
        this.startAttackTime = startAttackTime;
        this.endAttackTime = endAttackTime;
        this.types = types;
        /*this.type2 = type2;*/
        setImgId(types);
        this.account = account;

    }

   /* private List<Card> cards;

    private void initializeData() {
        cards = new ArrayList<>();
        cards.add(new Card("header1", "content1"));
        cards.add(new Card("header2", "content2"));
        cards.add(new Card("header3", "content3"));

    }*/
}
