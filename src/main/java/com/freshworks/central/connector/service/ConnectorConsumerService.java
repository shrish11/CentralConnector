package com.freshworks.central.connector.service;

import java.util.Map;
import com.freshworks.central.connector.request.CentralConnectorRequest;
import com.freshworks.central.hagrid.assets.*;
import java.util.List;
import com.freshworks.freshindex.index.query.Expression;
import com.freshworks.core.shared.consumer.AssetStreamResponse;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import java.util.Objects;
import java.util.ArrayList;

@Slf4j
@Service
public class ConnectorConsumerService extends AbstractConsumerService{


    @Override
     public List<Central> consumeCentralAsset(CentralConnectorRequest connectorRequest , ConnectorHagridConfiguration connectorHagridConfiguration) throws Exception{

        log.info("consumeCentralAsset invoked");
        return connectorHagridConfiguration.getConsumerService().getAssetByAssetType(Central.class);

     }


     @Override
      public List<Central> consumeCentralAssetByFilter(CentralConnectorRequest connectorRequest , ConnectorHagridConfiguration connectorHagridConfiguration , Expression expression) throws Exception{

           log.info("consumeCentralAssetByFilter invoked");
           return connectorHagridConfiguration.getConsumerService().getAssetByAssetTypeAndFilter(Central.class , expression);

      }


       @Override
        public List<Central> consumeCentralAssetStream(CentralConnectorRequest connectorRequest , ConnectorHagridConfiguration connectorHagridConfiguration , int startToken , int endToken) throws Exception{

            log.info("consumeCentralAssetStream invoked");
             AssetStreamResponse.Token centralToken = new AssetStreamResponse.Token();
             centralToken.setStart(startToken);
             centralToken.setCount(endToken);
             AssetStreamResponse<Central> centralAssetStreamResponse = connectorHagridConfiguration.getConsumerService().streamAssetByAssetType(Central.class, centralToken);
               if(Objects.isNull(centralAssetStreamResponse.getNextToken()))
                     return new ArrayList<>();
             return centralAssetStreamResponse.getAbstractAssetList();

        }

}
