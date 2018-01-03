//
// Copyright (c) 2011-2017 by Curalate, Inc.
//

package com.theapproach.server.api

import com.google.inject.Inject
import com.theapproach.server.db._
import com.theapproach.server.model._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class RouteApi @Inject()(
  db: DbAccess
) {

  def getLocationAndAssociatedData(id: LocationId): Future[Option[LocationPageResult]] = {
    db.getDataForLocationPage(id).map(_.map(data => {

      LocationPageResult(
        location = LocationConversions.fromDAO(data.location),
        images = data.images.map(ImageConversions.fromDAO)
      )
    }))
  }
}

case class LocationPageResult(
  location: Location,
  images: List[Image]
//  zoneData: Option[Location],
//  routeMetadata: RouteMetadata,
//  offers: List[Offer]
)