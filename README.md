# tekton-pipeline-visualizer

Project for visualizing Tekton Pipelines

## TODO

* have a watch on the Tekton resources
    * and send the data to a backend via RabbitMQ   
* figure out how we can trigger related pipelines (e.g., image scan) and link them to the same Supply Chain/System
* can we trigger a pipeline with CloudEvents?
* what are the Labels we get when it is triggered by a CloudEvent
* can we emit a CloudEvent when a pipeline is done?
    * e.g., when we have a version, or when we publish an image
* can we have Harbor emit a CloudEvent when a new image is pushed?

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
