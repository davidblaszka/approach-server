//
// Copyright (c) 2011-2018 by Curalate, Inc.
//

package com.theapproach.server.api

import com.google.inject.Inject
import com.theapproach.server.db._
import com.theapproach.server.model._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class OfferApi @Inject()(
  db: DbAccess
) {

  def getOfferPageData(id: OfferId): Future[Option[OfferPageData]] = {
    db.getOfferData(id).map(_.map(result => {
      OfferPageData(
        offer = OfferConversions.fromDAO(result.offer),
        offerImages = result.offerImages.map(ImageConversions.fromDAO),
        guideData = GuideDataForOfferPage(
          GuideConversions.fromDAO(result.guide.guide),
          ImageConversions.fromDAO(result.guide.image)
        ),
        locationData = LocationDataForOfferPage(
          location = LocationConversions.fromDAO(result.location.location),
          images = result.location.images.map(ImageConversions.fromDAO)
        )
      )
    }))
  }
}

case class GuideDataForOfferPage(
  guide: Guide,
  image: Image
)

case class LocationDataForOfferPage(
  location: Location,
  images: List[Image]
)

case class OfferPageData(
  offer: Offer,
  offerImages: List[Image],
  guideData: GuideDataForOfferPage,
  locationData: LocationDataForOfferPage
)
