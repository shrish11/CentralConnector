package com.freshworks.central.connector.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CentralConnectorConnDetails {

    String centralUrl;
    String centralTopic;
    String authHeader;
}
