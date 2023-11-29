package com.petesitemmanager.pim.domain.enums;

import java.util.HashMap;
import java.util.Map;

public class DungeonHash {
    public static final Map<Long, String> dungeonMap = new HashMap<>();

    static {
        dungeonMap.put(422102671L, "Pit of Heresy");
        dungeonMap.put(1742973996L, "Shattered Throne");
        dungeonMap.put(478604913L, "Prophecy");
        dungeonMap.put(526718853L, "Spire of the Watcher");
        dungeonMap.put(3618845105L, "Duality");
        dungeonMap.put(390471874L, "Ghosts of the Deep");
    }

    public static String getType(Long hashValue) {
        if (dungeonMap.containsKey(hashValue)) {
            return dungeonMap.get(hashValue);
        } else {
            return null;
        }
    }
}
