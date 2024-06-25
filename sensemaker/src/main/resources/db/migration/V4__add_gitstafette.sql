-- insert a code source
INSERT INTO code_source (
    source_name,
    source_type,
    source_url
) VALUES (
    'gitstafette',
    'Github',
    'https://github.com/joostvdg/gitstafette'
);

INSERT INTO supply_chain (
    name
) VALUES (
    'gitstafette'
);

INSERT INTO supply_chain_code_source (
    supply_chain_id,
    code_source_id
) VALUES (
    (select id from supply_chain where name = 'gitstafette'),
    (select id from code_source where source_name = 'gitstafette')
);
