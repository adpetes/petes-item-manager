package com.petesitemmanager.pim.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.hibernate.cache.spi.support.AbstractReadWriteAccess.Item;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.petesitemmanager.pim.domain.InventoryItem;
import com.petesitemmanager.pim.domain.enums.MilestoneHashTranslate;
import com.petesitemmanager.pim.exception.CustomException;

@Service
@Transactional
public class InventoryItemService {

    private Map<Long, InventoryItem> inventoryItemsData = null;

    private Map<Long, List<InventoryItem>> collectiblesData = null;

    private String manifestVersion = "";

    @Value("${bungie.api-key}")
    private String API_KEY;

    public Map<Long, InventoryItem> getAndMayUpdateInventoryItemsMap() throws CustomException {
        JSONObject manifest = getManifest();
        String curManifestVersion = manifest.getJSONObject("Response").getString("version");
        if (!curManifestVersion.equals(this.manifestVersion)) {
            this.manifestVersion = curManifestVersion;
            this.inventoryItemsData = getInventoryItemsMap(manifest);
        }
        return this.inventoryItemsData;
    }

    public Map<Long, List<InventoryItem>> getAndMayUpdateCollectiblesMap() throws CustomException {
        JSONObject manifest = getManifest();
        String curManifestVersion = manifest.getJSONObject("Response").getString("version");
        if (!curManifestVersion.equals(this.manifestVersion)) {
            this.manifestVersion = curManifestVersion;
            this.inventoryItemsData = getInventoryItemsMap(manifest); // collectibles depend on inventory items ->
                                                                      // update them first
            this.collectiblesData = getCollectiblesMap(manifest);
        }
        return this.collectiblesData;
    }

    public JSONObject getMilestonesManifest() throws CustomException {
        JSONObject manifest = getManifest();
        ResponseEntity<String> response = getManifestDefinition(manifest, "DestinyMilestoneDefinition");

        return new JSONObject(response.getBody());
    }

    public JSONObject getActivityManifest() throws CustomException {
        JSONObject manifest = getManifest();
        ResponseEntity<String> response = getManifestDefinition(manifest, "DestinyActivityDefinition");

        return new JSONObject(response.getBody());
    }

    public JSONObject getCollectiblesManifest() throws CustomException {
        JSONObject manifest = getManifest();
        ResponseEntity<String> response = getManifestDefinition(manifest, "DestinyCollectibleDefinition");

        return new JSONObject(response.getBody());
    }

    public Map<Long, List<InventoryItem>> getCollectiblesMap(JSONObject manifest) throws CustomException {
        ResponseEntity<String> collectiblesRes = getManifestDefinition(manifest,
                "DestinyCollectibleDefinition");

        JSONObject collectiblesJson = new JSONObject(collectiblesRes.getBody());
        Map<Long, List<InventoryItem>> collectibles = new HashMap<>();

        for (String hashValKey : collectiblesJson.keySet()) {
            JSONObject collectible = collectiblesJson.getJSONObject(hashValKey);
            if (collectible.has("sourceHash")) {
                Long sourceHash = collectible.getLong("sourceHash");
                Long milestoneHash = MilestoneHashTranslate.getType(sourceHash);
                if (milestoneHash != null) {
                    if (collectible.has("itemHash")) {
                        Long itemHash = collectible.getLong("itemHash");
                        if (inventoryItemsData.containsKey(itemHash)) {
                            InventoryItem inventoryItem = inventoryItemsData.get(itemHash);
                            collectibles.computeIfAbsent(milestoneHash, k -> new ArrayList<>()).add(inventoryItem);
                        }
                    }
                }
            }
        }

        return collectibles;
    }

