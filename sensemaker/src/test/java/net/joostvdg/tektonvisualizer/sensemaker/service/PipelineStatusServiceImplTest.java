package net.joostvdg.tektonvisualizer.sensemaker.service;

import net.joostvdg.tektonvisualizer.model.PipelineStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;

import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
class PipelineStatusServiceImplTest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private PipelineStatusServiceImpl pipelineStatusServiceImpl;

    @Test
    void shouldFindTestPipelineStatus() {
        List<PipelineStatus> pipelineStatuses = pipelineStatusServiceImpl.getAllPipelineStatuses();
        assertFalse(pipelineStatuses.isEmpty());
        assertEquals(1, pipelineStatuses.size());
        PipelineStatus pipelineStatus = pipelineStatuses.get(0);
        assertEquals(2, pipelineStatus.stages().size());
        assertEquals("Pipeline Status Name", pipelineStatus.name());
        assertEquals(3, pipelineStatus.results().size());
        assertEquals("github-webhook-listener", pipelineStatus.trigger().eventListener());
    }

}

