package com.petesitemmanager.pim.service.dto;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class InventoryItemInstanceDto {
    // TODO have fields for all things armour and weapon

    private String instanceItemId;

    private int light;

    private List<InventoryItemDto> perks = new ArrayList<>();

    private List<StatDto> stats = new ArrayList<>();

    public int getLight() {
        return light;
    }

    public void setLight(int light) {
        this.light = light;
    }

    public String getInstanceItemId() {
        return instanceItemId;
    }

    public void setInstanceItemId(String instanceItemId) {
        this.instanceItemId = instanceItemId;
    }

    public List<InventoryItemDto> getPerk() {
        return perks;
    }

    public void addPerk(InventoryItemDto perk) {
        this.perks.add(perk);
    }

    public List<StatDto> getStats() {
        return stats;
    }

    public void addStat(StatDto stat) {
        this.stats.add(stat);
    }

    public static Comparator<InventoryItemDto> lightComparator = new Comparator<InventoryItemDto>() {
        @Override
        public int compare(InventoryItemDto item1, InventoryItemDto item2) {
            int light1 = item1.getInventoryItemInstance().getLight();
            int light2 = item2.getInventoryItemInstance().getLight();
            return Integer.compare(light1, light2);
        }
    };
}