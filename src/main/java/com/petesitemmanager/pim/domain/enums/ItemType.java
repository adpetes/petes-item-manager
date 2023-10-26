package com.petesitemmanager.pim.domain.enums;

import java.util.HashMap;
import java.util.Map;

public class ItemType {
    public static final Map<Long, String> itemMap = new HashMap<>();

    static {
        itemMap.put(1498876634L, "KINETIC");
        itemMap.put(2465295065L, "ENERGY");
        itemMap.put(953998645L, "POWER");
        itemMap.put(3448274439L, "HELMET");
        itemMap.put(3551918588L, "GAUNTLETS");
        itemMap.put(14239492L, "CHEST");
        itemMap.put(20886954L, "BOOTS");
        itemMap.put(1585787867L, "CLASS_ITEM");
        itemMap.put(4274335291L, "EMBLEM");
    }

    public static String getType(Long hashValue) {
        if (itemMap.containsKey(hashValue)) {
            return itemMap.get(hashValue);
        } else {
            return null;
        }
    }
}
