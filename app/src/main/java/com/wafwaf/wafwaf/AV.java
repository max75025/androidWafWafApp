package com.wafwaf.wafwaf;

public class AV {

    String eventTime;
    String eventType;
    String fileName;
    String description;
    /*String type2;*/
    int imgId;
    String account;

    void setImgId(String eventType){
        switch (eventType){
            case "Новый"   : this.imgId = R.drawable.add_file; break;
            case "Изменен" : this.imgId = R.drawable.edit_file;break;
            case "Удален"  : this.imgId = R.drawable.delete_file;break;
        }

    }

    AV(){}

    AV(String eventTime, String eventType, String fileName, String SuspiciousType, String SuspiciousDescription, String account) {
        this.eventTime = eventTime;
        this.eventType = eventType;
        this.fileName = fileName;
        this.description = SuspiciousType + SuspiciousDescription;
        switch (eventType){
            case "Новый"   : this.imgId = R.drawable.add_file;
            case "Изменен" : this.imgId = R.drawable.edit_file;
            case "Удален"  : this.imgId = R.drawable.delete_file;
        }
        this.account = account;

    }

    public String getEventTime(){
        return eventTime;
    }
    public String getEventType(){
        return eventType;
    }public String getFileName(){
        return fileName;
    }public String getDescription(){
        return description;
    }public int getImgId(){
        return imgId;
    }public String getAccount(){
        return account;
    }


    public void setEventTime(String eventTime){
       this.eventTime = eventTime;
    }
    public void setEventType(String eventType){
        this.eventType = eventType;
    }
    public void setFileName(String fileName){
        this.fileName = fileName;
    }
    public void setDescription(String description){
        this.description = description;
    }
    public void setImgId(int imgId){
       this.imgId = imgId;
    }
    public void setAccount(String account){
        this.account = account;
    }
}
