package com.petesitemmanager.pim.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.petesitemmanager.pim.domain.InventoryItem;
import com.petesitemmanager.pim.domain.User;
import com.petesitemmanager.pim.domain.enums.ClassType;
import com.petesitemmanager.pim.domain.enums.DungeonHash;
import com.petesitemmanager.pim.domain.enums.RaidHash;
import com.petesitemmanager.pim.exception.CustomException;
import com.petesitemmanager.pim.service.dto.CharacterDto;
import com.petesitemmanager.pim.service.dto.InventoryItemDto;
import com.petesitemmanager.pim.service.dto.InventoryItemInstanceDto;
import com.petesitemmanager.pim.service.dto.MilestoneDto;
import com.petesitemmanager.pim.service.dto.ProfileDto;
import com.petesitemmanager.pim.service.dto.RotationDto;
import com.petesitemmanager.pim.service.dto.StatDto;
import com.petesitemmanager.pim.service.dto.TransferDto;
import com.petesitemmanager.pim.service.dto.VaultDto;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.json.JSONArray;
import org.json.JSONObject;

@Service
public class BungieService {

    @Value("${bungie.client-id}")
    private String CLIENT_ID;

    @Value("${bungie.client-secret}")
    private String CLIENT_SECRET;

    @Value("${user.master-token}")
    private String MASTER_SESSION_TOKEN;

    @Value("${user.master-id}")
    private String MASTER_ID;

    @Value("${bungie.api-key}")
    private String API_KEY;

    @Autowired
    private UserService userService;

    @Autowired
    private InventoryItemService inventoryItemService;

    public String getAuthorizationUrl(boolean reauth) throws CustomException {
        RestTemplate restTemplate = new RestTemplate();

        // Define the URL and request headers
        String url = "https://www.bungie.net/en/oauth/authorize?client_id=" + CLIENT_ID
                + "&response_type=code";
        if (reauth) {
            url += "&reauth=true";
        }
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<String> responseEntity = null;
        // Send GET request
        try {
            responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
            throw new CustomException("Unable to connect to Bungie API while requesting auth URL", 1);
        }
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            HttpHeaders responseHeaders = responseEntity.getHeaders();
            List<String> selfUrlHeader = responseHeaders.get("x-selfurl");
            if (selfUrlHeader != null && !selfUrlHeader.isEmpty()) {
                return selfUrlHeader.get(0);
            }
        }

