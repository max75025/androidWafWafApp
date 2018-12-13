package com.wafwaf.wafwaf;

public class Account {
   public String name;
   public String apiKey;


    Account(){ }

    Account(String name, String apiKey){
        this.apiKey =apiKey;
        this.name = name;
    }
}
