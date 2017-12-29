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

  def getRouteAndAssociatedData(id: RouteId): Future[Option[RoutePageResult]] = {
    db.getDataForRoutePage(id).map(_.map(data => {

      RoutePageResult(
        route = RouteConversions.fromDAO(data.route),
        images = data.images.map(ImageConversions.fromDAO)
      )
    }))
  }
}

case class RoutePageResult(
  route: Route,
  images: List[Image],
//  locationData: Location, // optional???
//  zoneData: Option[Location],
//  routeMetadata: RouteMetadata,
//  offers: List[Offer]
)