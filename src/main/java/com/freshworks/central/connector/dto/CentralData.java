package com.freshworks.central.connector.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bson.json.JsonObject;

import java.util.UUID;

@Getter
@Setter
@Builder
public class CentralData {

    String payload_type;
    JsonObject payload;
    String account_id;
    String organisation_id;
    String pod;
    String region;
    String payloadVersion;
    String requestId;

}
