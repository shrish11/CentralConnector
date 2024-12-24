package com.freshworks.central.worker.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.freshworks.central.common.util.JsonUtil;
import com.freshworks.central.connector.request.CentralConnectorRequest;
import com.netflix.conductor.common.metadata.tasks.Task;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class WorkerUtil {

    public static CentralConnectorRequest getConnectorRequest(Task task) {

               String requestId = task.getWorkflowInstanceId() + "-" + task.getTaskDefName();

               Map<String, Object> workflowInput = new HashMap<>();
               Map<String, Object> inputData = task.getInputData();
               try {
                      if(inputData != null && inputData.get("workflow") != null) {
                           workflowInput = (Map<String,Object>)inputData.get("workflow");
                     }

                    } catch (Exception e) {

                         log.error(e.getMessage());
                          throw new RuntimeException(e);
                    }

               Map<String, Object> inputRequiredFrom = new HashMap<>();
               try {
                       if (inputData != null && inputData.containsKey("inputRequiredFrom")) {
                               inputRequiredFrom = (Map<String, Object>) inputData.get("inputRequiredFrom");
                               System.out.println("Input Required From:");
                           }
                    } catch (Exception e) {
                          log.error(e.getMessage());
                          throw new RuntimeException(e);
                    }
               try {
               return CentralConnectorRequest.
                             builder().requestId(requestId)
                             .workflowInput(workflowInput)
                             .inputRequiredFrom(inputRequiredFrom)
                             .accountId(Optional.ofNullable(task.getInputData().get("accountId"))
                                     .map(Object::toString)
                                     .map(Long::parseLong)
                                     .orElse(null))
                             .data(JsonUtil.parseAsJsonNode(task.getInputData().get("data")))
                             .build();
                 } catch (JsonProcessingException e) {
                         log.error(e.getMessage());
                          throw new RuntimeException(e);
                    }

           }



}
