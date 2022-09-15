package com.katz.organizationservice.model;

public class OrganisationEventEmitter {
    private String classTypeName;
    private String correlationId;
    private String organizationId;
    private String action;

    public OrganisationEventEmitter() {
    }

    public OrganisationEventEmitter(String classTypeName, String correlationId, String organizationId, String action) {
        this.classTypeName = classTypeName;
        this.correlationId = correlationId;
        this.organizationId = organizationId;
        this.action = action;
    }

    public String getClassTypeName() {
        return classTypeName;
    }

    public void setClassTypeName(String classTypeName) {
        this.classTypeName = classTypeName;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return "OrganisationEvenEmitter{" +
                "classTypeName='" + classTypeName + '\'' +
                ", correlationId='" + correlationId + '\'' +
                ", organizationId='" + organizationId + '\'' +
                ", action='" + action + '\'' +
                '}';
    }
}
