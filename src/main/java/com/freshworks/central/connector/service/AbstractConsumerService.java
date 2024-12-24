package com.freshworks.central.connector.service;

import com.freshworks.core.shared.consumer.ConsumerService;
import com.freshworks.central.connector.request.CentralConnectorRequest;
import com.freshworks.central.hagrid.assets.*;
import java.util.List;
import com.freshworks.freshindex.index.query.Expression;

public abstract class AbstractConsumerService {


        public abstract List<Central> consumeCentralAsset(CentralConnectorRequest connectorRequest , ConnectorHagridConfiguration connectorHagridConfiguration) throws Exception;


         public abstract List<Central> consumeCentralAssetByFilter(CentralConnectorRequest connectorRequest , ConnectorHagridConfiguration connectorHagridConfiguration , Expression expression) throws Exception;


         public abstract List<Central> consumeCentralAssetStream(CentralConnectorRequest connectorRequest , ConnectorHagridConfiguration connectorHagridConfiguration , int startToken , int endToken) throws Exception;


}
