package net.joostvdg.tektonvisualizer.sensemaker.service;

import net.joostvdg.tektonvisualizer.model.PipelineStatus;

public interface NotificationService {

    /**
     * Notify on watched pipeline status received.
     * Watched in this case, means we are interested in the status of the pipeline.
     * We can only determine if we are interested in the status of the pipeline if we are able to attach it a to
     * a Supply Chain.
     *
     * Assuming this PipelineStatus is now a part of a Supply Chain, we should notify whoever wants to know about it
     * that we just received a status update.
     *
     * @param pipelineStatus the pipeline status
     */
    void notifyOnWatchedPipelineStatusReceived(PipelineStatus pipelineStatus);
}

