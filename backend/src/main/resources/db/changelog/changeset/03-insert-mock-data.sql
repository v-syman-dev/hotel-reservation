--liquibase formatted sql
--changeset vladislav:3

-- Add started data

-- Addresses
INSERT INTO addresses (id, country, city, street) VALUES
(nextval('address_sequence'), 'North Korea', 'P''yongsong', '5 Del Mar Drive'),
(nextval('address_sequence'), 'Botswana', 'Kopong', '21 Jenifer Road'),
(nextval('address_sequence'), 'China', 'Rongmei', '1 Mifflin Center');

-- Hotels
INSERT INTO hotels (id, name, rating, address_id) VALUES
(nextval('hotel_sequence'), 'Meevee', 2.54, (SELECT id FROM addresses WHERE country = 'North Korea' AND city = 'P''yongsong' AND street = '5 Del Mar Drive')),
(nextval('hotel_sequence'), 'Rhyloo', 4.76, (SELECT id FROM addresses WHERE country = 'Botswana' AND city = 'Kopong' AND street = '21 Jenifer Road')),
(nextval('hotel_sequence'), 'DabZ', 2.11, (SELECT id FROM addresses WHERE country = 'China' AND city = 'Rongmei' AND street = '1 Mifflin Center'));

-- Rooms (sample from MOCK_DATA.json)
INSERT INTO rooms (id, hotel_id, number, type, price_per_night) VALUES
(nextval('room_sequence'), (SELECT id FROM hotels WHERE name = 'Meevee'), 485, 'BUSINESS', 742.80),
(nextval('room_sequence'), (SELECT id FROM hotels WHERE name = 'Meevee'), 291, 'FAMILY', 688.11),
(nextval('room_sequence'), (SELECT id FROM hotels WHERE name = 'Meevee'), 255, 'DELUXE', 1713.41),
(nextval('room_sequence'), (SELECT id FROM hotels WHERE name = 'Rhyloo'), 1, 'ECONOMY', 394.14),
(nextval('room_sequence'), (SELECT id FROM hotels WHERE name = 'Rhyloo'), 359, 'FAMILY', 1142.65),
(nextval('room_sequence'), (SELECT id FROM hotels WHERE name = 'Rhyloo'), 252, 'PRESIDENTIAL', 1045.27),
(nextval('room_sequence'), (SELECT id FROM hotels WHERE name = 'DabZ'), 261, 'ECONOMY', 1915.70),
(nextval('room_sequence'), (SELECT id FROM hotels WHERE name = 'DabZ'), 9, 'BUSINESS', 407.73),
(nextval('room_sequence'), (SELECT id FROM hotels WHERE name = 'DabZ'), 208, 'PRESIDENTIAL', 1784.10);

-- Missing conveniences from MOCK_DATA.json
INSERT INTO conveniences (id, name) VALUES
(nextval('convenience_sequence'), 'ONSEN'),
(nextval('convenience_sequence'), 'BUTLER_SERVICE'),
(nextval('convenience_sequence'), 'MINI_BAR'),
(nextval('convenience_sequence'), 'VALET'),
(nextval('convenience_sequence'), 'CONCIERGE')
ON CONFLICT (name) DO NOTHING;

-- Hotel <-> convenience links (deduplicated)
INSERT INTO hotel_conveniences (hotel_id, convenience_id) VALUES
((SELECT id FROM hotels WHERE name = 'Meevee'), (SELECT id FROM conveniences WHERE name = 'ONSEN')),
((SELECT id FROM hotels WHERE name = 'Meevee'), (SELECT id FROM conveniences WHERE name = 'INFINITY_POOL')),
((SELECT id FROM hotels WHERE name = 'Meevee'), (SELECT id FROM conveniences WHERE name = 'RESTAURANT')),

((SELECT id FROM hotels WHERE name = 'Rhyloo'), (SELECT id FROM conveniences WHERE name = 'BAR')),
((SELECT id FROM hotels WHERE name = 'Rhyloo'), (SELECT id FROM conveniences WHERE name = 'TEA_GARDEN')),
((SELECT id FROM hotels WHERE name = 'Rhyloo'), (SELECT id FROM conveniences WHERE name = 'BUTLER_SERVICE')),
((SELECT id FROM hotels WHERE name = 'Rhyloo'), (SELECT id FROM conveniences WHERE name = 'SPA')),
((SELECT id FROM hotels WHERE name = 'Rhyloo'), (SELECT id FROM conveniences WHERE name = 'MINI_BAR')),

((SELECT id FROM hotels WHERE name = 'DabZ'), (SELECT id FROM conveniences WHERE name = 'SAUNA')),
((SELECT id FROM hotels WHERE name = 'DabZ'), (SELECT id FROM conveniences WHERE name = 'RESTAURANT')),
((SELECT id FROM hotels WHERE name = 'DabZ'), (SELECT id FROM conveniences WHERE name = 'CONFERENCE_HALL')),
((SELECT id FROM hotels WHERE name = 'DabZ'), (SELECT id FROM conveniences WHERE name = 'HELIPAD')),
((SELECT id FROM hotels WHERE name = 'DabZ'), (SELECT id FROM conveniences WHERE name = 'BUTLER_SERVICE')),
((SELECT id FROM hotels WHERE name = 'DabZ'), (SELECT id FROM conveniences WHERE name = 'WIFI')),
((SELECT id FROM hotels WHERE name = 'DabZ'), (SELECT id FROM conveniences WHERE name = 'CASINO'))
ON CONFLICT DO NOTHING;
