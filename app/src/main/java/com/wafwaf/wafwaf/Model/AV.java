package com.wafwaf.wafwaf.Model;

import com.wafwaf.wafwaf.R;
import com.wafwaf.wafwaf.util.UnixTime;

public class AV {

    private long eventTime;
    private String eventType;
    private String fileName;
    private String description;
    private int imgId;
    private String account;

    public void setImgId(String eventType){
        switch (eventType){
            case "Новый"   : this.imgId = R.drawable.add_file_color; break;
            case "Изменен" : this.imgId = R.drawable.edit_file_color;break;
            case "Удален"  : this.imgId = R.drawable.delete_file_color;break;
        }

    }

    public AV(){}

    public AV(long eventTime, String eventType, String fileName, String SuspiciousType, String SuspiciousDescription, String account) {
        this.eventTime = eventTime;
        this.eventType = eventType;
        this.fileName = fileName;
        this.description = SuspiciousType + SuspiciousDescription;
        switch (eventType){
            case "Новый"   : this.imgId = R.drawable.add_file_color; break;
            case "Изменен" : this.imgId = R.drawable.edit_file_color;break;
            case "Удален"  : this.imgId = R.drawable.delete_file_color;break;
        }
        this.account = account;

    }

    public String getEventTime(){
        return UnixTime.toDate(eventTime);
    }

    public long getEventTimeUnix(){return eventTime;}

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


    public void setEventTime(long eventTime){
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
