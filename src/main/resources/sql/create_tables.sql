
CREATE TABLE image(
  id bigserial NOT NULL,
  url varchar(256) NOT NULL,
  created bigint NOT NULL,
  offer_id bigint,
  route_id bigint,
  guide_id bigint,
  PRIMARY KEY(id)
);

CREATE INDEX image_offer ON image(offer_id);
CREATE INDEX image_route ON image(route_id);
CREATE INDEX image_guide ON image(guide_id);

CREATE TABLE route(
  id bigserial NOT NULL,
  location_id bigint,
  PRIMARY KEY(id)
);

insert into image(id, url, created, offer_id, route_id, guide_id)
VALUES
  (1, 'google.com', 0, NULL, 1, NULL),
  (2, 'google.com', 0, NULL, 2, NULL);

insert into route (id,  location_id)
VALUES
  (1, 33),
  (2, 34);