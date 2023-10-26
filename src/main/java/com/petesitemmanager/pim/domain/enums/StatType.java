package com.petesitemmanager.pim.domain.enums;

import java.util.HashMap;
import java.util.Map;

public class StatType {
    public static final Map<Long, String> statTypeMap = new HashMap<>();

    static {
        statTypeMap.put(155624089L, "STABILITY");
        statTypeMap.put(943549884L, "HANDLING");
        statTypeMap.put(1240592695L, "RANGE");
        statTypeMap.put(1345609583L, "AIM ASSISTANCE");
        statTypeMap.put(2714457168L, "AIRBORNE EFFECTIVENESS");
        statTypeMap.put(2715839340L, "RECOIL DIRECTION");
        statTypeMap.put(3555269338L, "ZOOM");
        statTypeMap.put(3871231066L, "MAGAZINE");
        statTypeMap.put(4043523819L, "IMPACT");
        statTypeMap.put(4188031367L, "RELOAD");
        statTypeMap.put(4284893193L, "ROUNDS PER MINUTE");
        statTypeMap.put(144602215L, "INTELLECT");
        statTypeMap.put(392767087L, "RESILIENCE");
        statTypeMap.put(1735777505L, "DISCIPLINE");
        statTypeMap.put(1943323491L, "RECOVERY");
        statTypeMap.put(2996146975L, "MOBILITY");
        statTypeMap.put(4244567218L, "STRENGTH");
    }

    public static String getType(Long val) {
        if (statTypeMap.containsKey(val)) {
            return statTypeMap.get(val);
        }
        return "???";
    }
}
