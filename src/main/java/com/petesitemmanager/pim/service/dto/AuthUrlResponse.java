package com.petesitemmanager.pim.service.dto;

public class AuthUrlResponse {
    private String authUrl;
    private String reauthUrl;

    public AuthUrlResponse(String authUrl, String reauthUrl) {
        this.authUrl = authUrl;
        this.reauthUrl = reauthUrl;
    }

    public String getAuthUrl() {
        return authUrl;
    }

    public void setAuthUrl(String authUrl) {
        this.authUrl = authUrl;
    }

    public String getReauthUrl() {
        return reauthUrl;
    }

    public void setReauthUrl(String reauthUrl) {
        this.reauthUrl = reauthUrl;
    }
}
