package com.petesitemmanager.pim.service.dto;

import com.petesitemmanager.pim.domain.enums.DamageType;
import com.petesitemmanager.pim.domain.enums.ItemType;

public class InventoryItemDto {

    public InventoryItemDto(String name, String iconUrl, String hashVal) {
        this.hashVal = hashVal;
        this.name = name;
        this.iconUrl = iconUrl;
    }

    public InventoryItemDto() {
    }

    private String hashVal;

    private String name;

    private String damageType;

    private String iconUrl;

    private String itemType;

    private InventoryItemInstanceDto inventoryItemInstance;

    public String getHashVal() {
        return hashVal;
    }

    public void setHashVal(String hashVal) {
        this.hashVal = hashVal;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDamageType() {
        return damageType;
    }

    public void setDamageType(int damageTypeVal) {
        String damageType = DamageType.getType(damageTypeVal);
        this.damageType = damageType;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public InventoryItemInstanceDto getInventoryItemInstance() {
        return inventoryItemInstance;
    }

    public void setInventoryItemInstance(InventoryItemInstanceDto inventoryItemInstanceDto) {
        this.inventoryItemInstance = inventoryItemInstanceDto;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(Long itemTypeHash) {
        String itemType = ItemType.getType(itemTypeHash);
        this.itemType = itemType;
    }

    // public boolean isEquipped() {
    // return isEquipped;
    // }

    // public void setEquipped(boolean isEquipped) {
    // this.isEquipped = isEquipped;
    // }
}
