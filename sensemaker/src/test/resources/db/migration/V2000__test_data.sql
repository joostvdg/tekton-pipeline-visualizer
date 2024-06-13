INSERT INTO pipeline_status (
    pipeline_id,
    name,
    start_timestamp,
    duration,
    success,
    completion_message
) VALUES (
    'pipeline-identifier',
    'Pipeline Status Name',
    '2023-04-01 12:00:00+00',
    3600,
    TRUE,
    'Pipeline completed successfully'
);

INSERT INTO pipeline_result (
    pipeline_status_id,
    key,
    value
) VALUES (
    1,
    'REPO_URL',
    'https://github.com/joostvdg/ingress-dns-export-controller.git'
);

INSERT INTO pipeline_result (
    pipeline_status_id,
    key,
    value
) VALUES (
    1,
    'REPO_COMMIT',
    'f69dc94743452f0ef80904ab35b44bfb859ccf31'
);

INSERT INTO pipeline_result (
    pipeline_status_id,
    key,
    value
) VALUES (
    1,
    'COMMIT_TIMESTAMP',
    '2024-05-15T00:15:19+02:00'
);

-- insert two pipeline stages
INSERT INTO pipeline_stage (
    pipeline_status_id,
    name,
    order_number,
    duration,
    success,
    completion_message
) VALUES (
    1,
    'Pipeline Stage 1',
    2,
    1800,
    TRUE,
    'Pipeline Stage 1 completed successfully'
);

INSERT INTO pipeline_stage (
    pipeline_status_id,
    name,
    order_number,
    duration,
    success,
    completion_message
) VALUES (
    1,
    'Pipeline Stage 2',
    2,
    1800,
    TRUE,
    'Pipeline Stage 2 completed successfully'
);

-- insert a pipeline trigger
INSERT INTO pipeline_trigger (
    pipeline_status_id,
    trigger_type,
    event_listener,
    event_id
) VALUES (
    1,
    'GitHub',
    'github-webhook-listener',
    'f69dc94743452f0ef80904ab35b44bfb859ccf31'
);

-- insert a code source
INSERT INTO code_source (
    source_name,
    source_type,
    source_url,
    sub_path
) VALUES (
    'Tekton Pipeline Visualizer - Sensemaker',
    'Github',
    'https://github.com/joostvdg/tekton-pipeline-visualizer.git',
    'sensemaker'
);

INSERT INTO code_source (
    source_name,
    source_type,
    source_url,
    sub_path
) VALUES (
    'Tekton Pipeline Visualizer - Harvester',
    'Github',
    'https://github.com/joostvdg/tekton-pipeline-visualizer.git',
    'harvester'
);

-- insert a supply chain
INSERT INTO supply_chain (
    name
) VALUES (
    'Tekton Pipeline Visualizer'
);

-- insert a supply chain code source
INSERT INTO supply_chain_code_source (
    supply_chain_id,
    code_source_id
) VALUES (
    (select id from supply_chain where name = 'Tekton Pipeline Visualizer'),
    (select id from code_source where source_name = 'Tekton Pipeline Visualizer - Sensemaker')
);
INSERT INTO supply_chain_code_source (
    supply_chain_id,
    code_source_id
) VALUES (
    (select id from supply_chain where name = 'Tekton Pipeline Visualizer'),
    (select id from code_source where source_name = 'Tekton Pipeline Visualizer - Harvester')
);

-- -- insert a pipeline status supply chain
-- INSERT INTO pipeline_status_supply_chain (
--     pipeline_status_id,
--     supply_chain_id
-- ) VALUES (
--     1,
--     1
-- );
