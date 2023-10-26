package com.petesitemmanager.pim.rest;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.petesitemmanager.pim.exception.CustomException;
import com.petesitemmanager.pim.service.BungieService;
import com.petesitemmanager.pim.service.dto.AuthUrlResponse;
import com.petesitemmanager.pim.service.dto.ProfileDto;
import com.petesitemmanager.pim.service.dto.TransferDto;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class BungieResource {

    @Autowired
    private BungieService bungieService;

    /* Bungie API authorization URL */
    @GetMapping("/bungie-authorize-url")
    public ResponseEntity<?> getAuthorizationUrl() throws CustomException {
        try {
            String authUrl = bungieService.getAuthorizationUrl(false);
            String reauthUrl = bungieService.getAuthorizationUrl(true);
            AuthUrlResponse response = new AuthUrlResponse(authUrl, reauthUrl);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e);
        }
    }

    /*
     * Bungie API authorization callback endpoint - return a session token upon
     * completion
     */
    @GetMapping("/oauth2redirect")
    public ResponseEntity<?> callbackFromOauth(
            @RequestParam("code") String authCode,
            HttpSession session,
            HttpServletResponse response) {
        try {
            String token = bungieService.processAuthorization(authCode, session);

            Cookie cookie = new Cookie("sessionToken", token);
            cookie.setMaxAge(604800); // 1 Week
            cookie.setSecure(true);
            cookie.setPath("/");
            cookie.setDomain("localhost");

            response.addCookie(cookie);
            response.sendRedirect("http://localhost:3000"); // TODO change to website url

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e);
        }
    }

    /*
     * Session token on client-side is used validate user - if invalid, user must
     * reauthenticate
     */
    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@RequestHeader("Authorization") String sessionToken,
            HttpServletRequest request) {
        try {
            if (sessionToken != null) {
                bungieService.findUserAndValidateSessionToken(sessionToken);
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Session token not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e);
        }
    }

    /* Delete all token information */
    @PostMapping("/signout")
    public ResponseEntity<?> signOut(@RequestHeader("Authorization") String sessionToken) {
        try {
            bungieService.deleteUserTokenInfo(sessionToken);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(e);
        }
    }

    /* User linked profiles */
    @GetMapping("/linked-profiles")
    public ResponseEntity<?> getLinkedProfiles(@RequestHeader("Authorization") String sessionToken,
            HttpServletRequest request) {
        try {
            if (sessionToken != null) {
                String linkedProfiles = bungieService.getLinkedProfiles(sessionToken);
                return ResponseEntity.ok().body(linkedProfiles);

            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Session token not found");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(e);
        }
    }

    /* User profile */
    @GetMapping("/profile/{profileId}/{membershipType}")
    public ResponseEntity<?> getProfileData(@RequestHeader("Authorization") String sessionToken,
            @PathVariable("profileId") String profileId, @PathVariable("membershipType") int membershipType) {
        try {
            if (sessionToken != null) {
                String profile = bungieService.getProfile(sessionToken, membershipType, profileId);
                return ResponseEntity.ok().body(profile);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Session token not found");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(e);
        }
    }

    /* Detailed User profile */
    @GetMapping("/profile-detailed/{profileId}/{membershipType}")
    public ResponseEntity<?> getProfileDataDetailed(
            @RequestHeader("Authorization") String sessionToken,
            @RequestHeader("Account-Id") String accountId,
            @PathVariable("profileId") String profileId,
            @PathVariable("membershipType") int membershipType) {
        try {
            ProfileDto profile = bungieService.getProfileDetailed(sessionToken, accountId, membershipType,
                    profileId);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/json");
            return ResponseEntity.ok().headers(headers).body(profile);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(e);
        }
    }

    /* Transfer inventory item */
    @PostMapping("/transfer-item/{profileId}/{membershipType}")
    public ResponseEntity<?> transferItem(
            @RequestHeader("Authorization") String sessionToken,
            @RequestHeader("Account-Id") String accountId,
            @RequestHeader("Demo") Boolean isDemo,
            @PathVariable("profileId") String profileId,
            @PathVariable("membershipType") int membershipType,
            @RequestBody TransferDto transferDto) {
        try {
            if (!isDemo) {
                bungieService.transferItemWithValidation(sessionToken, accountId, membershipType, profileId,
                        transferDto);
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(e);
        }
    }

    /* Equip inventory item */
    @PostMapping("/equip-item/{profileId}/{membershipType}")
    public ResponseEntity<?> equipItem(
            @RequestHeader("Authorization") String sessionToken,
            @RequestHeader("Account-Id") String accountId,
            @RequestHeader("Demo") Boolean isDemo,
            @PathVariable("profileId") String profileId,
            @PathVariable("membershipType") int membershipType,
            @RequestBody TransferDto transferDto) {
        try {
            if (!isDemo) {
                bungieService.equipItemWithValidation(sessionToken, accountId, membershipType, profileId,
                        transferDto);
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(e);
        }
    }
}