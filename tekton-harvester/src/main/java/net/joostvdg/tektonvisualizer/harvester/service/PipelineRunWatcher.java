package net.joostvdg.tektonvisualizer.harvester.service;

import com.google.gson.JsonElement;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.util.Watch;
import io.kubernetes.client.util.generic.dynamic.DynamicKubernetesApi;
import io.kubernetes.client.util.generic.dynamic.DynamicKubernetesObject;
import io.kubernetes.client.util.generic.options.ListOptions;

import net.joostvdg.tektonvisualizer.model.PipelineStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;

@Component
public class PipelineRunWatcher {

    private final ApiClient apiClient;

    private final PipelineStatusService pipelineStatusService;

    private final Logger logger = LoggerFactory.getLogger(PipelineRunWatcher.class);

    public PipelineRunWatcher(ApiClient apiClient, PipelineStatusService pipelineStatusService) {
        this.apiClient = apiClient;
        this.pipelineStatusService = pipelineStatusService;
    }

    // TODO-1: ensure we don't start this method on builds/testing
    @PostConstruct
    @Async
    public void watch() {
        logger.info("Watching for PipelineRuns");

        DynamicKubernetesApi dynamicApi =
                new DynamicKubernetesApi("tekton.dev", "v1", "pipelineruns", apiClient);

        ListOptions listOptions = new ListOptions();
        listOptions.setLimit(1000);

        try {
            try (var watch = dynamicApi.watch(listOptions)) {
                for (Watch.Response<DynamicKubernetesObject> item : watch) {
                    var pipelineRun = item.object;
                    var pipelineRunName = pipelineRun.getMetadata().getName();
                    logger.info("Detected PipelineRun: {}", pipelineRunName);

                    JsonElement statusData = pipelineRun.getRaw().get("status");
                    if (statusData == null) {
                        continue;
                    }
                    JsonElement conditions = statusData.getAsJsonObject().get("conditions");
                    if (conditions == null) {
                        continue;
                    }
                    JsonElement firstCondition = conditions.getAsJsonArray().get(0);
                    String statusSuccess = firstCondition.getAsJsonObject().get("status").getAsString();
                    String statusType = firstCondition.getAsJsonObject().get("type").getAsString();
                    // TODO: parse and send to queue
                    // TODO: can we make an Algebraic Data Type for this (e.g., the Succeeded, True/False)?
                    if (statusSuccess.equals("True") && statusType.equals("Succeeded")) {
                        logger.info("PipelineRun: {} has succeeded", pipelineRunName);
                        pipelineStatusService.processPipelineRun(pipelineRun);
                    } else if (statusSuccess.equals("False") && statusType.equals("Succeeded")) {
                        logger.info("PipelineRun: {} has failed", pipelineRunName);
                        pipelineStatusService.processPipelineRun(pipelineRun);
                    }
                }
            }
        } catch (ApiException | IOException e) {
            logger.error("Error while watching for PipelineRuns", e);
            throw new RuntimeException(e);
        }

    }
}
