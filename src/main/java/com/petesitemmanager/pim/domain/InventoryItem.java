package com.petesitemmanager.pim.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

// @Entity
public class InventoryItem {

    public InventoryItem() {

    }

    public InventoryItem(Long hashVal, String name, String iconUrl) {
        this.hashVal = hashVal;
        this.name = name;
        this.iconUrl = iconUrl;
    }

    public InventoryItem(Long hashVal, String name, String iconUrl, Integer damageType, Long itemType) {
        this.hashVal = hashVal;
        this.name = name;
        this.iconUrl = iconUrl;
        this.damageType = damageType;
        this.itemType = itemType;
    }

    // @Id
    private Long hashVal;

    private String name;

    private String iconUrl;

    private Integer damageType;

    private Long itemType;

    public Long getHashVal() {
        return hashVal;
    }

    public void setHashVal(Long hashVal) {
        this.hashVal = hashVal;
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

    public Integer getDamageType() {
        return damageType;
    }

    public void setDamageType(Integer damageType) {
        this.damageType = damageType;
    }

    public Long getItemType() {
        return itemType;
    }

    public void setItemType(Long itemType) {
        this.itemType = itemType;
    }
}