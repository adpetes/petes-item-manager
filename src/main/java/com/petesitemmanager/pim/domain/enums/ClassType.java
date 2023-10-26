package com.petesitemmanager.pim.domain.enums;

import java.util.HashMap;
import java.util.Map;

public class ClassType {
    public static final Map<Long, String> classTypeMap = new HashMap<>();

    static {
        classTypeMap.put(671679327L, "Hunter");
        classTypeMap.put(3655393761L, "Titan");
        classTypeMap.put(2271682572L, "Warlock");
    }

    public static String getType(long val) {
        if (classTypeMap.containsKey(val)) {
            return classTypeMap.get(val);
        } else {
            return null;
        }
    }
}