        throw new CustomException(
                "Error retrieving authorization URL; status code: " + responseEntity.getStatusCodeValue(), 1);
    }

    public String processAuthorization(String authCode) throws CustomException {
        String url = "https://www.bungie.net/platform/app/oauth/token/";

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(CLIENT_ID, CLIENT_SECRET);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String requestBody = "grant_type=authorization_code&code=" + authCode;

        HttpEntity<String> tokenRequest = new HttpEntity<>(requestBody, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> tokenResponse = null;

        try {
            tokenResponse = restTemplate.postForEntity(url, tokenRequest, String.class);
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
            throw new CustomException("Unable to connect to Bungie API while requesting access token", 1);
        }

        if (tokenResponse.getStatusCode() == HttpStatus.OK) {
            JSONObject authResponse = new JSONObject(tokenResponse.getBody());
            User newUser = userService.createOrUpdateUser(authResponse, true);
            return newUser.getSessionToken();
        } else {
            throw new CustomException(
                    "Error retrieving access token; status code: " + tokenResponse.getStatusCodeValue(), 3);
        }
    }

    public boolean validateUserSessionToken(Optional<User> user) {
        if (user.isPresent() && user.get().getSessionTokenExpiry() != null) {
            return Instant.now().getEpochSecond() < user.get().getSessionTokenExpiry();
        } else {
            return false;
        }
    }

    public void deleteUserTokenInfo(String sessionToken) throws CustomException {
        Optional<User> user = userService.findUserBySessionToken(sessionToken);
        if (user.isPresent() && user.get().getIsMaster()) {
            return;
        }
        if (validateUserSessionToken(user)) {
            userService.wipeUserTokenInfo(user.get());
        } else {
            throw new CustomException("Invalid session token", 2);
        }
    }

    public String getLinkedProfiles(String sessionToken) throws CustomException {
        User user = getValidatedUserBySessionToken(sessionToken);

        String url = "https://www.bungie.net/Platform/Destiny2/254/Profile/" + user.getBungieId()
                + "/LinkedProfiles/?getAllMemberships=true";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Bearer", user.getAccessToken());
        headers.set("X-API-Key", API_KEY);

        HttpEntity<String> request = new HttpEntity<>(headers);
        RestTemplate template = new RestTemplate();
        ResponseEntity<String> response = null;
        try {
            response = template.exchange(url, HttpMethod.GET, request, String.class);
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
            throw new CustomException("Unable to connect to Bungie API while requesting linked profiles", 1);
        }

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new CustomException("Error linked profiles; status code: " + response.getStatusCodeValue(), 3);
        }
    }

    public boolean validateUserAccessToken(User user) {
        return Instant.now().getEpochSecond() < user.getAccessTokenExpiry();
    }

    public boolean validateUserRefreshToken(User user) {
        return Instant.now().getEpochSecond() < user.getRefreshTokenExpiry();
    }

    public User findUserAndValidateSessionToken(String sessionToken) throws CustomException {
        Optional<User> user = userService.findUserBySessionToken(sessionToken);
        if (!validateUserSessionToken(user)) {
            throw new CustomException("Invalid session token", 2);
        }
        return user.get();
    }

    public User getValidatedUserBySessionToken(String sessionToken) throws CustomException {
        User user = findUserAndValidateSessionToken(sessionToken);
        if (!validateUserAccessToken(user)) {
            if (validateUserRefreshToken(user)) {
                user = refreshUserAccessToken(user);
            } else {
                throw new CustomException("Refresh token expired.", 2);
            }
        }
        return user;
    }

    public User refreshUserAccessToken(User user) throws CustomException {
        String url = "https://www.bungie.net/platform/app/oauth/token/";

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(CLIENT_ID, CLIENT_SECRET);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String requestBody = "grant_type=refresh_token&refresh_token=" + user.getRefreshToken();

        HttpEntity<String> tokenRequest = new HttpEntity<>(requestBody, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> tokenResponse = restTemplate.postForEntity(url, tokenRequest, String.class);

        if (tokenResponse.getStatusCode() == HttpStatus.OK) {
            JSONObject authResponse = new JSONObject(tokenResponse.getBody());
            User newUser = userService.createOrUpdateUser(authResponse, false);
            return newUser;
        } else {
            throw new CustomException(String.format(
                    "Error refreshing linked profiles; bungieId: %s; status code: %d", user.getBungieId(),
                    tokenResponse.getStatusCodeValue()), 3);
        }
    }

    public String getProfile(String sessionToken, int membershipType, String profileId, User user)
            throws CustomException {
        String accessToken = user.getAccessToken();

        String url = "https://www.bungie.net/Platform/Destiny2/" + membershipType + "/Profile/" + profileId
                + "/?components=102,200,300,304,305,307";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.set("X-API-Key", API_KEY);

        HttpEntity<String> request = new HttpEntity<>(headers);
        RestTemplate template = new RestTemplate();
        ResponseEntity<String> response = template.exchange(url, HttpMethod.GET, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new CustomException(String.format("Error retrieving profile; profile id: %s; status code: %d",
                    profileId, response.getStatusCodeValue()));
        }
    }

    public String getCharacter(String sessionToken, int membershipType, String profileId, String characterId, User user)
            throws CustomException {

        String url = "https://www.bungie.net/Platform/Destiny2/" + membershipType + "/Profile/" + profileId
                + "/Character/" + characterId
                + "/?components=205,300,201,307,305,304";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + user.getAccessToken());
        headers.set("X-API-Key", API_KEY);

        HttpEntity<String> request = new HttpEntity<>(headers);
        RestTemplate template = new RestTemplate();
        ResponseEntity<String> response = null;
        try {
            response = template.exchange(url, HttpMethod.GET, request, String.class);
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
            throw new CustomException("Unable to connect to Bungie API while requesting character data", 1);
        }

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new CustomException(String.format("Error retrieving character %s; profile id: %s; status code: %d",
                    characterId, profileId, response.getStatusCodeValue()), 3);
        }
    }

    public ProfileDto getProfileDetailed(String sessionToken, String accountId, int membershipType, String profileId)
            throws CustomException {
        User user = null;
        if (sessionToken.equals(MASTER_SESSION_TOKEN)) {
            user = userService.findByBungieId(MASTER_ID).get();
            if (!validateUserAccessToken(user)) {
                user = refreshUserAccessToken(user);
            }
        } else {
            user = getValidatedUserBySessionToken(sessionToken);
            validateRequestBySessionToken(accountId, user);
        }
        try {
            Map<Long, InventoryItem> inventoryItemData = inventoryItemService.getAndMayUpdateInventoryItemsMap();

            String rawProfileData = getProfile(sessionToken, membershipType, profileId, user);
            JSONObject profileJson = (new JSONObject(rawProfileData)).getJSONObject("Response");
            ProfileDto profile = new ProfileDto();
            JSONObject characterInfoJson = profileJson.getJSONObject("characters")
                    .getJSONObject("data");

            for (String characterId : characterInfoJson.keySet()) {
                Long classType = characterInfoJson.getJSONObject(characterId)
                        .getLong("classHash");
                int light = characterInfoJson.getJSONObject(characterId)
                        .getInt("light");
                String curClassType = ClassType.getType(classType);
                CharacterDto character = createProfileCharacter(characterId, sessionToken, membershipType, profileId,
                        inventoryItemData, user);
                character.setCharacterId(characterId);
                character.setLight(light);
                character.setCharacterClass(curClassType);
                switch (curClassType) {
                    case "Hunter":
                        profile.setHunter(character);
                        break;
                    case "Warlock":
                        profile.setWarlock(character);
                        break;
                    case "Titan":
                        profile.setTitan(character);
                        break;
                }
            }

            JSONArray profileVault = profileJson.getJSONObject("profileInventory")
                    .getJSONObject("data")
                    .getJSONArray("items");
            JSONObject itemDetails = profileJson.getJSONObject("itemComponents");

            profile.setVault(createProfileVault(profileVault, itemDetails, inventoryItemData));

            return profile;
        } catch (HttpServerErrorException e) {
            throw new CustomException(
                    String.format("Failed to create detailed profile. Error: %s", e.getLocalizedMessage()), 6);
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
            throw new CustomException(
                    String.format("Failed to create detailed profile. Error: %s", e.getLocalizedMessage()), 4);
        }
    }

    private void validateRequestBySessionToken(String accountId, User user) throws CustomException {
        // Match user (retrieved from session token) with accountId
        if (!user.getBungieId().equals(accountId)) {
            throw new CustomException("Unauthorized: attempt to access account which does not belong to the user", 2);
        }
    }

    private VaultDto createProfileVault(JSONArray profileVault, JSONObject itemDetails,
            Map<Long, InventoryItem> inventoryItemData) throws CustomException {
        VaultDto vault = new VaultDto();
        try {
            for (int i = 0; i < profileVault.length(); i++) {
                JSONObject item = profileVault.getJSONObject(i);
                Long key = item.getLong("itemHash");
                InventoryItem inventoryItem = inventoryItemData.get(key);
                InventoryItemDto inventoryItemDto = createInventoryItemDtoForManager(item, inventoryItem, itemDetails,
                        false,
                        inventoryItemData);
                if (inventoryItemDto != null) {
                    vault.addToItemInventory(inventoryItemDto.getInventoryItemInstance().getInstanceItemId(),
                            inventoryItemDto);
                }
            }
        } catch (HttpServerErrorException e) {
            throw new CustomException(
                    String.format("Failed to create vault. Error: %s", e.getLocalizedMessage()), 6);
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
            throw new CustomException(String.format("Failed to create vault. Error: %s", e.getLocalizedMessage()), 4);
        }

        // vault.sortAll();
        return vault;
    }

    private CharacterDto createProfileCharacter(String characterId, String sessionToken, int membershipType,
            String profileId, Map<Long, InventoryItem> inventoryItemData, User user) throws CustomException {
        CharacterDto character = new CharacterDto();
        String rawCharacterData = getCharacter(sessionToken, membershipType, profileId, characterId, user);

        try {
            JSONObject characterJson = (new JSONObject(rawCharacterData)).getJSONObject("Response");
            JSONObject itemDetails = characterJson.getJSONObject("itemComponents");

            JSONArray characterEquipment = characterJson.getJSONObject("equipment")
                    .getJSONObject("data")
                    .getJSONArray("items");

            JSONArray characterInventory = characterJson.getJSONObject("inventory")
                    .getJSONObject("data")
                    .getJSONArray("items");

            for (int i = 0; i < characterEquipment.length(); i++) {
                JSONObject item = characterEquipment.getJSONObject(i);
                Long key = item.getLong("itemHash");
                InventoryItem inventoryItem = inventoryItemData.get(key);
                InventoryItemDto inventoryItemDto = createInventoryItemDtoForManager(item, inventoryItem, itemDetails,
                        true,
                        inventoryItemData);
                if (inventoryItemDto != null) {
                    character.setEquippedItem(inventoryItemDto);
                }
            }
            for (int i = 0; i < characterInventory.length(); i++) {
                JSONObject item = characterInventory.getJSONObject(i);
                Long key = item.getLong("itemHash");
                InventoryItem inventoryItem = inventoryItemData.get(key);
                if (item.getInt("location") == 4) {
                    continue;
                }
                InventoryItemDto inventoryItemDto = createInventoryItemDtoForManager(item, inventoryItem, itemDetails,
                        false,
                        inventoryItemData);
                if (inventoryItemDto != null) {
                    character.addToItemInventory(inventoryItemDto.getInventoryItemInstance().getInstanceItemId(),
                            inventoryItemDto);
                }
            }
            return character;
        } catch (HttpServerErrorException e) {
            throw new CustomException(
                    String.format("Failed to create character with id: %s. Error: %s", characterId,
                            e.getLocalizedMessage()),
                    6);
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
            throw new CustomException(
                    String.format("Failed to create character with id: %s. Error: %s", characterId,
                            e.getLocalizedMessage()),
                    4);
        }
    }

    private InventoryItemDto createInventoryItemDtoForManager(JSONObject item, InventoryItem inventoryItem,
            JSONObject itemDetails, boolean forEquipped, Map<Long, InventoryItem> inventoryItemData)
            throws CustomException {
        if (inventoryItem == null) {
            return null;
        }
        try {
            InventoryItemDto inventoryItemDto = new InventoryItemDto(
                    inventoryItem.getName(),
                    inventoryItem.getIconUrl(),
                    inventoryItem.getHashVal().toString());
            inventoryItemDto.setItemType(inventoryItem.getItemType());
            inventoryItemDto.setItemSubType(inventoryItem.getItemSubType());
            if (!item.has("itemInstanceId") || inventoryItemDto.getItemType() == null) {
                return null;
            }
            String instanceItemId = item.getString("itemInstanceId");
            InventoryItemInstanceDto inventoryItemInstanceDto = new InventoryItemInstanceDto();

            // Basic info
            JSONObject itemInstanceBasicInfo = itemDetails.getJSONObject("instances")
                    .getJSONObject("data")
                    .getJSONObject(instanceItemId);
            if (item.has("location") && itemInstanceBasicInfo.has("canEquip")
                    && itemInstanceBasicInfo.has("cannotEquipReason")) {
                if (item.getInt("location") == 4
                        || (item.getInt("location") == 1 && itemInstanceBasicInfo.getInt("cannotEquipReason") == 16
                                && !itemInstanceBasicInfo.getBoolean("canEquip"))) {
                    return null;
                }
            }
            if (inventoryItemDto.getItemType() == "EMBLEM") {
                if (forEquipped) {
                    return inventoryItemDto;
                }
                return null;
            }
            if (inventoryItem.getDamageType() != null) {
                inventoryItemDto.setDamageType(inventoryItem.getDamageType());
            }
            inventoryItemInstanceDto.setInstanceItemId(instanceItemId);
            if (itemInstanceBasicInfo.has("primaryStat")) {
                inventoryItemInstanceDto
                        .setLight(itemInstanceBasicInfo.getJSONObject("primaryStat").getInt("value"));
            }

            // Perks
            JSONArray itemInstancePerks = itemDetails.getJSONObject("sockets")
                    .getJSONObject("data")
                    .getJSONObject(instanceItemId)
                    .getJSONArray("sockets");
            for (int i = 0; i < 5; i++) { // First 5 perks are relevant
                if (itemInstancePerks.length() - 1 < i) {
                    break;
                }
                if (itemInstancePerks.getJSONObject(i).has("plugHash")) {
                    long perkHashVal = itemInstancePerks.getJSONObject(i).getLong("plugHash");
                    InventoryItem perkData = inventoryItemData.get(perkHashVal);
                    if (perkData != null) {
                        InventoryItemDto perkDto = new InventoryItemDto(
                                perkData.getName(),
                                perkData.getIconUrl(),
                                Long.toString(perkHashVal));
                        inventoryItemInstanceDto.addPerk(perkDto);
                    }
                }
            }

            // Stats
            JSONObject itemInstanceStats = itemDetails.getJSONObject("stats")
                    .getJSONObject("data")
                    .getJSONObject(instanceItemId)
                    .getJSONObject("stats");
            for (String statHash : itemInstanceStats.keySet()) {
                long statHashVal = Long.parseLong(statHash);
                int statValue = itemInstanceStats.getJSONObject(statHash).getInt("value");

                StatDto stat = new StatDto();
                stat.setStatType(statHashVal);
                stat.setStatValue(statValue);
                inventoryItemInstanceDto.addStat(stat);
            }

            inventoryItemDto.setInventoryItemInstance(inventoryItemInstanceDto);
            return inventoryItemDto;

        } catch (HttpServerErrorException e) {
            throw new CustomException(
                    String.format("Failed to create item: %s. Error: %s", item.toString(), e.getLocalizedMessage()), 6);
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
            throw new CustomException(
                    String.format("Failed to create item: %s. Error: %s", item.toString(), e.getLocalizedMessage()), 4);
        }
    }

    public void transferItemWithValidation(String sessionToken, String accountId, int membershipType, String profileId,
            TransferDto transferDto) throws CustomException {
        User user = getValidatedUserBySessionToken(sessionToken);
        validateRequestBySessionToken(accountId, user);
        validateTransfer(transferDto);

        // sourceCharacterId is used as generic characterId by tranferItem - Ex:
        // transfers from vault to character use only sourceCharacterId + toVault=true
        if (!transferDto.getSourceCharacterId().equals("0") && !transferDto.getTargetCharacterId().equals("0")) {
            TransferDto intermediateTransfer = new TransferDto();
            intermediateTransfer.setInventoryItem(transferDto.getInventoryItem());
            intermediateTransfer.setInventoryItemInstance(transferDto.getInventoryItemInstance());
            intermediateTransfer.setToVault(true);
            intermediateTransfer.setSourceCharacterId(transferDto.getSourceCharacterId());
            transferItem(user, accountId, membershipType, profileId, intermediateTransfer);

            transferDto.setSourceCharacterId(transferDto.getTargetCharacterId());
            transferItem(user, accountId, membershipType, profileId, transferDto);
        } else if (transferDto.getSourceCharacterId().equals("0")) {
            transferDto.setSourceCharacterId(transferDto.getTargetCharacterId());
            transferItem(user, accountId, membershipType, profileId, transferDto);
        } else {
            transferItem(user, accountId, membershipType, profileId, transferDto);
        }
    }

    public void transferItem(User user, String accountId, int membershipType, String profileId,
            TransferDto transferDto) throws CustomException {
        String url = "https://www.bungie.net/Platform/Destiny2/Actions/Items/TransferItem/";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + user.getAccessToken());
        headers.set("X-API-Key", API_KEY);

        String requestBody = String.format(
                "{\"membershipType\":%d, \"itemReferenceHash\":%s, \"stackSize\":1, \"transferToVault\":%b, \"itemId\":%s, \"characterId\":%s}",
                membershipType, transferDto.getInventoryItem().getHashVal(),
                transferDto.getToVault() ? transferDto.getToVault() : false,
                transferDto.getInventoryItemInstance().getInstanceItemId(), transferDto.getSourceCharacterId());

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
        RestTemplate template = new RestTemplate();
        try {
            ResponseEntity<String> response = template.exchange(url, HttpMethod.POST, request, String.class);

            if (response.getStatusCode() != HttpStatus.OK) {
                JSONObject responseJson = new JSONObject(response.getBody());
                throw new CustomException(responseJson.getString("Message"));
            }
        } catch (HttpServerErrorException e) {
            throw new CustomException(
                    String.format("Failed to transfer item %s for profile %s. Error: %s",
                            transferDto.getInventoryItemInstance().getInstanceItemId(), profileId,
                            e.getLocalizedMessage()),
                    6);
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
            String jsonString = e.getLocalizedMessage();

            // Extract the JSON part from the string
            int startIndex = jsonString.indexOf('{');
            int endIndex = jsonString.lastIndexOf('}');
            Integer errorCode = null;
            String message = null;
            if (startIndex != -1 && endIndex != -1) {
                String error = jsonString.substring(startIndex, endIndex + 1);
                JSONObject jsonErrorData = new JSONObject(error);

                // Extract the ErrorCode value
                errorCode = jsonErrorData.getInt("ErrorCode");
                message = jsonErrorData.getString("Message");
            }
            throw new CustomException(
                    String.format("Failed to transfer item %s for profile %s. Error: %s",
                            transferDto.getInventoryItemInstance().getInstanceItemId(), profileId,
                            message != null ? message : e.getLocalizedMessage()),
                    errorCode != null ? errorCode : 1);
        }
    }

    private void validateTransfer(TransferDto transferDto) throws CustomException {
        if (transferDto.getInventoryItemInstance().getInstanceItemId() == null) {
            throw new CustomException(
                    String.format("BadRequest: must provide item instance id"), 5);
        }
        if (transferDto.getInventoryItem().getHashVal() == null) {
            throw new CustomException(
                    String.format("BadRequest: must provide item hash value"), 5);
        }
        if (transferDto.getSourceCharacterId() == null) {
            throw new CustomException(
                    String.format("BadRequest: must provide source characterId"), 5);
        }
        if (transferDto.getTargetCharacterId() == null) {
            throw new CustomException(
                    String.format("BadRequest: must provide target characterId"), 5);
        }
    }

    public void equipItemWithValidation(String sessionToken, String accountId, int membershipType, String profileId,
            TransferDto transferDto) throws CustomException {

        User user = getValidatedUserBySessionToken(sessionToken);
        validateRequestBySessionToken(accountId, user);
        validateTransfer(transferDto);
        if (transferDto.getSourceCharacterId().equals(transferDto.getTargetCharacterId())) {
            equipItem(user, sessionToken, accountId, membershipType, profileId, transferDto);
        } else if (transferDto.getSourceCharacterId().equals("0")) {
            TransferDto intermediateTransfer = new TransferDto();
            intermediateTransfer.setInventoryItemInstance(transferDto.getInventoryItemInstance());
            intermediateTransfer.setInventoryItem(transferDto.getInventoryItem());
            intermediateTransfer.setSourceCharacterId(transferDto.getTargetCharacterId());
            intermediateTransfer.setToVault(false);
            transferItem(user, accountId, membershipType, profileId, intermediateTransfer);

            transferDto.setSourceCharacterId(transferDto.getTargetCharacterId());
            equipItem(user, sessionToken, accountId, membershipType, profileId, transferDto);
        } else {
            TransferDto intermediateTransfer = new TransferDto();
            intermediateTransfer.setInventoryItemInstance(transferDto.getInventoryItemInstance());
            intermediateTransfer.setInventoryItem(transferDto.getInventoryItem());
            intermediateTransfer.setToVault(true);
            intermediateTransfer.setSourceCharacterId(transferDto.getSourceCharacterId());
            transferItem(user, accountId, membershipType, profileId, intermediateTransfer);

            intermediateTransfer.setSourceCharacterId(transferDto.getTargetCharacterId());
            intermediateTransfer.setToVault(false);
            transferItem(user, accountId, membershipType, profileId, intermediateTransfer);

            transferDto.setSourceCharacterId(transferDto.getTargetCharacterId());
            equipItem(user, sessionToken, accountId, membershipType, profileId, transferDto);
        }
    }

    public void equipItem(User user, String sessionToken, String accountId, int membershipType, String profileId,
            TransferDto transferDto) throws CustomException {
        String url = "https://www.bungie.net/Platform/Destiny2/Actions/Items/EquipItem/";
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-Key", API_KEY);
        headers.set("Authorization", "Bearer " + user.getAccessToken());

        String requestBody = String.format(
                "{\"membershipType\":%d, \"itemId\":%s, \"characterId\":%s}",
                membershipType,
                transferDto.getInventoryItemInstance().getInstanceItemId(),
                transferDto.getSourceCharacterId());

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
        RestTemplate template = new RestTemplate();
        try {
            ResponseEntity<String> response = template.exchange(url, HttpMethod.POST, request, String.class);

            if (response.getStatusCode() != HttpStatus.OK) {
                JSONObject responseJson = new JSONObject(response.getBody());
                throw new CustomException(responseJson.getString("Message"));
            }
        } catch (HttpServerErrorException e) {
            throw new CustomException(
                    String.format("Failed to equip item %s for profile %s. Error: %s",
                            transferDto.getInventoryItemInstance().getInstanceItemId(), profileId,
                            e.getLocalizedMessage()),
                    6);
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
            String jsonString = e.getLocalizedMessage();

            // Extract the JSON part from the string
            int startIndex = jsonString.indexOf('{');
            int endIndex = jsonString.lastIndexOf('}');
            Integer errorCode = null;
            String message = null;
            if (startIndex != -1 && endIndex != -1) {
                String error = jsonString.substring(startIndex, endIndex + 1);
                JSONObject jsonErrorData = new JSONObject(error);

                // Extract the ErrorCode value
                errorCode = jsonErrorData.getInt("ErrorCode");
                message = jsonErrorData.getString("Message");
            }
            throw new CustomException(
                    String.format("Failed to equip item %s for profile %s. Error: %s",
                            transferDto.getInventoryItemInstance().getInstanceItemId(), profileId,
                            message != null ? message : e.getLocalizedMessage()),
                    errorCode != null ? errorCode : 1);
        }
    }

    private String getMilestoneJsonData() throws CustomException {
        String url = "https://www.bungie.net/Platform/Destiny2/Milestones/";
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-Key", API_KEY);

        HttpEntity<String> request = new HttpEntity<>(headers);
        RestTemplate template = new RestTemplate();
        ResponseEntity<String> response = null;
        try {
            response = template.exchange(url, HttpMethod.GET, request, String.class);
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
            throw new CustomException("Unable to connect to Bungie API while requesting character data", 1);
        }

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new CustomException(String.format("Error retrieving milestone rotation data", 3));
        }
    }

    public RotationDto getMilestoneRotationData() throws CustomException {
        JSONObject milestoneJson = (new JSONObject(getMilestoneJsonData())).getJSONObject("Response");
        JSONObject activityManifestJson = inventoryItemService.getActivityManifest();
        RotationDto rotation = new RotationDto();

        // Exotic mission milestone hash in the manifest changes weekly. Try all
        // possibilities
        String[] exoticMissionHashes = { "1027301269", "4244749316", "3557475774" };
        MilestoneDto exoticMissionMilestone = null;
        for (String hash : exoticMissionHashes) {
            exoticMissionMilestone = getExoticMissionRotationData(milestoneJson, activityManifestJson, hash);
            if (exoticMissionMilestone != null) {
                rotation.addMilestone("weekly exotic mission", exoticMissionMilestone);
                break;
            }
        }
        if (exoticMissionMilestone == null) {
            System.out.println("Failed to create exotic mission milestone.");
            throw new CustomException(
                    String.format("Failed to create exotic mission milestone."), 4);
        }
        rotation.addMilestone("weekly raid", getRaidRotationData(milestoneJson, activityManifestJson));
        rotation.addMilestone("weekly dungeon", getDungeonRotationData(milestoneJson, activityManifestJson));
        rotation.addMilestone("weekly nightfall", getNightfallRotationData(milestoneJson, activityManifestJson));
        rotation.addMilestone("weekly partition", getPartitionRotationData(milestoneJson, activityManifestJson));

        return rotation;
    }

    private MilestoneDto getPartitionRotationData(JSONObject milestoneJson, JSONObject activityManifestJson)
            throws CustomException {
        try {
            String partitionHash = "3446833552";
            Long partitionActivity = milestoneJson.getJSONObject(partitionHash)
                    .getJSONArray("activities")
                    .getJSONObject(0)
                    .getLong("activityHash");

            JSONObject activity = activityManifestJson.getJSONObject(Long.toString(partitionActivity));
            String description = activity.getJSONObject("originalDisplayProperties").getString("description");
            String name = activity.getJSONObject("originalDisplayProperties").getString("name");
            String iconUrl = activity.getString("pgcrImage");
            return new MilestoneDto(name, iconUrl, description);
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
            throw new CustomException(
                    String.format("Failed to create partition milestone. Error: %s", e.getLocalizedMessage()), 4);
        }
    }

    private MilestoneDto getNightfallRotationData(JSONObject milestoneJson, JSONObject activityManifestJson)
            throws CustomException {
        try {
            String nightfallHash = "2029743966";
            Long nightfallActivityHash = milestoneJson.getJSONObject(nightfallHash)
                    .getJSONArray("activities")
                    .getJSONObject(0)
                    .getLong("activityHash");

            JSONObject activity = activityManifestJson.getJSONObject(Long.toString(nightfallActivityHash));
            String description = activity.getJSONObject("originalDisplayProperties").getString("description");
            String name = activity.getJSONObject("originalDisplayProperties").getString("name");
            String iconUrl = activity.getString("pgcrImage");
            return new MilestoneDto(name, iconUrl, description);
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
            throw new CustomException(
                    String.format("Failed to create nightfall milestone. Error: %s", e.getLocalizedMessage()), 4);
        }
    }

    private MilestoneDto getExoticMissionRotationData(JSONObject milestoneJson, JSONObject activityManifestJson,
            String exoticMissionHash)
            throws CustomException {
        try {
            Long exoticMissionActivityHash = milestoneJson.getJSONObject(exoticMissionHash)
                    .getJSONArray("activities")
                    .getJSONObject(0)
                    .getLong("activityHash");

            JSONObject activity = activityManifestJson.getJSONObject(Long.toString(exoticMissionActivityHash));
            String description = activity.getJSONObject("originalDisplayProperties").getString("description");
            String name = activity.getJSONObject("originalDisplayProperties").getString("name");
            String iconUrl = activity.getString("pgcrImage");

            MilestoneDto milestone = new MilestoneDto(name, iconUrl, description);
            List<InventoryItem> potentialRewards = inventoryItemService.getAndMayUpdateCollectiblesMap()
                    .get(exoticMissionActivityHash);
            milestone.setRewards(createMilestoneRewards(potentialRewards));

            return milestone;
        } catch (Exception e) {
            return null;
        }
    }

    private MilestoneDto getDungeonRotationData(JSONObject milestoneJson, JSONObject activityManifestJson)
            throws CustomException {
        try {
            for (Long dungeonHash : DungeonHash.dungeonMap.keySet()) {
                String dungeonHashStr = Long.toString(dungeonHash);
                if (milestoneJson.has(dungeonHashStr)) {
                    JSONObject dungeonMilestone = milestoneJson.getJSONObject(dungeonHashStr);
                    if (dungeonMilestone.has("activities")) {
                        JSONObject activity = dungeonMilestone.getJSONArray("activities")
                                .getJSONObject(0);
                        JSONArray objectives = activity.getJSONArray("challengeObjectiveHashes");

                        Integer numChallenges = objectives.length();
                        if (numChallenges > 0) {
                            JSONObject dungeonDetails = activityManifestJson
                                    .getJSONObject(Long.toString(activity.getLong("activityHash")));
                            String name = dungeonDetails.getJSONObject("originalDisplayProperties").getString("name");
                            String iconUrl = dungeonDetails.getString("pgcrImage");
                            String description = dungeonDetails.getJSONObject("originalDisplayProperties")
                                    .getString("description");

                            MilestoneDto milestone = new MilestoneDto(name, iconUrl, description);
                            List<InventoryItem> potentialRewards = inventoryItemService.getAndMayUpdateCollectiblesMap()
                                    .get(dungeonHash);
                            milestone.setRewards(createMilestoneRewards(potentialRewards));

                            return milestone;
                        }
                    }
                }
            }
            throw new CustomException("Failed to find information pertaining to the weekly dungeon", 4);
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
            throw new CustomException(
                    String.format("Failed to create exotic mission milestone. Error: %s", e.getLocalizedMessage()), 4);
        }
    }

    private MilestoneDto getRaidRotationData(JSONObject milestoneJson, JSONObject activityManifestJson)
            throws CustomException {
        try {
            for (Long raidHash : RaidHash.raidMap.keySet()) {
                String raidHashStr = Long.toString(raidHash);
                if (milestoneJson.has(raidHashStr)) {
                    JSONObject activity = milestoneJson.getJSONObject(raidHashStr)
                            .getJSONArray("activities")
                            .getJSONObject(0);
                    JSONObject booleanActivityOptions = activity.getJSONObject("booleanActivityOptions");
                    Integer numOptions = booleanActivityOptions.length();
                    if (numOptions > 2) {
                        JSONObject raidDetails = activityManifestJson
                                .getJSONObject(Long.toString(activity.getLong("activityHash")));
                        String name = raidDetails.getJSONObject("originalDisplayProperties").getString("name");
                        String iconUrl = raidDetails.getString("pgcrImage");
                        String description = raidDetails.getJSONObject("originalDisplayProperties")
                                .getString("description");

                        MilestoneDto milestone = new MilestoneDto(name, iconUrl, description);
                        List<InventoryItem> potentialRewards = inventoryItemService.getAndMayUpdateCollectiblesMap()
                                .get(raidHash);
                        milestone.setRewards(createMilestoneRewards(potentialRewards));

                        return milestone;
                    }
                }
            }
            throw new CustomException("Failed to find information pertaining to the weekly raid", 4);
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
            throw new CustomException(
                    String.format("Failed to create exotic mission milestone. Error: %s", e.getLocalizedMessage()), 4);
        }
    }

    private List<InventoryItemDto> createMilestoneRewards(List<InventoryItem> potentialRewards) {
        Set<String> addedRewards = new HashSet<>(); // Collectibles manifest from API contains
        List<InventoryItemDto> rewards = new ArrayList<>();

        for (InventoryItem reward : potentialRewards) {
            if (reward != null && !addedRewards.contains(reward.getName()) && reward.getItemType() != 4274335291L) {
                addedRewards.add(reward.getName());
                InventoryItemDto inventoryItemDto = new InventoryItemDto(
                        reward.getName(),
                        reward.getIconUrl(),
                        reward.getHashVal().toString());
                inventoryItemDto.setItemType(reward.getItemType());
                inventoryItemDto.setItemSubType(reward.getItemSubType());
                if (reward.getDamageType() != null) {
                    inventoryItemDto.setDamageType(reward.getDamageType());
                }
                rewards.add(inventoryItemDto);
            }
        }

        return rewards;
    }
}
