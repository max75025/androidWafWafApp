package com.app2waf.Model;

import com.app2waf.util.UnixTime;

public class   Antivirus{
    public void setApiKey(String apiKey) {
        ApiKey = apiKey;
    }

    public void setEventTime(long eventTime) {
        EventTime = eventTime;
    }

    public void setEventType(String eventType) {
        EventType = eventType;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public void setFileExt(String fileExt) {
        FileExt = fileExt;
    }

    public void setFilePath(String filePath) {
        FilePath = filePath;
    }

    public void setSuspiciousType(String suspiciousType) {
        SuspiciousType = suspiciousType;
    }

    public void setSuspiciousDescription(String suspiciousDescription) {
        SuspiciousDescription = suspiciousDescription;
    }

    public String getEventTime() {
        return UnixTime.toDate(EventTime);

    }

    public long getEventTimeUnix() {
        return EventTime;

    }

    public String getEventType() {
        return EventType;
    }

    public String getFileName() {
        return FileName;
    }

    public String getFileExt() {
        return FileExt;
    }

    public String getFilePath() {
        return FilePath;
    }

    public String getSuspiciousType() {
        return SuspiciousType;
    }

    public String getSuspiciousDescription() {
        return SuspiciousDescription;
    }

    private String  ApiKey;
    private long     EventTime;
    private String  EventType;
    private String  FileName;
    private String  FileExt;
    private String  FilePath;
    private String  SuspiciousType;
    private String  SuspiciousDescription;

    public String getApiKey() {
        return ApiKey;
    }

    public  Antivirus(){}
}
