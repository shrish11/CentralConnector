package com.freshworks.central.worker;

import com.netflix.conductor.client.http.TaskClient;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskResult;
import com.netflix.conductor.sdk.workflow.task.WorkerTask;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;
import com.google.common.collect.ImmutableMap;
import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.freshworks.central.connector.service.CentralConnectorService;
import com.freshworks.central.connector.request.CentralConnectorRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import com.freshworks.central.connector.service.ConnectorHagridConfiguration;
import lombok.extern.slf4j.Slf4j;
import com.freshworks.central.connector.util.CentralConnectorUtil;
import com.freshworks.central.worker.util.WorkerUtil;
import com.freshworks.platform.optimuscore.ThreadLocalTaskContext;


@Slf4j
@Component
public class CentralWorker {

   private final TaskClient httpTaskClient;
   private final CentralConnectorService centralConnectorService;

   ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public CentralWorker(TaskClient httpTaskClient , CentralConnectorService centralConnectorService) {
        this.httpTaskClient = httpTaskClient;
        this.centralConnectorService = centralConnectorService;
    }


    @WorkerTask(value = "central")
    public TaskResult work(Task task) {

            log.info("worker task: central invoked");
            ThreadLocalTaskContext.setCurrentTask(task);
            CentralConnectorRequest centralConnectorRequest = WorkerUtil.getConnectorRequest(task);
            ConnectorHagridConfiguration connectorHagridConfiguration = new ConnectorHagridConfiguration();
            Map<String, String> inputData = CentralConnectorUtil.convertConnectorRequestDataToHagridMap(centralConnectorRequest);

            ImmutableMap<String , String> baggageMap = centralConnectorService.filterAndCreateBaggageMap(inputData);

            centralConnectorService.startSync(baggageMap, centralConnectorRequest , connectorHagridConfiguration);


//             while(!jiraissueConnectorService.isSyncComplete(connectorHagridConfiguration)) {
//
//              }

             Map<String,Object> output = centralConnectorService.consume(centralConnectorRequest , connectorHagridConfiguration);



        task.setStatus(Task.Status.COMPLETED);
        task.setOutputData(output);
        System.out.println("invoked");
        ThreadLocalTaskContext.clear();
        return new TaskResult(task);
    }




}


