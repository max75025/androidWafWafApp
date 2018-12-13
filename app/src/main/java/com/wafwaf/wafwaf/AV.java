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
}
