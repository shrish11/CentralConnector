package com.freshworks.central.connector.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Date;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true )
public class CommonConnectorRequest {

    @NonNull
    String requestId;
    Long accountId;
    Date requestedAt = new Date();

    Map<String, Object> workflowInput;

    Map<String,Object> inputRequiredFrom;


}
