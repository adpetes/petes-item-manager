package com.petesitemmanager.pim.domain.enums;

import java.util.HashMap;
import java.util.Map;

public class DamageType {
    public static final Map<Integer, String> damageTypeMap = new HashMap<>();

    static {
        damageTypeMap.put(0, "NONE");
        damageTypeMap.put(1, "KINETIC");
        damageTypeMap.put(2, "ARC");
        damageTypeMap.put(3, "SOLAR");
        damageTypeMap.put(4, "VOID");
        damageTypeMap.put(5, "RAID");
        damageTypeMap.put(6, "STASIS");
        damageTypeMap.put(7, "STRAND");
    }

    public static String getType(int val) {
        if (damageTypeMap.containsKey(val)) {
            return damageTypeMap.get(val);
        } else {
            return null;
        }
    }
}
