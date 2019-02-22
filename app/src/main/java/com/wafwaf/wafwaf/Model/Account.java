package com.wafwaf.wafwaf.Model;

public class Account {
   private String name;
   private String apiKey;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public Account(){ }

    public Account(String name, String apiKey){
        this.apiKey =apiKey;
        this.name = name;
    }
}
