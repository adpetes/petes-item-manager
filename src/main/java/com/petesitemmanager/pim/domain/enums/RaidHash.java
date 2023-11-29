package com.petesitemmanager.pim.domain.enums;

import java.util.HashMap;
import java.util.Map;

public class RaidHash {
    public static final Map<Long, String> raidMap = new HashMap<>();

    static {
        raidMap.put(3181387331L, "Last Wish");
        raidMap.put(2712317338L, "Garden of Salvation");
        raidMap.put(541780856L, "Deep Stone Crypt");
        raidMap.put(2136320298L, "Vow of Disciple");
        raidMap.put(1888320892L, "Vault of Glass");
        raidMap.put(292102995L, "King's Fall");
        raidMap.put(3699252268L, "Root of Nightmares");
        raidMap.put(540415767L, "Crota's End");
    }

    public static String getType(Long hashValue) {
        if (raidMap.containsKey(hashValue)) {
            return raidMap.get(hashValue);
        } else {
            return null;
        }
    }
}
