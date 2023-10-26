package com.petesitemmanager.pim.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class User {
    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false)
    private String bungieId;

    @Column(unique = true)
    private String sessionToken;

    @Column(nullable = true)
    private Long sessionTokenExpiry;
    // CREATE UNIQUE INDEX idx_unique_access_token ON user (access_token(255));
    // CREATE UNIQUE INDEX idx_unique_refresh_token ON user (refresh_token(255));

    @Column(unique = true, columnDefinition = "VARCHAR(512)")
    private String accessToken;

    @Column(nullable = true)
    private Long accessTokenExpiry;

    @Column(unique = true, columnDefinition = "VARCHAR(512)")
    private String refreshToken;

    @Column(nullable = true)
    private Long refreshTokenExpiry;

    private Boolean isMaster = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBungieId() {
        return bungieId;
    }

    public void setBungieId(String bungieId) {
        this.bungieId = bungieId;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Long getAccessTokenExpiry() {
        return accessTokenExpiry;
    }

    public void setAccessTokenExpiry(Long accessTokenExpiry) {
        this.accessTokenExpiry = accessTokenExpiry;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Long getRefreshTokenExpiry() {
        return refreshTokenExpiry;
    }

    public void setRefreshTokenExpiry(Long refreshTokenExpiry) {
        this.refreshTokenExpiry = refreshTokenExpiry;
    }

    public Long getSessionTokenExpiry() {
        return sessionTokenExpiry;
    }

    public void setSessionTokenExpiry(Long sessionTokenExpiry) {
        this.sessionTokenExpiry = sessionTokenExpiry;
    }

    public Boolean getIsMaster() {
        return isMaster;
    }

    public void setIsMaster(Boolean isMaster) {
        this.isMaster = isMaster;
    }
}