    public Map<Long, InventoryItem> getInventoryItemsMap(JSONObject manifest) throws CustomException {
        try {
            ResponseEntity<String> inventoryItemsRes = getManifestDefinition(manifest,
                    "DestinyInventoryItemLiteDefinition");

            JSONObject inventoryItemsJson = new JSONObject(inventoryItemsRes.getBody());
            Map<Long, InventoryItem> inventoryItems = new HashMap<>();
            Set<Long> hashesToExclude = new HashSet<>();
            hashesToExclude.add(1449602859L);
            hashesToExclude.add(1404791674L);
            hashesToExclude.add(1043342778L);

            for (String hashValKey : inventoryItemsJson.keySet()) {
                JSONObject itemJson = inventoryItemsJson.getJSONObject(hashValKey);
                if ((isWeaponArmourPerkOrMod(itemJson) || isEmblem(itemJson))
                        && !isExcluded(itemJson, hashesToExclude)) {
                    String name = null, icon = null;
                    Integer damageType = 0;
                    Long itemType = null;
                    Integer itemSubType = null;
                    JSONObject displayProperties = itemJson.getJSONObject("displayProperties");

                    if (displayProperties.has("icon")) {
                        icon = displayProperties.getString("icon");
                    }
                    name = displayProperties.getString("name");
                    if (itemJson.has("itemSubType")) {
                        itemSubType = itemJson.getInt("itemSubType");
                    }
                    if (itemJson.has("inventory")
                            && itemJson.getJSONObject("inventory").has("bucketTypeHash")) {
                        itemType = itemJson.getJSONObject("inventory").getLong("bucketTypeHash");
                    } else {
                        continue;
                    }
                    if (itemJson.has("defaultDamageType")) {
                        damageType = itemJson.getInt("defaultDamageType");
                    }

                    if (isEmblem(itemJson)) {
                        if (itemJson.has("secondaryIcon")) {
                            icon = itemJson.getString("secondaryIcon");
                        } else {
                            continue;
                        }
                    }
                    Long key = Long.parseLong(hashValKey);
                    if (name != "" && icon != null) {
                        inventoryItems.put(key, new InventoryItem(key,
                                name,
                                icon,
                                damageType,
                                itemType,
                                itemSubType));
                    }
                }
            }
            return inventoryItems;
        } catch (Exception e) {
            throw new CustomException("Error loading inventory items: " +
                    e.getLocalizedMessage());
        }
    }

    private boolean isExcluded(JSONObject itemJson, Set<Long> hashesToExclude) {
        JSONArray itemCategoryHashes = itemJson.getJSONArray("itemCategoryHashes");
        String name = itemJson.getJSONObject("displayProperties").getString("name");
        String itemTypeDisplayName = itemJson.getString("itemTypeDisplayName");
        for (int i = 0; i < itemCategoryHashes.length(); i++) {
            Long hash = itemCategoryHashes.getLong(i);
            if (hashesToExclude.contains(hash)) {
                return true;
            }
        }
        if (name.equals("Upgrade Armor")) {
            return true;
        }
        if (itemTypeDisplayName.contains("Armor Mod") || itemTypeDisplayName.contains("Weapon Mod")) {
            return true;
        }

        return false;
    }

    private Boolean isEmblem(JSONObject itemJson) {
        if (itemJson.has("itemType") && (itemJson.getInt("itemType") == 14)) {
            return true;
        }
        return false;
    }

    private Boolean isWeaponArmourPerkOrMod(JSONObject itemJson) {
        if (itemJson.has("itemTypeDisplayName")) {
            if (itemJson.getString("itemTypeDisplayName").equals("Transmat Effect")
                    || itemJson.getString("itemTypeDisplayName").contains("Emote")) {
                return false;
            }
            if (itemJson.getString("itemTypeDisplayName").equals("Festival Mask")
                    || itemJson.getString("itemTypeDisplayName").equals("Enhanced Trait")) {
                return true;
            }
        }
        if (itemJson.has("itemType")
                && (itemJson.getInt("itemType") == 2 || itemJson.getInt("itemType") == 3
                        || (itemJson.getInt("itemType") == 19 && itemJson.getInt("itemSubType") == 0))) {
            return true;
        }
        return false;
    }

    private ResponseEntity<String> getManifestDefinition(JSONObject manifest, String definition)
            throws CustomException {
        String url = "https://www.bungie.net" + manifest.getJSONObject("Response")
                .getJSONObject("jsonWorldComponentContentPaths")
                .getJSONObject("en")
                .getString(definition);

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-Key", API_KEY);
        HttpEntity<String> inventoryItemsRequest = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, inventoryItemsRequest,
                String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            return response;
        } else {
            throw new CustomException(
                    "Error retrieving manifest defintion: " + definition + "; status code: "
                            + response.getStatusCodeValue());
        }
    }

    private JSONObject getManifest() throws CustomException {
        String manifestUrl = "https://www.bungie.net/Platform/Destiny2/Manifest/";

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-Key", API_KEY);
        HttpEntity<String> manifestRequest = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(manifestUrl, HttpMethod.GET, manifestRequest,
                String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            JSONObject manifest = new JSONObject(response.getBody());
            return manifest;
        } else {
            throw new CustomException(
                    "Error retrieving inventory items; status code: " + response.getStatusCodeValue());
        }
    }

}
