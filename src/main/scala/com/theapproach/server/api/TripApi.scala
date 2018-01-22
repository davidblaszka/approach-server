//
// Copyright (c) 2011-2018 by Curalate, Inc.
//

package com.theapproach.server.api

import com.google.inject.Inject
import com.theapproach.server.db._
import com.theapproach.server.model._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class TripApi @Inject()(
  db: DbAccess
) {

  def getTripPageData(id: TripId): Future[Option[TripPageData]] = {
    db.getTripData(id).map(_.map(result => {
      TripPageData(
        trip = TripConversions.fromDAO(result.trip),
        tripImages = result.tripImages.map(ImageConversions.fromDAO),
        guideData = GuideDataForTripPage(
          GuideConversions.fromDAO(result.guide.guide),
          ImageConversions.fromDAO(result.guide.image)
        ),
        locationData = LocationDataForTripPage(
          location = LocationConversions.fromDAO(result.location.location),
          images = result.location.images.map(ImageConversions.fromDAO)
        )
      )
    }))
  }
}

case class GuideDataForTripPage(
  guide: Guide,
  image: Image
)

case class LocationDataForTripPage(
  location: Location,
  images: List[Image]
)

case class TripPageData(
  trip: Trip,
  tripImages: List[Image],
  guideData: GuideDataForTripPage,
  locationData: LocationDataForTripPage
)
