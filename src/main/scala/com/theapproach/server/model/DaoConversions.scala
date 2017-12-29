//
// Copyright (c) 2011-2017 by Curalate, Inc.
//

package com.theapproach.server.model

import com.theapproach.server.db.{ImageDAO, RouteDAO}
import org.joda.time.{DateTime, DateTimeZone}

object ImageConversions {

  def fromDAO(dao: ImageDAO): Image = {
    Image(
      id = ImageId(dao.id),
      url = dao.url,
      created = new DateTime(dao.created, DateTimeZone.UTC),
      offerId = dao.offerId.map(OfferId),
      routeId = dao.routeId.map(RouteId),
      guideId = dao.guideId.map(GuideId)
    )
  }

}


object RouteConversions {


  def fromDAO(dao: RouteDAO): Route = {
    Route(
      RouteId(dao.id),
      locationId = LocationId(dao.locationId),
      title = dao.title
    )
  }
}