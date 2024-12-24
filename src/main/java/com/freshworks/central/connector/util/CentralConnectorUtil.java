package com.freshworks.central.connector.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.freshworks.central.common.util.CommonUtil;
import com.freshworks.central.common.util.JsonUtil;
import com.freshworks.central.connector.dto.CentralConnectorConnDetails;
import com.freshworks.central.connector.dto.CentralData;
import com.freshworks.central.connector.request.CentralConnectorRequest;
import com.freshworks.core.traverser.net.http.HttpRequest;
import com.freshworks.core.traverser.net.http.HttpRequestResponse;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.ContentType;
import org.bson.json.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

@Slf4j
public class CentralConnectorUtil {

     public static Map<String,String> convertConnectorRequestDataToHagridMap(CentralConnectorRequest connectorRequest) {

            Map<String, String> workflowMap = new HashMap<>();
            Map<String,String> inputFromOtherConnector = new HashMap<>();
            try {
                Map<String, Object> workflowInput = connectorRequest.getWorkflowInput();
                String workflowInputStr = JsonUtil.toJsonString(workflowInput);
                workflowMap.put("workflowInput", workflowInputStr);
//                workflowMap = CommonUtil.convertMap(connectorRequest.getWorkflowInput());
            }  catch (Exception e) {
                throw new RuntimeException(e);
            }

            try {
 //               inputFromOtherConnector = CommonUtil.convertMap(connectorRequest.getInputRequiredFrom());
                String inputFromOtherConnectorStr = JsonUtil.toJsonString(connectorRequest.getInputRequiredFrom());
                inputFromOtherConnector.put("inputRequiredFrom", inputFromOtherConnectorStr);


            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            return CommonUtil.mergeMaps(workflowMap, inputFromOtherConnector);
        }

        public static Map<String, Object> getWorkFlowInputData(ImmutableMap<String, String> baggageMap) throws IOException {

            String workflowInput = baggageMap.get("workflowInput");
            return JsonUtil.parseAsObject(workflowInput, new TypeReference<>() {});

        }

        public static Map<String, Object> getInputFromOtherConnector(ImmutableMap<String, String> baggageMap) throws IOException {

        String inputRequiredFrom = baggageMap.get("inputRequiredFrom");
        return JsonUtil.parseAsObject(inputRequiredFrom, new TypeReference<>() {});

    }


        private static Map<String,Object> getCentralConnectorInput(String centralConnectorInput) throws IOException {
            //Implement your logic here
//            String centralConnectorInput = baggageMap.get("central_connector");
            return JsonUtil.parseAsObject(centralConnectorInput, new TypeReference<>() {});

        }

        public static CentralConnectorConnDetails getCentralConnDetails(Map<String, Object> workFlowInputData) throws IOException {
            //Implement your logic here
            Map<String, Object> centralConnector = getCentralConnectorInput(JsonUtil.toJsonString(workFlowInputData.get("central_connector")));
            return CentralConnectorConnDetails.builder()
                    .centralUrl(String.valueOf(centralConnector.get("central_url")))
                    .centralTopic(String.valueOf(centralConnector.get("central_topic")))
                    .authHeader(String.valueOf(centralConnector.get("central_authentication_header")))
                    .build();


        }

        private static List<JsonObject> getJiraIssues(String jiraConnectorInputData) throws IOException {
            // Implement your logic here


            Map<String, Object> jiraConnectorData = JsonUtil.parseAsObject(jiraConnectorInputData, new TypeReference<>() {
            });


            JsonNode jsonNode = JsonUtil.parseAsJsonNode(jiraConnectorData.get("jiraIssues"));
            List<JsonObject> jiraList = getJsonObjects(jsonNode);
            return jiraList;

        }

    public static Stack<CentralData> getCentralData(Map<String, Object> inputFromOtherConnector , CentralConnectorConnDetails connectorConnDetails) throws IOException {

        List<JsonObject> jiraIssues = getJiraIssues(JsonUtil.toJsonString(inputFromOtherConnector.get("jira_connector_data")));
        Stack<CentralData> centralDataStack = new Stack<>();
        for (JsonObject jiraIssue : jiraIssues) {
            CentralData centralData = CentralData.builder()
                    .pod("US")
                    .payload_type(connectorConnDetails.getCentralTopic())
                    .payload(jiraIssue)
                    .account_id("631122015820167")
                    .organisation_id("452387069477037914")
                    .payloadVersion("2.0.0")
                    .requestId(UUID.randomUUID().toString())
                    .region("US").build();

            centralDataStack.push(centralData);
        }

        return centralDataStack;
    }

    private static @NotNull List<JsonObject> getJsonObjects(JsonNode jsonNode) {
        List<JsonObject> jiraList = new ArrayList<>();
        if (jsonNode.isArray()) {
            for (JsonNode node : jsonNode) {
                jiraList.add(new JsonObject(node.toString()));
            }
        }
        return jiraList;
    }

    public static @NotNull HttpRequestResponse getHttpRequestResponse(Stack<CentralData> centralStack , String centralUrl , String authHeader) {
        HttpRequestResponse httpRequestResponse = new HttpRequestResponse();
        if(!centralStack.isEmpty()) {
            CentralData centralData = centralStack.pop();
//            String centralUrl = getData("centralUrl");
//            String authHeader = getData("authHeader");

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
        return httpRequestResponse;
    }


}
