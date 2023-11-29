package com.petesitemmanager.pim.domain.enums;

import java.util.HashMap;
import java.util.Map;

// Converts collectibles hash to milestones hash 
public class MilestoneHashTranslate {
    public static final Map<Long, Long> milestoneMap = new HashMap<>();

    static {
        milestoneMap.put(2455011338L, 3181387331L);
        milestoneMap.put(1491707941L, 2712317338L);
        milestoneMap.put(1405897559L, 541780856L);
        milestoneMap.put(1007078046L, 2136320298L);
        milestoneMap.put(2065138144L, 1888320892L);
        milestoneMap.put(160129377L, 292102995L);
        milestoneMap.put(3190710249L, 3699252268L);
        milestoneMap.put(1897187034L, 540415767L);
        milestoneMap.put(1745960977L, 422102671L);
        milestoneMap.put(2559145507L, 1742973996L);
        milestoneMap.put(506073192L, 478604913L);
        milestoneMap.put(1597738585L, 526718853L);
        milestoneMap.put(1282207663L, 3618845105L);
        milestoneMap.put(3288974535L, 390471874L);
        milestoneMap.put(2745272818L, 3883295757L);
        milestoneMap.put(1823766625L, 2668737148L);
        milestoneMap.put(2422551147L, 1221538367L);
    }

    public static Long getType(Long hashValue) {
        if (milestoneMap.containsKey(hashValue)) {
            return milestoneMap.get(hashValue);
        } else {
            return null;
        }
    }
}
