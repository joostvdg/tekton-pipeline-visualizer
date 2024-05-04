# tekton-pipeline-visualizer

Project for visualizing Tekton Pipelines

## Idea

* Backend 1 scrapes data from Tekton resources (PipelineRun, TaskRun, etc)
* Backend 2 stores the retrieved data
* Frontend visualizes

## backend 1

* Java Spring Boot
* Scrapes Kubernetes for data
* Caches data in Redis
* Sends data via RabbitMQ to Backend 2
* stream logs into something
 * ElasticSearch, or alternative?
 * Write access

## Backend 2

* Java Spring Boot
* Postgresql for storing accounts
  * should we store the pipeline data here, or use a Graph database?
  * JooQ
  * Flyway
* Receive data via RabbitMQ
* Expose data via GraphQL and OAuth
* Read access to log storage

## Backend 3

* Notification configuration
* SQLlite?
* Export via GraphQL
* Can receive notification information via RabbitMQ
 * propogates it based on account settings

## Frontend

* Vaadin
* Retrieve data via GraphQL
* Render data based on user account / role /org
