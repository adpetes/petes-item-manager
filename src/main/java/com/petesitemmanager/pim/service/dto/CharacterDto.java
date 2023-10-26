package com.petesitemmanager.pim.service.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CharacterDto {

    public CharacterDto() {

    }

    private String characterId;

    private String characterClass;

    private int light;

    private InventoryItemDto emblem;

    private InventoryItemDto kineticEquipped;

    private InventoryItemDto energyEquipped;

    private InventoryItemDto powerEquipped;

    private InventoryItemDto helmetEquipped;

    private InventoryItemDto gauntletsEquipped;

    private InventoryItemDto chestEquipped;

    private InventoryItemDto bootsEquipped;

    private InventoryItemDto classItemEquipped;

    private Map<String, InventoryItemDto> kineticInInventory = new HashMap<>();

    private Map<String, InventoryItemDto> energyInInventory = new HashMap<>();

    private Map<String, InventoryItemDto> powerInInventory = new HashMap<>();

    private Map<String, InventoryItemDto> helmetInInventory = new HashMap<>();

    private Map<String, InventoryItemDto> gauntletsInInventory = new HashMap<>();

    private Map<String, InventoryItemDto> chestInInventory = new HashMap<>();

    private Map<String, InventoryItemDto> bootsInInventory = new HashMap<>();

    private Map<String, InventoryItemDto> classItemInInventory = new HashMap<>();

    public String getCharacterClass() {
        return characterClass;
    }

    public void setCharacterClass(String characterClass) {
        this.characterClass = characterClass;
    }

    public InventoryItemDto getEmblem() {
        return emblem;
    }

    public void setEmblem(InventoryItemDto emblem) {
        this.emblem = emblem;
    }

    public InventoryItemDto getKineticEquipped() {
        return kineticEquipped;
    }

    public void setKineticEquipped(InventoryItemDto kineticEquipped) {
        this.kineticEquipped = kineticEquipped;
    }

    public InventoryItemDto getEnergyEquipped() {
        return energyEquipped;
    }

    public void setEnergyEquipped(InventoryItemDto energyEquipped) {
        this.energyEquipped = energyEquipped;
    }

    public InventoryItemDto getPowerEquipped() {
        return powerEquipped;
    }

    public void setPowerEquipped(InventoryItemDto powerEquipped) {
        this.powerEquipped = powerEquipped;
    }

    public InventoryItemDto getHelmetEquipped() {
        return helmetEquipped;
    }

    public void setHelmetEquipped(InventoryItemDto helmetEquipped) {
        this.helmetEquipped = helmetEquipped;
    }

    public InventoryItemDto getGauntletsEquipped() {
        return gauntletsEquipped;
    }

    public void setGauntletsEquipped(InventoryItemDto gauntletsEquipped) {
        this.gauntletsEquipped = gauntletsEquipped;
    }

    public InventoryItemDto getChestEquipped() {
        return chestEquipped;
    }

    public void setChestEquipped(InventoryItemDto chestEquipped) {
        this.chestEquipped = chestEquipped;
    }

    public InventoryItemDto getBootsEquipped() {
        return bootsEquipped;
    }

    public void setBootsEquipped(InventoryItemDto bootsEquipped) {
        this.bootsEquipped = bootsEquipped;
    }

    public InventoryItemDto getClassItemEquipped() {
        return classItemEquipped;
    }

    public void setClassItemEquipped(InventoryItemDto classItemEquipped) {
        this.classItemEquipped = classItemEquipped;
    }

    public Map<String, InventoryItemDto> getEnergyInInventory() {
        return energyInInventory;
    }

    public void setEnergyInInventory(Map<String, InventoryItemDto> energyInInventory) {
        this.energyInInventory = energyInInventory;
    }

    public Map<String, InventoryItemDto> getPowerInInventory() {
        return powerInInventory;
    }

    public void setPowerInInventory(Map<String, InventoryItemDto> powerInInventory) {
        this.powerInInventory = powerInInventory;
    }

    public Map<String, InventoryItemDto> getHelmetInInventory() {
        return helmetInInventory;
    }

    public void setHelmetInInventory(Map<String, InventoryItemDto> helmetInInventory) {
        this.helmetInInventory = helmetInInventory;
    }

    public Map<String, InventoryItemDto> getGauntletsInInventory() {
        return gauntletsInInventory;
    }

    public void setGauntletsInInventory(Map<String, InventoryItemDto> gauntletInInventory) {
        this.gauntletsInInventory = gauntletInInventory;
    }

    public Map<String, InventoryItemDto> getChestInInventory() {
        return chestInInventory;
    }

    public void setChestInInventory(Map<String, InventoryItemDto> chestInInventory) {
        this.chestInInventory = chestInInventory;
    }

    public Map<String, InventoryItemDto> getBootsInInventory() {
        return bootsInInventory;
    }

    public void setBootsInInventory(Map<String, InventoryItemDto> bootsInInventory) {
        this.bootsInInventory = bootsInInventory;
    }

    public Map<String, InventoryItemDto> getClassItemInInventory() {
        return classItemInInventory;
    }

    public void setClassItemInInventory(Map<String, InventoryItemDto> classItemInInventory) {
        this.classItemInInventory = classItemInInventory;
    }

    public Map<String, InventoryItemDto> getKineticInInventory() {
        return kineticInInventory;
    }

    public void setKineticInInventory(Map<String, InventoryItemDto> kineticInInventory) {
        this.kineticInInventory = kineticInInventory;
    }

    public int getLight() {
        return light;
    }

    public void setLight(int light) {
        this.light = light;
    }

    public String getCharacterId() {
        return characterId;
    }

    public void setCharacterId(String characterId) {
        this.characterId = characterId;
    }

    public void setEquippedItem(InventoryItemDto inventoryItemDto) {
        switch (inventoryItemDto.getItemType()) {
            case "KINETIC":
                kineticEquipped = inventoryItemDto;
                break;
            case "ENERGY":
                energyEquipped = inventoryItemDto;
                break;
            case "POWER":
                powerEquipped = inventoryItemDto;
                break;
            case "HELMET":
                helmetEquipped = inventoryItemDto;
                break;
            case "CHEST":
                chestEquipped = inventoryItemDto;
                break;
            case "GAUNTLETS":
                gauntletsEquipped = inventoryItemDto;
                break;
            case "BOOTS":
                bootsEquipped = inventoryItemDto;
                break;
            case "CLASS_ITEM":
                classItemEquipped = inventoryItemDto;
                break;
            case "EMBLEM":
                emblem = inventoryItemDto;
                break;
        }
    }

    public void addToItemInventory(String itemInstanceId, InventoryItemDto inventoryItemDto) {
        switch (inventoryItemDto.getItemType()) {
            case "KINETIC":
                kineticInInventory.put(itemInstanceId, inventoryItemDto);
                break;
            case "ENERGY":
                energyInInventory.put(itemInstanceId, inventoryItemDto);
                break;
            case "POWER":
                powerInInventory.put(itemInstanceId, inventoryItemDto);
                break;
            case "HELMET":
                helmetInInventory.put(itemInstanceId, inventoryItemDto);
                break;
            case "CHEST":
                chestInInventory.put(itemInstanceId, inventoryItemDto);
                break;
            case "GAUNTLETS":
                gauntletsInInventory.put(itemInstanceId, inventoryItemDto);
                break;
            case "BOOTS":
                bootsInInventory.put(itemInstanceId, inventoryItemDto);
                break;
            case "CLASS_ITEM":
                classItemInInventory.put(itemInstanceId, inventoryItemDto);
                break;
        }
    }

}
