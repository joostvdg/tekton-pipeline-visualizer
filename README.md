# tekton-pipeline-visualizer

Project for visualizing Tekton Pipelines

## TODO

* collect and store code_source from the PipelineStatus's Results
* map PipelineStatus to SupplyChain
    * do we need to use a Pipeline?
    * or do we match on code_source + path?
* expose SupplyChain via GraphQL
* expose PipelineStatus via GraphQL
* create Vaadin frontend
    * show SupplyChain
    * show PipelineStatus
    * show PipelineStatus Results
* have a watch on the Tekton resources
    * and send the data to a backend via RabbitMQ
* map multiple PipelineStatus to a SupplyChain
    * e.g., when we have multiple PipelineStatus for the same code_source
* add OTEL so we can trace all steps
* figure out how we can trigger related pipelines (e.g., image scan) and link them to the same Supply Chain/System
    * can we trigger a pipeline with CloudEvents?
      * what are the Labels we get when it is triggered by a CloudEvent
      * can we emit a CloudEvent when a pipeline is done?
          * e.g., when we have a version, or when we publish an image
    * can we have Harbor emit a CloudEvent when a new image is pushed?
* create first version of the Notifier backend
    * store notification settings
    * limit to Slack and Discord?

### Messaging

Support at least these messaging systems:

* RabbitMQ
* Kafka


## Pipeline Status Metadata

### Results

* `REPO_URL` - the URL of the repository, e.g., `https://github.com/joostvdg/ingress-dns-export-controller.git`
* `REPO_COMMIT`- the commit of the repository, e.g., `c0ffee`
* `REPO_COMMIT_TIMESTAMP` - the timestamp of the commit, e.g., `f69dc94743452f0ef80904ab35b44bfb859ccf31`
* `COMMIT_AUTHOR` - the author of the commit, e.g., `Joost van der Griendt`
* `COMMIT_AUTHOR_EMAIL` - the email of the author of the commit, e.g., `me@example.com`
* `COMMIT_MESSAGE` - the message of the commit, e.g., `Initial commit`
* `COMMIT_TIMESTAMP` - the timestamp of the commit, e.g., `2024-05-15T00:15:19+02:00`
* `TRIGGER` - the trigger of the pipeline, e.g., `git` (do we get info from Tekton Triggers?)
 
### Labels

#### From a GitHub Event

* `dashboard.tekton.dev/rerunOf` (example value: `idec-image-builds-run-tqzgl-r-zklfm`)
* 
* `tekton.dev/pipeline` (example value: ` idec-image-builds`)
* `triggers.tekton.dev/eventlistener` (example value: ` idec-image-builds`)
* `triggers.tekton.dev/trigger` (example value: ` github-listener`)
* `triggers.tekton.dev/triggers-eventid` (example value: ` b09a980e-2b5d-4321-afab-068ac1d2f45d`) 

## Idea

* Backend 1 scrapes data from Tekton resources (PipelineRun, TaskRun, etc)
* Backend 2 stores the retrieved data
* Frontend visualizes

## backend 1 - Tekton Harvester

* Java Spring Boot
* Scrapes Kubernetes for data
* Caches data in Redis
* Sends data via RabbitMQ to Backend 2
* stream logs into something
 * ElasticSearch, or alternative?
 * Write access
* https://www.baeldung.com/java-kubernetes-watch

## Backend 2 - Sensemaker

* Java Spring Boot
* Postgresql for storing accounts
  * should we store the pipeline data here, or use a Graph database?
  * JooQ
  * Flyway
* Receive data via RabbitMQ
* Expose data via GraphQL and OAuth
* Read access to log storage
* connect the dots of the harvester data to internal model
    * emit CloudEvent when data is processed
    * e.g., compare current run to previous run of same pipeline/system
    * do we send it via RabbitMQ? Or Kafka?

## Backend 3 - Notifier

