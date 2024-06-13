CREATE UNIQUE INDEX code_source_name ON CODE_SOURCE (source_name);
CREATE UNIQUE INDEX supply_chain_name ON SUPPLY_CHAIN (name);

-- insert a code source
INSERT INTO code_source (
    source_name,
    source_type,
    source_url
) VALUES (
    'IDEC',
    'Github',
    'https://github.com/joostvdg/ingress-dns-export-controller.git'
);

INSERT INTO supply_chain (
    name
) VALUES (
    'IDEC'
);

INSERT INTO supply_chain_code_source (
    supply_chain_id,
    code_source_id
) VALUES (
    (select id from supply_chain where name = 'IDEC'),
    (select id from code_source where source_name = 'IDEC')
);
