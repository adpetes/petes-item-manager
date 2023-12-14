package com.petesitemmanager.pim.service.dto;

import java.util.ArrayList;
import java.util.List;

public class MilestoneDto {
    private String name;

    private String iconUrl;

    private String description;

    private List<InventoryItemDto> rewards = new ArrayList<>();

    public MilestoneDto(String name, String iconUrl, String description) {
        this.name = name;
        this.iconUrl = iconUrl;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<InventoryItemDto> getRewards() {
        return rewards;
    }

    public void setRewards(List<InventoryItemDto> rewards) {
        this.rewards = rewards;
    }

    public void addReward(InventoryItemDto reward) {
        rewards.add(reward);
    }
}
