package com.petesitemmanager.pim.service.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RotationDto {
    private Map<String, MilestoneDto> data = new HashMap<>();

    public Map<String, MilestoneDto> getData() {
        return data;
    }

    public void setData(Map<String, MilestoneDto> data) {
        this.data = data;
    }

    public void addMilestone(String type, MilestoneDto milestone) {
        data.put(type, milestone);
    }
}
