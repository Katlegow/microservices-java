package com.katz.licensingservice.model;

public class UserContext {
    private String correlationId;
    private String authToken;
    private String userId;
    private String orgId;

    public UserContext(String correlationId, String authToken, String userId, String orgId) {
        this.correlationId = correlationId;
        this.authToken = authToken;
        this.userId = userId;
        this.orgId = orgId;
    }

    public UserContext(String correlationId) {
        this(correlationId, null, null, null);
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }
}
