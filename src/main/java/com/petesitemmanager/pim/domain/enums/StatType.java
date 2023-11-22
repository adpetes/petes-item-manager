package com.petesitemmanager.pim.domain.enums;

import java.util.HashMap;
import java.util.Map;

public class StatType {
    public static final Map<Long, String> statTypeMap = new HashMap<>();

    static {
        statTypeMap.put(3022301683L, "Charge Rate");
        statTypeMap.put(2837207746L, "Swing Speed");
        statTypeMap.put(925767036L, "Ammo Capacity");
        statTypeMap.put(3736848092L, "Guard Endurance");
        statTypeMap.put(209426660L, "Guard Resistance");
        statTypeMap.put(1842278586L, "Shield Duration");
        statTypeMap.put(2523465841L, "Velocity");
        statTypeMap.put(3614673599L, "Blast Radius");
        statTypeMap.put(155624089L, "Stability");
        statTypeMap.put(2961396640L, "Charge Time");
        statTypeMap.put(447667954L, "Draw Time");
        statTypeMap.put(1591432999L, "Accuracy");
        statTypeMap.put(943549884L, "Handling");
        statTypeMap.put(1240592695L, "Range");
        statTypeMap.put(1345609583L, "Aim Assistance");
        statTypeMap.put(2714457168L, "Airborne");
        statTypeMap.put(2715839340L, "Recoil Direction");
        statTypeMap.put(3555269338L, "Zoom");
        statTypeMap.put(3871231066L, "Magazine");
        statTypeMap.put(4043523819L, "Impact");
        statTypeMap.put(4188031367L, "Reload Speed");
        statTypeMap.put(4284893193L, "RPM");
        statTypeMap.put(144602215L, "Intellect");
        statTypeMap.put(392767087L, "Resilience");
        statTypeMap.put(1735777505L, "Discipline");
        statTypeMap.put(1943323491L, "Recovery");
        statTypeMap.put(2996146975L, "Mobility");
        statTypeMap.put(4244567218L, "Strength");
    }

    public static String getType(Long val) {
        if (statTypeMap.containsKey(val)) {
            return statTypeMap.get(val);
        }
        return "???";
    }
}
