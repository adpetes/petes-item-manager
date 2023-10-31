package com.petesitemmanager.pim.rest;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.Cookie.SameSite;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
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
@RestController
public class BungieResource {

    @Autowired
    private BungieService bungieService;

    /* Bungie API authorization URL */
    @GetMapping("/bungie-authorize-url")
    public ResponseEntity<?> getAuthorizationUrl() throws CustomException {
        try {
            System.out.println("we got to the auth url.");
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
            System.out.println("we got to the callback");
            String token = bungieService.processAuthorization(authCode);

            response.sendRedirect("http://pimfr.s3-website.us-east-2.amazonaws.com/?sessionId=" + token);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
            return ResponseEntity.internalServerError().body(e);
        }
    }

    /*
     * Session token on client-side is used validate user - if invalid, user must
     * reauthenticate
     */
    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@RequestHeader("Authorization") String sessionToken) {
        try {
            if (sessionToken != null) {
                bungieService.findUserAndValidateSessionToken(sessionToken);
                // response.redi
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
    public ResponseEntity<?> getLinkedProfiles(@RequestHeader("Authorization") String sessionToken) {
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

    /* Detailed User profile */
    @GetMapping("/profile-detailed/{profileId}/{membershipType}")
    public ResponseEntity<?> getProfileDataDetailed(
            @RequestHeader("Authorization") String sessionToken,
            @RequestHeader("Account-Id") String accountId,
            @PathVariable("profileId") String profileId,
            @PathVariable("membershipType") int membershipType,
            HttpSession session,
            HttpServletResponse response) {
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