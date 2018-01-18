//
// Copyright (c) 2011-2017 by Curalate, Inc.
//

package com.theapproach.server.model

import org.joda.time.DateTime
import io.paradoxical.global.tiny._

case class ImageId(value: Long) extends LongValue
case class LocationId(value: Long) extends LongValue
case class LocationMetadataId(value: Long) extends LongValue
case class OfferId(value: Long) extends LongValue
case class GuideId(value: Long) extends LongValue

case class Rating(
  average: Double
)

case class State(
  value: String
) extends StringValue

case class Country(
  value: String
) extends StringValue

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
  created: DateTime,
  url: String,
  offerId: Option[OfferId],
  locationId: Option[LocationId],
  guideId: Option[GuideId],
)

case class Offer(
  id: OfferId,
  created: DateTime,
  updated: DateTime,
  guideId: GuideId,
  locationId: LocationId,
  heading: String,
  description: Option[String],
  itinerary: Option[String]
)

case class Guide(
  id: GuideId,
  created: DateTime,
  updated: DateTime,
  name: String,
  location: Option[String],
  aboutInfo: Option[String]
)
