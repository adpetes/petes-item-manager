package com.petesitemmanager.pim.domain.enums;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.util.Pair;

public class ErrorCodeType {
    public static final Map<Integer, String> errorCodeTypeMap = new HashMap<>();

    static {
        errorCodeTypeMap.put(1, "unexpected_bungie_api_failure");
        errorCodeTypeMap.put(2, "invalid_session_token");
        errorCodeTypeMap.put(3, "SOLAR");
        errorCodeTypeMap.put(4, "VOID");
        errorCodeTypeMap.put(5, "RAID");
        errorCodeTypeMap.put(6, "STASIS");
        errorCodeTypeMap.put(7, "STRAND");
    }

    public static String getType(int val) {
        if (errorCodeTypeMap.containsKey(val)) {
            return errorCodeTypeMap.get(val);
        } else {
            return null;
        }
    }
}
