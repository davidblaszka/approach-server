//
// Copyright (c) 2011-2017 by Curalate, Inc.
//

package com.theapproach.server.model

import org.joda.time.DateTime


case class ImageId(value: Long)
case class LocationId(value: Long)
case class LocationMetadataId(value: Long)
case class OfferId(value: Long)
case class GuideId(value: Long)

case class Rating(
  average: Double
)

case class State(
  name: String
)

case class Country(
  name: String
)

case class LocationMetadata(
  id: Long,
  rating: Option[Rating]
//   Grade???
//  distance...
)

case class Location(
  id: LocationId,
  created: DateTime,
  modified: DateTime,
  metadataId: LocationMetadataId,
  title: String,
  locationType: Long,
  zoneId: Option[LocationId],
  zoneName: Option[String],
  areaId: Option[LocationId],
  areaName: Option[String],
  regionId: LocationId,
  regionName: String,
  state: State,
  country: Country
)

// Metadata
//    million columns
//    ui does the work of untangling
      // if type==climp... show some rollup

case class Image(
  id: ImageId,
  url: String,
  created: DateTime,
  offerId: Option[OfferId],
  locationId: Option[LocationId],
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
