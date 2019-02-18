package com.wafwaf.wafwaf;

import java.io.Serializable;

public class AttackRawLogs implements Serializable {
    private String attackTime;
    private String attackDirection;
    private String attackPackage;

    public String getAttackTime(){
        return attackTime;
    }
    public String getAttackDirection(){
        return attackDirection;
    }
    public String getAttackPackage(){
        return attackPackage;
    }

    public void setAttackTime(String time){
        this.attackTime = time;
    }
    public void setAttackDirection(String attackDirection){
        this.attackDirection = attackDirection;
    }
    public void setAttackPackage(String attackPackage){
        this.attackPackage = attackPackage;
    }

    public AttackRawLogs(String time, String attackDirection, String attackPackage){
        this.attackTime = time;
        this.attackDirection = attackDirection;
        this.attackPackage = attackPackage;
    }
    public AttackRawLogs(){

    }
}
