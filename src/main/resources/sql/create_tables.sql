

CREATE TABLE location(
  id BIGSERIAL not null,
  created bigint not null default 0,
  modified bigint not null default 0,
  parent_location_id bigint,
  title varchar(64) not null,
  metadata_id BIGINT NOT NULL,
  location_type SMALLINT NOT NULL,
  zone_id bigint,
  zone_name varchar(64),
  area_id bigint,
  area_name VARCHAR(64),
  region_id bigint not null,
  region_name VARCHAR(64) not null,
  state VARCHAR(32) not null,
  country VARCHAR(32) not null,
  primary key(id)
);

CREATE INDEX location_parent on location(parent_location_id);
CREATE INDEX location_zone ON location(zone_id);
CREATE INDEX location_area ON location(area_id);
CREATE INDEX location_region ON location(region_id);

insert into location (title, region_id, region_name, location_type, metadata_id, state, country, parent_location_id)
VALUES
  ('Mount Rainier',      1, 'Mount Rainier National Park', 0, 0, 'CA', 'USA', NULL),
  ('Rainier Route #1',   1, 'Mount Rainier National Park', 0, 0, 'CA', 'USA', 1),
  ('Mount Rainier Zone', 1, 'Mount Rainier National Park', 0, 0, 'CA', 'USA', 1);

create table guide(
  id bigserial not null,
  created bigint not null default 0,
  updated bigint not null default 0,
  name varchar(64) not null,
  location varchar(32), -- could also reference an actual location? city state country?
  about_info text,
  PRIMARY KEY(id)
);

insert into guide(id, name, location, about_info) VALUES
  (1, 'RMI Expeditions', 'Seattle, WA', 'Getting safely up and down mountains is just the beginning of the story at Rainier Mountaineering, Inc. (RMI). Founded by the legendary Lou Whittaker and staffed by the most experienced and talented guides in America, RMI has built a four-decade long legacy of safe, successful, and enjoyable mountaineering adventures.'),
  (2, 'Shrek', 'Denver, CO', 'Shrek was founded by Donkey is 2014');

create table trip(
  id bigserial NOT NULL,
  created bigint not null default 0,
  updated bigint not null default 0,
  guide_id bigint REFERENCES guide(id),
  location_id bigint references location(id),
  heading VARCHAR(128) NOT NULL,
  description text,
  itinerary text,
  PRIMARY KEY(id)
);

insert into trip(guide_id, heading, location_id, itinerary) VALUES
  (1, 'Climb a mountain!', 1, 'Arrive in Moscow (SVO). A group transfer is arranged from the airport to our hotel at 4:00 p.m. If your flight arrives earlier in the day, after 3:00 p.m., or if you are arriving at a different airport you can hire a taxi or Uber to get to the hotel.. Once we check-in to our hotel, the afternoon is free to rest and explore the city. A team orientation meeting is held at 7:00 p.m. We spend the night in Moscow at the Park Inn Sadu.')
;

create table offer(
  id BIGSERIAL NOT NULL,
  created bigint not null default 0,
  updated bigint not null default 0,
  offer_id bigint REFERENCES trip(id),
  start_time TIMESTAMP WITH TIME ZONE,
  duration INTERVAL,
  PRIMARY KEY (id)
);

insert into offer(id, offer_id, start_time, duration) VALUES
  (1, 1, now(), '3 days'),
  (2, 1, now(), '1 day');


CREATE TABLE review(
  id bigserial not null,
  created bigint not null default 0,
  updated bigint not null default 0,
  location_id bigint REFERENCES location(id),
  trip_id bigint REFERENCES trip(id),
  user_id bigint not null,
  title varchar(64) not null,
  review_text text,
  rating real,
  primary key(id)
);

insert into review(id, location_id, user_id, created, title, rating, review_text)
VALUES (1, 1, 0, 0, 'No fog', '4.5', 'A 10,000 foot glacier climb on the slightly less popular east side of Mount Rainier (originally known as Tahoma). The hike to camp passes through Glacier Basin, site of mining activity up through the 1930''s. The route, including the climb up the Inter Glacier, can get icy by late July, increasing the difficulty.'),
  (2, 1, 0, 0, 'No fog', '3.4', 'Once upon a time there was a big fat fucking duck and it fell over and now nobody will ever love me again OK>???? LEABVE ME ND ME CAT ALONE. Need more text dsbfbsd fds fd afds ndsf ads fn asdfa sdijfjadsfjasd ');

CREATE TABLE image(
  id bigserial NOT NULL,
  created bigint not null default 0,
  updated bigint not null default 0,
  url varchar(256) NOT NULL,
  trip_id bigint,
  location_id bigint REFERENCES location(id),
  guide_id bigint REFERENCES guide(id),
  review_id bigint REFERENCES review(id),
  position bigint,
  PRIMARY KEY(id)
);

CREATE INDEX image_trip ON image(trip_id);
CREATE INDEX image_location_id ON image(location_id);
CREATE INDEX image_guide ON image(guide_id);

insert into image(url, location_id, review_id, guide_id)
VALUES
  ('http://www.backgroundbandit.com/wallpapers/31/700.jpg',                                       1,    1,    NULL),
  ('https://justinpluslauren.com/wp-content/gallery/niagara-falls-in-winter/20150125_150709.jpg', 1,    1,    NULL),
  ('https://justinpluslauren.com/wp-content/gallery/niagara-falls-in-winter/20150125_150709.jpg', 2,    NULL, NULL),
  ('https://justinpluslauren.com/wp-content/gallery/niagara-falls-in-winter/20150125_150709.jpg', 3,    NULL, NULL),
  ('https://www.rmiguides.com/assets/images/logo-badge-small.png',                                NULL, NULL, 1);

