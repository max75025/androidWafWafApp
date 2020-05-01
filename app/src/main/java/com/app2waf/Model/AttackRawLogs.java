package com.app2waf.Model;

import com.app2waf.util.UnixTime;

import java.io.Serializable;

public class AttackRawLogs implements Serializable {
    private long attackTime;
    private String attackDirection;
    private String attackPackage;

    public String getAttackTime(){ return UnixTime.toDate(attackTime); }
    public long getAttackTimeUnix(){return attackTime;}

    public String getAttackDirection(){
        return attackDirection;
    }
    public String getAttackPackage(){
        return attackPackage;
    }

    public void setAttackTime(long time){
        this.attackTime = time;
    }
    public void setAttackDirection(String attackDirection){
        this.attackDirection = attackDirection;
    }
    public void setAttackPackage(String attackPackage){
        this.attackPackage = attackPackage;
    }

    public AttackRawLogs(long time, String attackDirection, String attackPackage){
        this.attackTime = time;
        this.attackDirection = attackDirection;
        this.attackPackage = attackPackage;
    }
    public AttackRawLogs(){

    }
}
