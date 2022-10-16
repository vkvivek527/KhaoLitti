package com.khaolitti.khao;

import java.util.Date;

public class RewardModel {
    private String type;
    private String lowerLimit;
    private String upperLimit;
    private String dicsORamt;
    private String coupanBody;
    private Date timestamp;
    private Boolean alreadyUsed;
    private String coupanId;


    public RewardModel(String coupanId,String type, String lowerLimit, String upperLimit, String dicsORamt, String coupanBody, Date timestamp,Boolean alreadyUsed) {
        this.type = type;
        this.coupanId=coupanId;
        this.lowerLimit = lowerLimit;
        this.upperLimit = upperLimit;
        this.dicsORamt = dicsORamt;
        this.coupanBody = coupanBody;
        this.timestamp = timestamp;
        this.alreadyUsed=alreadyUsed;
    }

    public Boolean getAlreadyUsed() {
        return alreadyUsed;
    }

    public String getCoupanId() {
        return coupanId;
    }

    public void setCoupanId(String coupanId) {
        this.coupanId = coupanId;
    }

    public void setAlreadyUsed(Boolean alreadyUsed) {
        this.alreadyUsed = alreadyUsed;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLowerLimit() {
        return lowerLimit;
    }

    public void setLowerLimit(String lowerLimit) {
        this.lowerLimit = lowerLimit;
    }

    public String getUpperLimit() {
        return upperLimit;
    }

    public void setUpperLimit(String upperLimit) {
        this.upperLimit = upperLimit;
    }

    public String getDicsORamt() {
        return dicsORamt;
    }

    public void setDicsORamt(String discount) {
        this.dicsORamt = discount;
    }

    public String getCoupanBody() {
        return coupanBody;
    }

    public void setCoupanBody(String coupanBody) {
        this.coupanBody = coupanBody;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
