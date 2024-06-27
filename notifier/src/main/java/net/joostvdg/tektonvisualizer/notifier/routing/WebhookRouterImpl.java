/* (C)2024 */
package net.joostvdg.tektonvisualizer.notifier.routing;

import com.alibaba.fastjson2.JSON;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import jakarta.annotation.PostConstruct;
import net.joostvdg.tektonvisualizer.model.PipelineStatus;
import net.joostvdg.tektonvisualizer.notifier.cloudevents.CloudEventBase;
import net.joostvdg.tektonvisualizer.notifier.cloudevents.EventData;
import net.joostvdg.tektonvisualizer.notifier.mapping.NotificationMapping;
import net.joostvdg.tektonvisualizer.notifier.mapping.SourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.util.retry.Retry;

import javax.net.ssl.SSLException;

@Service
public class WebhookRouterImpl implements WebhookRouter {

  //    {
  //        "specversion": "1.0",
  //            "type": "dev.cdeventsx.joostvdg-sourcebuild.finished.0.1.0",
  //            "source": "/tekton/harbor-events-relay-source-build",
  //            "id": "f8aa59e5f027607f734b34bc9ce5f13938368f74",
  //            "time": "2024-06-03T22:03:20Z",
  //            "datacontenttype": "application/json",
  //            "data": {
  //        "version": "0.1.0",
  //                "git-repo": "github.com/joostvdg/harbor-events-relay",
  //                "git-repo-path": "cmd/harbor-events-relay",
  //                "git-commit": "f8aa59e5f027607f734b34bc9ce5f13938368f74"
  //    }
  //    }

  private final Logger logger = LoggerFactory.getLogger(WebhookRouterImpl.class);

  private final AtomicInteger pendingResponsesCtr = new AtomicInteger();
  private static final int MAX_RETRIES = 3;
  private final Map<SourceType, String> cloudEventTypes;

    public WebhookRouterImpl() {
        cloudEventTypes = new HashMap<>();
        cloudEventTypes.put(SourceType.SOURCE_BUILD, "dev.cdeventsx.joostvdg-sourcebuild.finished.0.1.0");
      cloudEventTypes.put(SourceType.IMAGE_BUILD, "dev.cdeventsx.joostvdg-imagebuild.finished.0.1.0");
      cloudEventTypes.put(SourceType.IMAGE_SCAN, "dev.cdeventsx.joostvdg-imagescan.finished.0.1.0");

    }


    @Override
  public boolean route(NotificationMapping notificationMapping, PipelineStatus pipelineStatus) {

    String gitCommit = pipelineStatus.results().get("REPO_COMMIT");
    String version = pipelineStatus.results().get("VERSION");

    var eventId = UUID.randomUUID().toString();
    var cloudEventData =
        new EventData(
            version,
            notificationMapping.source().gitUrl(),
            notificationMapping.source().subPath(),
            gitCommit,
            "",
            "");
    // TODO: properly handle event Type and event Source
    // TODO: properly handle event Time
    var cloudEvent =
        new CloudEventBase(
            "1.0",
            cloudEventTypes.get(notificationMapping.source().type()),
            "/tekton-vizualizer/notifier",
            eventId,
            "2024-06-03T22:03:20Z",
            "application/json",
            cloudEventData);

    var jsonPayload = JSON.toJSONBytes(cloudEvent);

    logger.info(
        "Sending webhook to: {}, for notifying on pipelineStatus: {}",
        notificationMapping.target().url(),
        pipelineStatus.name());
    logger.info("Target: {}, Payload: {}",notificationMapping.target().url() ,new String(jsonPayload, StandardCharsets.UTF_8));

    // TODO: properly handle SSL certs
      SslContext sslContext = null;
      try {
          sslContext = SslContextBuilder
                  .forClient()
                  .trustManager(InsecureTrustManagerFactory.INSTANCE)
                  .build();
      } catch (SSLException e) {
          throw new RuntimeException(e);
      }
    SslContext finalSslContext = sslContext;
    HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(finalSslContext));
    WebClient webClient = WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient)).build();
    webClient
        .post()
        .uri(notificationMapping.target().url())
        .bodyValue(jsonPayload)
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, res -> handleClientErrors(res.statusCode()))
        .onStatus(HttpStatusCode::is5xxServerError, res -> handleServerErrors(res.statusCode()))
        .toBodilessEntity()
        .doOnError(err -> logger.info("Error Occurred: {}", err.getMessage()))
        .doOnSuccess(b -> logger.info("Successful response arrived with body: {}", b))
        .retryWhen(
            Retry.backoff(MAX_RETRIES, Duration.ofMillis(500L))
                .doAfterRetry(retrySignal -> pendingResponsesCtr.decrementAndGet()))
        .block();

    return true;
  }

  private Mono<? extends Throwable> handleClientErrors(HttpStatusCode httpStatusCode) {
    logger.error("Client Error: {}", httpStatusCode);
    return Mono.error(new RuntimeException("Client Error: " + httpStatusCode));
  }

  private Mono<? extends Throwable> handleServerErrors(HttpStatusCode httpStatusCode) {
    logger.error("Server Error: {}", httpStatusCode);
    return Mono.error(new RuntimeException("Server Error: " + httpStatusCode));
  }
}
