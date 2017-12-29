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

  def getRouteAndAssociatedData(id: RouteId): Future[RoutePageResultData] = {
    db.getDataForRoutePage(id).map((data: Map[RouteDAO, Seq[ImageDAO]]) => {

      ???
//      RoutePageResultData()
    })
  }
}

case class RoutePageResultData(
  route: Route,
  images: List[Image],
  title: String,
  locationData: Location, // optional???
  zoneData: Option[Location],
  routeMetadata: RouteMetadata,
  offers: List[Offer]
)