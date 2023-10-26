package com.petesitemmanager.pim.service.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VaultDto {

    public VaultDto() {

    }

    private Map<String, InventoryItemDto> kinetic = new HashMap<>();

    private Map<String, InventoryItemDto> energy = new HashMap<>();

    private Map<String, InventoryItemDto> power = new HashMap<>();

    private Map<String, InventoryItemDto> helmet = new HashMap<>();

    private Map<String, InventoryItemDto> gauntlets = new HashMap<>();

    private Map<String, InventoryItemDto> chest = new HashMap<>();

    private Map<String, InventoryItemDto> boots = new HashMap<>();

    private Map<String, InventoryItemDto> classItem = new HashMap<>();

    public Map<String, InventoryItemDto> getKinetic() {
        return kinetic;
    }

    public void setKinetic(Map<String, InventoryItemDto> kinetic) {
        this.kinetic = kinetic;
    }

    public Map<String, InventoryItemDto> getEnergy() {
        return energy;
    }

    public void setEnergy(Map<String, InventoryItemDto> energy) {
        this.energy = energy;
    }

    public Map<String, InventoryItemDto> getPower() {
        return power;
    }

    public void setPower(Map<String, InventoryItemDto> power) {
        this.power = power;
    }

    public Map<String, InventoryItemDto> getHelmet() {
        return helmet;
    }

    public void setHelmet(Map<String, InventoryItemDto> helmet) {
        this.helmet = helmet;
    }

    public Map<String, InventoryItemDto> getGauntlets() {
        return gauntlets;
    }

    public void setGauntlets(Map<String, InventoryItemDto> gauntlet) {
        this.gauntlets = gauntlet;
    }

    public Map<String, InventoryItemDto> getChest() {
        return chest;
    }

    public void setChest(Map<String, InventoryItemDto> chest) {
        this.chest = chest;
    }

    public Map<String, InventoryItemDto> getBoots() {
        return boots;
    }

    public void setBoots(Map<String, InventoryItemDto> boots) {
        this.boots = boots;
    }

    public Map<String, InventoryItemDto> getClassItem() {
        return classItem;
    }

    public void setClassItem(Map<String, InventoryItemDto> classItem) {
        this.classItem = classItem;
    }

    public void addToItemInventory(String itemInstanceId, InventoryItemDto inventoryItemDto) {
        switch (inventoryItemDto.getItemType()) {
            case "KINETIC":
                kinetic.put(itemInstanceId, inventoryItemDto);
                break;
            case "ENERGY":
                energy.put(itemInstanceId, inventoryItemDto);
                break;
            case "POWER":
                power.put(itemInstanceId, inventoryItemDto);
                break;
            case "HELMET":
                helmet.put(itemInstanceId, inventoryItemDto);
                break;
            case "CHEST":
                chest.put(itemInstanceId, inventoryItemDto);
                break;
            case "GAUNTLETS":
                gauntlets.put(itemInstanceId, inventoryItemDto);
                break;
            case "BOOTS":
                boots.put(itemInstanceId, inventoryItemDto);
                break;
            case "CLASS_ITEM":
                classItem.put(itemInstanceId, inventoryItemDto);
                break;
        }
    }

    // public void sortAll() {
    // Collections.sort(helmet,
    // Collections.reverseOrder(InventoryItemInstanceDto.lightComparator));
    // Collections.sort(chest,
    // Collections.reverseOrder(InventoryItemInstanceDto.lightComparator));
    // Collections.sort(gauntlet,
    // Collections.reverseOrder(InventoryItemInstanceDto.lightComparator));
    // Collections.sort(boots,
    // Collections.reverseOrder(InventoryItemInstanceDto.lightComparator));
    // Collections.sort(classItem,
    // Collections.reverseOrder(InventoryItemInstanceDto.lightComparator));
    // Collections.sort(kinetic,
    // Collections.reverseOrder(InventoryItemInstanceDto.lightComparator));
    // Collections.sort(energy,
    // Collections.reverseOrder(InventoryItemInstanceDto.lightComparator));
    // Collections.sort(power,
    // Collections.reverseOrder(InventoryItemInstanceDto.lightComparator));
    // }
}
