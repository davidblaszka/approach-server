//
// Copyright (c) 2011-2017 by Curalate, Inc.
//

package com.theapproach.server.api

import com.google.inject.Inject
import com.theapproach.server.db._
import com.theapproach.server.model._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object LocationApi {
  def selectSubLocationData(primaryId: LocationId, allLocations: List[LocationAndImage]): List[SubLocationResult] = {
    allLocations.filter(_.location.id != primaryId.value).map(locationData => {
      SubLocationResult(
        LocationConversions.fromDAO(locationData.location),
        ImageConversions.fromDAO(locationData.images.headOption.getOrElse(
          // could return a default image here?
          throw new Exception(s"no image for location ${locationData.location.id}")
        ))
      )
    })
  }
}

class LocationApi @Inject()(
  db: DbAccess
) {
  import LocationApi._

  def getLocationAndAssociatedData(id: LocationId): Future[Option[LocationPageResult]] = {
    for {
      locationPageData <- db.getLocationData(id)
    } yield {
      val primaryPageOpt = locationPageData.find(_.location.id == id.value)

      primaryPageOpt.map(primaryPage => {
        LocationPageResult(
          location = LocationConversions.fromDAO(primaryPage.location),
          images = primaryPage.images.map(ImageConversions.fromDAO),
          subLocations = selectSubLocationData(id, locationPageData)
        )
      })
    }
  }
}

case class LocationPageResult(
  location: Location,
  images: List[Image],
  subLocations: List[SubLocationResult]
//  zoneData: Option[Location],
//  routeMetadata: RouteMetadata,
//  offers: List[Offer]
)

case class SubLocationResult(
  location: Location,
  image: Image
)