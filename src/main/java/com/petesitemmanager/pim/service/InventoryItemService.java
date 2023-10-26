package com.petesitemmanager.pim.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.PostConstruct;
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
import com.petesitemmanager.pim.exception.CustomException;
import com.petesitemmanager.pim.repository.InventoryItemRepository;

@Service
@Transactional
public class InventoryItemService {

    private final Logger log = LoggerFactory.getLogger(InventoryItem.class);

    @Value("${bungie.api-key}")
    private String API_KEY;

    @Autowired
    private InventoryItemRepository inventoryItemRepository;

    @Transactional(readOnly = true)
    public Optional<InventoryItem> findByHashVal(Long hashVal) {
        log.debug("Request to get InventoryItem by hashVal : {}", hashVal);
        return inventoryItemRepository.findByHashVal(hashVal);
    }

    @PostConstruct
    public void getAndSaveInventoryItemData() throws CustomException {
        try {
            ResponseEntity<String> inventoryItemsRes = getInventoryItemData();

            JSONObject inventoryItemsJson = new JSONObject(inventoryItemsRes.getBody());
            List<InventoryItem> inventoryItems = new ArrayList<>();

            for (String hashValKey : inventoryItemsJson.keySet()) {
                JSONObject itemJson = inventoryItemsJson.getJSONObject(hashValKey);
                if (isWeaponArmourPerkOrMod(itemJson) || isEmblem(itemJson)) {
                    String name = null, icon = null;
                    Integer damageType = 0;
                    Long itemType = null;
                    JSONObject displayProperties = itemJson.getJSONObject("displayProperties");

                    if (displayProperties.has("icon")) {
                        icon = displayProperties.getString("icon");
                    }
                    name = displayProperties.getString("name");
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
                    inventoryItems.add(new InventoryItem(Long.parseLong(hashValKey),
                            name,
                            icon,
                            damageType,
                            itemType));
                }
            }
            inventoryItemRepository.saveAll(inventoryItems);
        } catch (Exception e) {
            throw new CustomException("Error loading inventory items: " +
                    e.getLocalizedMessage());
        }
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
            if (itemJson.getString("itemTypeDisplayName").equals("Festival Mask")) {
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

    private ResponseEntity<String> getInventoryItemData() throws CustomException {
        String url = getInventoryItemRequestUrl();

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
                    "Error retrieving inventory items; status code: " + response.getStatusCodeValue());
        }
    }

    private String getInventoryItemRequestUrl() throws CustomException {
        String manifestUrl = "https://www.bungie.net/Platform/Destiny2/Manifest/";

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-Key", API_KEY);
        HttpEntity<String> manifestRequest = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(manifestUrl, HttpMethod.GET, manifestRequest,
                String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            JSONObject manifest = new JSONObject(response.getBody());
            return "https://www.bungie.net" + manifest.getJSONObject("Response")
                    .getJSONObject("jsonWorldComponentContentPaths")
                    .getJSONObject("en")
                    .getString("DestinyInventoryItemDefinition");

        } else {
            throw new CustomException(
                    "Error retrieving inventory items; status code: " + response.getStatusCodeValue());
        }
    }
}
