/* (C)2024 */
package net.joostvdg.tektonvisualizer.harvester.controller;

import java.util.List;
import net.joostvdg.tektonvisualizer.harvester.service.PipelineStatusService;
import net.joostvdg.tektonvisualizer.model.PipelineStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("export")
public class ExportController {

  private final PipelineStatusService pipelineStatusService;

  // Controller Constructor to add the InspectService
  public ExportController(PipelineStatusService pipelineStatusService) {
    this.pipelineStatusService = pipelineStatusService;
  }

  @GetMapping(produces = "application/json")
  public ResponseEntity<List<PipelineStatus>> exportDns() {
    return ResponseEntity.ok(pipelineStatusService.getPipelineStatuses().getBody());
  }
}
