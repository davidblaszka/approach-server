//
// Copyright (c) 2011-2017 by Curalate, Inc.
//

package com.theapproach.server.model

import org.joda.time.DateTime


case class ImageId(value: Long)
case class RouteId(value: Long)
case class LocationId(value: Long)
case class OfferId(value: Long)
case class GuideId(value: Long)

case class RouteRating(
  average: Double
)

case class RouteMetadata(
  rating: Option[RouteRating]
//   Grade???
//  distance...
)


case class Location(
  id: LocationId,
  title: String,
  zoneId: Option[LocationId],
  regionId: Option[LocationId]
)

case class Image(
  id: ImageId,
  url: String,
  created: DateTime,
  offerId: Option[OfferId],
  routeId: Option[RouteId],
  guideId: Option[GuideId]
)

case class Offer(
  id: OfferId,
  guideId: GuideId,
  created: DateTime,
  updated: DateTime,
  startTime: DateTime,
  endTime: DateTime,
  heading: String,
  price: Option[String]
)

case class Guide(
  id: GuideId
)

case class Route(
  id: RouteId,
  locationId: LocationId
)
