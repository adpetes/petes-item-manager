package com.petesitemmanager.pim.service.dto;

public class TransferDto {
    InventoryItemInstanceDto inventoryItemInstance;

    InventoryItemDto inventoryItem;

    Integer stackSize = 1;

    String sourceCharacterId;

    String targetCharacterId;

    Boolean toVault;

    public InventoryItemInstanceDto getInventoryItemInstance() {
        return inventoryItemInstance;
    }

    public void setInventoryItemInstance(InventoryItemInstanceDto inventoryItemInstance) {
        this.inventoryItemInstance = inventoryItemInstance;
    }

    public InventoryItemDto getInventoryItem() {
        return inventoryItem;
    }

    public void setInventoryItem(InventoryItemDto inventoryItem) {
        this.inventoryItem = inventoryItem;
    }

    public Integer getStackSize() {
        return stackSize;
    }

    public void setStackSize(Integer stackSize) {
        this.stackSize = stackSize;
    }

    public String getSourceCharacterId() {
        return sourceCharacterId;
    }

    public void setSourceCharacterId(String characterId) {
        this.sourceCharacterId = characterId;
    }

    public Boolean getToVault() {
        return toVault;
    }

    public void setToVault(Boolean toVault) {
        this.toVault = toVault;
    }

    public String getTargetCharacterId() {
        return targetCharacterId;
    }

    public void setTargetCharacterId(String targetCharacterId) {
        this.targetCharacterId = targetCharacterId;
    }

}
