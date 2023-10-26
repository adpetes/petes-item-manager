package com.petesitemmanager.pim.service.dto;

import com.petesitemmanager.pim.domain.enums.StatType;

public class StatDto {
    private String statType;

    private int statValue;

    public StatDto() {

    }

    public String getStatType() {
        return statType;
    }

    public void setStatType(Long statHash) {
        String statType = StatType.getType(statHash);
        this.statType = statType;
    }

    public int getStatValue() {
        return statValue;
    }

    public void setStatValue(int statValue) {
        this.statValue = statValue;
    }

}