* Notification configuration
* SQLlite?
* Export via GraphQL
* Can receive notification information via RabbitMQ/Kafka?
    * propagates it based on account settings
    * e.g., Email, Slack, etc
    * when status changes, when a new pipeline is triggered, etc
    * map the data to the internal model
    * e.g., Team/User/Role/NotificationType


## Frontend

* Vaadin
* Retrieve data via GraphQL (from Backend 2 and 3)
* Render data based on user account / role /org

## Events

* https://cloudevents.io/
* https://cdevents.dev

### TODO

* create prototype of a chain of events
    * e.g., Tekton Harvester watches PipelineRun events, captures the data, and sends it to Sensemaker via RabbitMQ
    * Sensemaker processes the data, stores what is required for visualization, and sends a CloudEvent to Notifier
    * Notifier processes the CloudEvent, which can also be a CDEvent, and sends notifications to the configured channels
      * we can see two different kinds of channels, those aimed at humans, and those at machines
      * the humans can be notified via Slack, Discord, Email, etc, and have a way of configuring this (team level, personal level)
      * the machines can be notified via Kafka, or JSON over HTTP, etc
* How do we chain the events?
    * Tekton Harvester watches a PipelineRun, sends a message over RabbitMQ Queue
    * Sensemaker listens to the RabbitMQ Queue, processes the message, and sends a something to Notifier via Kafka
    * Notifier listens to the Kafka topic, processes the message, and then sends a message to a Tekton EventListener
    * This kicks of the next pipeline
* Recurring events?
    * e.g., every day at 3 AM, we want to run a pipeline that checks the latest version of a dependency
    * e.g., every time a new image is pushed to a registry, we want to run a pipeline that scans the image
    * where do we configure this?
    * how do we trigger this?
* example: do we create a mapping for an application that is picked up by Notifier, so it can map an incoming event to an outgoing event?
* Goals
    * make the X Harvester a standard routingType of thing, so we can have a Tekton Harvester, ArgoCD Harvester, etc
    * make the X Harvester a dumb thing, it can translate a specfic event to a generic event and send it to queue
    * Sensemaker is the brains, a modern monolith (e.g., Spring Modulith), which is able to process the events, correlate them, and store them
    * Notifier is the notification engine, which can be configured to send notifications to different channels
      * so it needs a way of mapping incoming events to outgoing events
      * it should have an easy way of configuring this, e.g., Kubernetes CR, so that you can manage it adjacent to the pipelines

## Links

* RabbitMQ
    * https://spring.academy/guides/messaging-with-rabbitmq 
    * https://spring.io/guides/gs/messaging-rabbitmq
    * https://www.rabbitmq.com/tutorials/tutorial-one-spring-amqp.html
    * https://docs.spring.io/spring-boot/reference/messaging/amqp.html
    * https://spring.io/blog/2010/06/14/understanding-amqp-the-protocol-used-by-rabbitmq/
* Tekton
    * https://tekton.dev/docs/pipelines/pipelines/
    * https://github.com/tektoncd/results
* Serialization
    * https://snyk.io/blog/new-java-17-features-for-improved-security-and-serialization/
    * https://www.baeldung.com/java-serialization
    * https://www.baeldung.com/java-apache-avro
    * https://avro.apache.org/docs/1.11.1/getting-started-java/
    * https://www.baeldung.com/fastjson
* Database
    * https://documentation.red-gate.com/flyway/quickstart-how-flyway-works/quickstart-guides/quickstart-maven
    * https://www.baeldung.com/database-migrations-with-flyway
    * https://www.jooq.org/doc/latest/manual/code-generation/codegen-maven/
    * https://www.sivalabs.in/spring-boot-jooq-tutorial-getting-started/
    * https://github.com/sivaprasadreddy/spring-boot-jooq-demo
    * https://github.com/joostvdg/keep-watching/blob/master/backend/src/main/java/com/github/joostvdg/keepwatching/service/impl/MovieServiceImpl.java
    * https://maciejwalkowiak.com/blog/testcontainers-spring-boot-setup/
* Testing Spring
    * https://www.baeldung.com/junit-5-extensions