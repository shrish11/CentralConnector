package com.freshworks.central.hagrid.steps;

import com.freshworks.central.common.util.JsonUtil;
import com.freshworks.central.connector.dto.CentralConnectorConnDetails;
import com.freshworks.central.connector.dto.CentralData;
import com.freshworks.central.connector.util.CentralConnectorUtil;
import com.freshworks.core.shared.infra.InfraDbKeyValue;
import com.freshworks.core.shared.infra.InfraDbList;

import com.freshworks.core.traverser.AbstractStep;
import com.freshworks.core.traverser.Annotations.FreshHierarchy;
import com.freshworks.core.traverser.ParentStep;
import com.freshworks.core.traverser.TraverserService;
import com.freshworks.core.traverser.exception.StepFailedException;
import com.freshworks.core.traverser.net.http.HttpRequest;
import com.freshworks.core.traverser.net.http.HttpRequestResponse;
import com.freshworks.core.traverser.net.http.HttpResponse;
import com.freshworks.platform.utils.auth.AuthUtil;
import com.freshworks.platform.utils.auth.AuthUtilFactory;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.ParseException;
import org.bson.json.JsonObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Stack;

@FreshHierarchy(parentClass = ParentStep.class, rateLimit = 5, duration = 5)
public class Central extends AbstractStep {

    public Central(InfraDbList list, InfraDbKeyValue abstractKeyValue) {
        super(list, abstractKeyValue);
    }

    Stack<CentralData> centralStack;
    /**
         * @param baggageMap
         * @throws StepFailedException
         */
        @Override
        public void setup(ImmutableMap<String, String> baggageMap) throws StepFailedException {
            // Implement your setup logic here if needed
            try {
                Map<String, Object> workFlowInputData = CentralConnectorUtil.getWorkFlowInputData(baggageMap);
                Map<String, Object> inputFromOtherConnector = CentralConnectorUtil.getInputFromOtherConnector(baggageMap);
                CentralConnectorConnDetails centralConnDetails = CentralConnectorUtil.getCentralConnDetails(workFlowInputData);
                saveData("centralUrl", centralConnDetails.getCentralUrl());
                saveData("authHeader", centralConnDetails.getAuthHeader());


                centralStack = CentralConnectorUtil.getCentralData(inputFromOtherConnector , centralConnDetails);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * @param parentJsonObject
         * @return
         * @throws StepFailedException
         */
        @Override
        public Optional<Boolean> shouldProceedWithParentObject(JsonNode... parentJsonObject) throws StepFailedException {
            // Implement your logic here

            return Optional.of(Boolean.TRUE);
        }

        /**
         * @param parentJsonObject
         * @return
         * @throws StepFailedException
         */
        @Override
        public Optional startSync(JsonNode... parentJsonObject) throws StepFailedException {
            // Implement your logic here
            HttpRequestResponse httpRequestResponse = new HttpRequestResponse();
            if(!centralStack.isEmpty()) {
                CentralData centralData = centralStack.pop();
                String centralUrl = getData("centralUrl");
                String authHeader = getData("authHeader");

                HttpRequest httpRequest = new HttpRequest();
                try {
                    httpRequest.initPost(centralUrl);
                    httpRequest.setHeader("service", authHeader);
                    httpRequest.setHeader("payload-version", "2.0.0");

                    httpRequest.setBodyAndContentType(JsonUtil.toJsonString(centralData), ContentType.APPLICATION_JSON);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
                httpRequestResponse.setRequest(httpRequest);

            }
            return Optional.of(httpRequestResponse);
        }

        /**
         * @param jsonNode
         * @throws StepFailedException
         */
        @Override
        public void filterResponse(JsonNode jsonNode) throws StepFailedException {
            // Implement your logic here if needed

        }

        /**
         * @param currentRequest
         * @param parentJsonObject
         * @return
         * @throws StepFailedException
         */
        @Override
        public Optional getNextSyncRequest(HttpRequestResponse currentRequest, JsonNode... parentJsonObject) throws StepFailedException {
            // Implement your logic here if needed
            HttpRequestResponse httpRequestResponse = new HttpRequestResponse();
            if(!centralStack.isEmpty()) {
                CentralData centralData = centralStack.pop();
                String centralUrl = getData("centralUrl");
                String authHeader = getData("authHeader");

                HttpRequest httpRequest = new HttpRequest();
                try {
                    httpRequest.initPost(centralUrl);
                    httpRequest.setHeader("service", authHeader);
                    httpRequest.setHeader("payload-version", "2.0.0");

                    httpRequest.setBodyAndContentType(JsonUtil.toJsonString(centralData), ContentType.APPLICATION_JSON);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
                httpRequestResponse.setRequest(httpRequest);

            }
            return Optional.of(httpRequestResponse);
        }

        /**
         * @param currentRequest
         * @return
         * @throws URISyntaxException
         * @throws StepFailedException
         */
        @Override
        public TraverserService.TraverseAction handleNon200ResponseCode(HttpRequestResponse currentRequest) throws URISyntaxException, StepFailedException {

           //write in MDC or in Document
            HttpResponse response = currentRequest.getResponse();
            int code = response.getCode();
            String body = null;
            try {
                body = response.getBody();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            System.out.println("code: "+code + " body: "+body);
            return null;
        }

        /**
         * @param currentRequest
         * @param parentJsonObject
         * @return
         * @throws StepFailedException
         */
        @Override
        public Optional<Boolean> isSyncComplete(HttpRequestResponse currentRequest, JsonNode... parentJsonObject) throws StepFailedException {
            // Implement your logic here
            if(centralStack.isEmpty())
                return Optional.of(Boolean.TRUE);
            else
                return Optional.of(Boolean.FALSE);
           }

        /**
         * @param jsonNode
         * @return
         */
        @Override
        public Optional<JsonNode> parseSyncResponse(JsonNode jsonNode) {
            // Implement your logic here
            return Optional.of(jsonNode);
        }

        /**
         *
         */
        @Override
        public void closeSync() {

        }
}

