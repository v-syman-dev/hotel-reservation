--liquibase formatted sql
--changeset vladislav:2

-- Conveniences
INSERT INTO conveniences (id, name) VALUES
(nextval('convenience_sequence'), 'WIFI'),
(nextval('convenience_sequence'), 'RESTAURANT'),
(nextval('convenience_sequence'), 'PARKING'),
(nextval('convenience_sequence'), 'POOL'),
(nextval('convenience_sequence'), 'GYM'),
(nextval('convenience_sequence'), 'SPA'),
(nextval('convenience_sequence'), 'BAR'),
(nextval('convenience_sequence'), 'BREAKFAST'),
(nextval('convenience_sequence'), 'INFINITY_POOL'),
(nextval('convenience_sequence'), 'CASINO'),
(nextval('convenience_sequence'), 'ROOFTOP_BAR'),
(nextval('convenience_sequence'), 'TEA_GARDEN'),
(nextval('convenience_sequence'), 'HELIPAD'),
(nextval('convenience_sequence'), 'SAUNA'),
(nextval('convenience_sequence'), 'SKI_STORAGE'),
(nextval('convenience_sequence'), 'CONFERENCE_HALL');
