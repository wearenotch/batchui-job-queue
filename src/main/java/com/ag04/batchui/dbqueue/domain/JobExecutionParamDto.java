package com.ag04.batchui.dbqueue.domain;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class JobExecutionParamDto implements Serializable {

    private String typeCd;

    private String keyName;

    private String stringVal;

    private ZonedDateTime dateVal;

    private Long longVal;

    private Double doubleVal;

    private String identifying;

    public String getTypeCd() {
        return typeCd;
    }

    public void setTypeCd(String typeCd) {
        this.typeCd = typeCd;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public String getStringVal() {
        return stringVal;
    }

    public void setStringVal(String stringVal) {
        this.stringVal = stringVal;
    }

    public ZonedDateTime getDateVal() {
        return dateVal;
    }

    public void setDateVal(ZonedDateTime dateVal) {
        this.dateVal = dateVal;
    }

    public Long getLongVal() {
        return longVal;
    }

    public void setLongVal(Long longVal) {
        this.longVal = longVal;
    }

    public Double getDoubleVal() {
        return doubleVal;
    }

    public void setDoubleVal(Double doubleVal) {
        this.doubleVal = doubleVal;
    }

    public String getIdentifying() {
        return identifying;
    }

    public void setIdentifying(String identifying) {
        this.identifying = identifying;
    }

    @Override
    public String toString() {
        return "JobExecutionParamDto{" +
                "typeCd='" + getTypeCd() + "'" +
                ", keyName='" + getKeyName() + "'" +
                ", stringVal='" + getStringVal() + "'" +
                ", dateVal='" + getDateVal() + "'" +
                ", longVal=" + getLongVal() +
                ", doubleVal=" + getDoubleVal() +
                ", identifying='" + getIdentifying() + "'" +
                "}";
    }
}
