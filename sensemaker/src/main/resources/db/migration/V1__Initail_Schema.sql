
-- Create pipeline_status Table
CREATE TABLE pipeline_status (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    pipeline_id VARCHAR(255) NOT NULL,
    start_timestamp TIMESTAMP WITH TIME ZONE NOT NULL,
    duration BIGINT NOT NULL,
    success BOOLEAN NOT NULL,
    completion_message VARCHAR(255)
);

-- Create pipeline_stage Table
CREATE TABLE pipeline_stage (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    pipeline_status_id INT NOT NULL,
    duration INT,
    order_number INT,
    success BOOLEAN NOT NULL,
    completion_message VARCHAR(255),
    FOREIGN KEY (pipeline_status_id) REFERENCES pipeline_status(id)
);

-- Create pipeline_result Table
CREATE TABLE pipeline_result (
    id SERIAL PRIMARY KEY,
    pipeline_status_id INT NOT NULL,
    key VARCHAR(255) NOT NULL,
    value VARCHAR(255) NOT NULL,
    FOREIGN KEY (pipeline_status_id) REFERENCES pipeline_status(id)
);

-- Assuming pipeline_trigger structure, adjust as necessary
CREATE TABLE pipeline_trigger (
    id SERIAL PRIMARY KEY,
    pipeline_status_id INT NOT NULL,
    trigger_type VARCHAR(255) NOT NULL,
    event_listener VARCHAR(255) NOT NULL,
    event_id VARCHAR(255) NOT NULL,
    rerun_of VARCHAR(255),
    FOREIGN KEY (pipeline_status_id) REFERENCES pipeline_status(id)
);

-- Create code_source Table
CREATE TABLE code_source (
    id SERIAL PRIMARY KEY,
    source_name VARCHAR(255) NOT NULL,
    source_type VARCHAR(255) NOT NULL,
    source_url VARCHAR(255) NOT NULL,
    sub_path VARCHAR(255)
);

-- Create supply_chain Table
CREATE TABLE supply_chain (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

-- Create join table for supply_chain and code_source
CREATE TABLE supply_chain_code_source (
    supply_chain_id INT NOT NULL,
    code_source_id INT NOT NULL,
    PRIMARY KEY (supply_chain_id, code_source_id),
    FOREIGN KEY (supply_chain_id) REFERENCES supply_chain(id),
    FOREIGN KEY (code_source_id) REFERENCES code_source(id)
);

-- Create join table for pipeline_status and supply_chain
CREATE TABLE pipeline_status_supply_chain (
    pipeline_status_id INT NOT NULL,
    supply_chain_id INT NOT NULL,
    PRIMARY KEY (pipeline_status_id, supply_chain_id),
    FOREIGN KEY (pipeline_status_id) REFERENCES pipeline_status(id),
    FOREIGN KEY (supply_chain_id) REFERENCES supply_chain(id)
);

-- Unique Index on Pipeline Status Name
CREATE UNIQUE INDEX pipeline_status_name_index ON pipeline_status (name);
CREATE UNIQUE INDEX url_subpath_index ON CODE_SOURCE (SOURCE_URL, SUB_PATH);