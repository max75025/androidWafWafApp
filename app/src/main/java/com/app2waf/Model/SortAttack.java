package com.app2waf.Model;

public class SortAttack {
    private String IpAddr;
    private String Country;
    private long     StartTime;
    private long     EndTime;
    private String  ResultTypes;

    public String getIpAddr() {
        return IpAddr;
    }

    public void setIpAddr(String ipAddr) {
        IpAddr = ipAddr;
    }

    public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        Country = country;
    }

    public long getStartTime() {
        return StartTime;
    }

    public void setStartTime(long startTime) {
        StartTime = startTime;
    }

    public long getEndTime() {
        return EndTime;
    }

    public void setEndTime(long endTime) {
        EndTime = endTime;
    }

    public String getResultTypes() {
        return ResultTypes;
    }

    public void setResultTypes(String resultTypes) {
        ResultTypes = resultTypes;
    }

    public SortAttack(){}
}
