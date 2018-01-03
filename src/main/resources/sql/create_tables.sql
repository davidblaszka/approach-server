

CREATE TABLE location(
  id BIGSERIAL not null,
  parent_location_id bigint,
  created bigint not null,
  modified bigint not null ,
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

CREATE INDEX location_zone ON location(zone_id);
CREATE INDEX location_area ON location(area_id);
CREATE INDEX location_region ON location(region_id);

insert into location (title, region_id, region_name, location_type, metadata_id, created, modified, state, country)
VALUES
  ('Emmons-Winthrop Glacier', 1, 'Mount Rainier National Park', 0, 0, 0, 0, 'CA', 'USA');

CREATE TABLE image(
  id bigserial NOT NULL,
  url varchar(256) NOT NULL,
  created bigint NOT NULL,
  offer_id bigint,
  location_id bigint,
  guide_id bigint,
  PRIMARY KEY(id)
);

CREATE INDEX image_offer ON image(offer_id);
CREATE INDEX image_location_id ON image(location_id);
CREATE INDEX image_guide ON image(guide_id);

insert into image(url, created, location_id)
VALUES
  ('http://www.backgroundbandit.com/wallpapers/31/700.jpg', 0, 1),
  ('https://justinpluslauren.com/wp-content/gallery/niagara-falls-in-winter/20150125_150709.jpg', 0, 2);
