package com.petesitemmanager.pim.domain.enums;

import java.util.HashMap;
import java.util.Map;

public class ItemSubType {
    public static final Map<Integer, String> itemMap = new HashMap<>();

    static {
        itemMap.put(6, "Auto Rifle");
        itemMap.put(7, "Shotgun");
        itemMap.put(8, "Machine Gun");
        itemMap.put(9, "Hand Cannon");
        itemMap.put(10, "Rocket Launcher");
        itemMap.put(11, "Fusion Rifle");
        itemMap.put(12, "Sniper Rifle");
        itemMap.put(13, "Pulse Rifle");
        itemMap.put(14, "Scout Rifle");
        itemMap.put(17, "Sidearm");
        itemMap.put(18, "Sword");
        itemMap.put(19, "Mask");
        itemMap.put(22, "Linear Fusion Rifle");
        itemMap.put(23, "Grenade Launcher");
        itemMap.put(24, "Submachine Gun");
        itemMap.put(25, "Trace Rifle");
        itemMap.put(26, "Helmet");
        itemMap.put(27, "Gauntlets");
        itemMap.put(28, "Chest");
        itemMap.put(29, "Legs");
        itemMap.put(30, "Class Item");
        itemMap.put(31, "Bow");
        itemMap.put(33, "Glaive");
    }

    public static String getType(Integer hashValue) {
        if (itemMap.containsKey(hashValue)) {
            return itemMap.get(hashValue);
        } else {
            return null;
        }
    }
}
