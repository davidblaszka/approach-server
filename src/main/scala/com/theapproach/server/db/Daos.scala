//
// Copyright (c) 2011-2018 by Curalate, Inc.
//

package com.theapproach.server.db


case class ImageDAO(
  id: Long,
  url: String,
  created: Long,
  offerId: Option[Long],
  locationId: Option[Long],
  guideId: Option[Long],
  reviewId: Option[Long],
  position: Option[Long]
)

case class LocationDAO(
  id: Long,
  parentLocationId: Option[Long],
  created: Long,
  modified: Long,
  metadataId: Long,
  title: String,
  locationType: Long,
  zoneId: Option[Long],
  zoneName: Option[String],
  areaId: Option[Long],
  areaName: Option[String],
  regionId: Long,
  regionName: String,
  state: String,
  country: String
)

case class ReviewDAO(
  id: Long,
  locationId: Option[Long],
  userId: Long,
  created: Long,
  title: String,
  reviewText: Option[String],
  rating: Option[Double]
)

case class GuideDAO(
  id: Long,
  created: Long,
  updated: Long,
  name: String,
  location: Option[String],
  aboutInfo: Option[String]
)

case class OfferDAO(
  id: Long,
  created: Long,
  updated: Long,
  guideId: Long,
  locationId: Long,
  heading: String,
  description: Option[String],
  itinerary: Option[String]
)