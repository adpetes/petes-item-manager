package com.petesitemmanager.pim.service;

import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

import com.petesitemmanager.pim.domain.User;
import com.petesitemmanager.pim.exception.CustomException;
import com.petesitemmanager.pim.repository.UserRepository;

@Service
@Transactional
public class UserService {
    private final Logger log = LoggerFactory.getLogger(User.class);

    @Value("${user.master-id}")
    private String MASTER_ID;

    @Value("${user.master-token}")
    private String MASTER_SESSION_TOKEN;

    @Autowired
    private UserRepository userRepository;

    public User save(User user) {
        log.debug("Request to save User : {}", user);
        User userSaved = userRepository.save(user);
        return userSaved;
    }

    @Transactional(readOnly = true)
    public Optional<User> findOne(Long id) {
        log.debug("Request to get User by id : {}", id);
        return userRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<User> findByBungieId(String bungieId) {
        log.debug("Request to get User by bungieId : {}", bungieId);
        return userRepository.findByBungieId(bungieId);
    }

    @Transactional(readOnly = true)
    public Optional<User> findUserBySessionToken(String sessionToken) {
        log.debug("Request to get User by sessionToken : {}", sessionToken);
        return userRepository.findBySessionToken(sessionToken);
    }

    public static String generateSessionToken() {
        SecureRandom random = new SecureRandom();
        byte[] tokenBytes = new byte[128];
        random.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }

    public User createOrUpdateUser(JSONObject authResponse, boolean updateSessionToken) throws CustomException {
        // Read API response containing accessToken and more
        String accessToken = authResponse.getString("access_token");
        int expiresIn = authResponse.getInt("expires_in");
        String refreshToken = authResponse.getString("refresh_token");
        int refreshExpiresIn = authResponse.getInt("refresh_expires_in");
        String bungieId = authResponse.getString("membership_id");

        // If user is in DB, update with new token information, otherwise create new
        Optional<User> potentialUser = findByBungieId(bungieId);
        User user = potentialUser.isPresent() ? potentialUser.get() : new User();
        Instant now = Instant.now();

        if (updateSessionToken) {
            String sessionToken = generateSessionToken();
            user.setSessionToken(sessionToken);
            user.setSessionTokenExpiry(now.getEpochSecond() + 604800L); // 1 week
        }

        user.setBungieId(bungieId);
        user.setAccessToken(accessToken);
        user.setRefreshToken(refreshToken);
        user.setAccessTokenExpiry(now.getEpochSecond() + expiresIn);
        user.setRefreshTokenExpiry(now.getEpochSecond() + refreshExpiresIn);

        if (bungieId.equals(MASTER_ID)) {
            user.setIsMaster(true);
            user.setSessionToken(MASTER_SESSION_TOKEN);
            user.setSessionTokenExpiry(now.getEpochSecond() + 6048000L); // 100 week
        }
        try {
            return save(user);
        } catch (Exception e) {
            throw new CustomException("Failed to save user with id: " + user.getBungieId());
        }
    }

    public User wipeUserTokenInfo(User user) throws CustomException {
        user.setAccessToken(null);
        user.setRefreshToken(null);
        user.setSessionToken(null);
        user.setAccessTokenExpiry(null);
        user.setRefreshTokenExpiry(null);
        user.setSessionTokenExpiry(null);
        try {
            return save(user);
        } catch (Exception e) {
            throw new CustomException("Failed to save user with id: " + user.getBungieId());
        }
    }

}
