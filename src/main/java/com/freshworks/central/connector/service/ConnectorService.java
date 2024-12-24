package com.freshworks.central.connector.service;

import com.freshworks.central.connector.request.CentralConnectorRequest;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

public interface ConnectorService {

    ImmutableMap<String , String> filterAndCreateBaggageMap(Map<String,String> inputData);

    Map<String , Object> consume(CentralConnectorRequest connectorRequest , ConnectorHagridConfiguration connectorHagridConfiguration);



    void startSync(ImmutableMap<String,String> map , CentralConnectorRequest connectorRequest , ConnectorHagridConfiguration connectorHagridConfiguration);

    void clearData(ConnectorHagridConfiguration connectorHagridConfiguration);

    boolean isSyncComplete(ConnectorHagridConfiguration connectorHagridConfiguration);
}
