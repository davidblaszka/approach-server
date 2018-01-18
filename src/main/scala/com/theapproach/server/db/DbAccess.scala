//
// Copyright (c) 2011-2017 by Curalate, Inc.
//

package com.theapproach.server.db

import com.google.inject.Inject
import com.theapproach.server.model.{LocationId, OfferId}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import slick.jdbc.JdbcProfile


class DbAccess @Inject()(val driver: JdbcProfile) {

  import driver.api._

  class ImageTable(tag: Tag) extends Table[ImageDAO](tag, "image") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def url = column[String]("url")

    def timeCreated = column[Long]("created")

    def offerId = column[Option[Long]]("offer_id")

    def locationId = column[Option[Long]]("location_id")

    def guideId = column[Option[Long]]("guide_id")

    def reviewId = column[Option[Long]]("review_id")

    def position = column[Option[Long]]("position")

    def * = (id, url, timeCreated, offerId, locationId, guideId, reviewId, position) <> (ImageDAO.tupled, ImageDAO.unapply)

    def location = foreignKey("location", locationId, locationQuery)(_.id)
    def guide = foreignKey("guide", guideId, guideQuery)(_.id)
    def review = foreignKey("review", reviewId, reviewQuery)(_.id)
  }

  class LocationTable(tag: Tag) extends Table[LocationDAO](tag, "location") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def parentLocationId = column[Option[Long]]("parent_location_id")

    def created = column[Long]("created")

    def modified = column[Long]("modified")

    def metadataId = column[Long]("metadata_id")

    def title = column[String]("title")

    def locationType = column[Long]("location_type")

    def zoneId = column[Option[Long]]("zone_id")

    def zoneName = column[Option[String]]("zone_name")

    def areaId = column[Option[Long]]("area_id")

    def areaName = column[Option[String]]("area_name")

    def regionId = column[Long]("region_id")

    def regionName = column[String]("region_name")

    def state = column[String]("state")

    def country = column[String]("country")

    def * = (id, parentLocationId, created, modified, metadataId, title, locationType, zoneId, zoneName, areaId, areaName, regionId, regionName, state, country) <> (LocationDAO.tupled, LocationDAO.unapply)
  }

  class ReviewTable(tag: Tag) extends Table[ReviewDAO](tag, "review") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def locationId = column[Option[Long]]("location_id")
    def userId = column[Long]("user_id")
    def created = column[Long]("created")
    def title = column[String]("title")
    def reviewText = column[Option[String]]("review_text")
    def rating = column[Option[Double]]("rating")

    def * = (id, locationId, userId, created, title, reviewText, rating) <> (ReviewDAO.tupled, ReviewDAO.unapply)
  }

  class GuideTable(tag: Tag) extends Table[GuideDAO](tag, "guide") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def created = column[Long]("created")
    def updated = column[Long]("updated")
    def name = column[String]("name")
    def location = column[Option[String]]("location")
    def aboutInfo = column[Option[String]]("about_info")

    def * = (id, created, updated, name, location, aboutInfo) <> (GuideDAO.tupled, GuideDAO.unapply)
  }

  class OfferTable(tag: Tag) extends Table[OfferDAO](tag, "offer") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def created = column[Long]("created")
    def updated = column[Long]("updated")
    def guideId = column[Long]("guide_id")
    def locationId = column[Long]("location_id")
    def heading = column[String]("heading")
    def description = column[Option[String]]("description")
    def itinerary = column[Option[String]]("itinerary")

    def * = (id, created, updated, guideId, locationId, heading, description, itinerary) <> (OfferDAO.tupled, OfferDAO.unapply)
  }

  protected lazy val imageQuery: TableQuery[ImageTable] = TableQuery[ImageTable]
  protected lazy val locationQuery: TableQuery[LocationTable] = TableQuery[LocationTable]
  protected lazy val reviewQuery: TableQuery[ReviewTable] = TableQuery[ReviewTable]
  protected lazy val guideQuery: TableQuery[GuideTable] = TableQuery[GuideTable]
  protected lazy val offerQuery: TableQuery[OfferTable] = TableQuery[OfferTable]
  protected lazy val db = Database.forConfig("database")

  protected val logger = org.slf4j.LoggerFactory.getLogger(getClass)

  def getLocationData(id: LocationId): Future[List[LocationAndImages]] = {
    logger.info("Starting getLocationData call")
    val action = for {
      locationResult <- locationQuery if locationResult.id === id.value || locationResult.parentLocationId === id.value
      imageResult <- imageQuery if imageResult.locationId === locationResult.id
    } yield (locationResult, imageResult)

    val query = action.result.map((rows: Seq[(LocationDAO, ImageDAO)]) => {
      val locationToImage = rows.groupBy(_._1).mapValues(_.map(_._2))

      locationToImage.map {
        case (locationResult, imageResults) => {
          LocationAndImages(
            location = locationResult,
            images = imageResults.toList
          )
        }
      }.toList
    })

    db.run(query)
  }

  def getReviewDataForLocation(id: LocationId): Future[List[LocationReviewResponse]] = {
    logger.info("Starting getReviewData call")

    val action = for {
      (reviews, images) <- reviewQuery.filter(_.locationId === id.value) joinLeft imageQuery on (_.id === _.reviewId)
    } yield (reviews, images)


    val query = action.result.map((rows: Seq[(ReviewDAO, Option[ImageDAO])]) => {
      val locationToImage: Map[ReviewDAO, Seq[ImageDAO]] = rows.groupBy(_._1).mapValues(_.flatMap(_._2))

      locationToImage.map {
        case (reviewDao, imageDaos) => {
          LocationReviewResponse(
            review = reviewDao,
            images = imageDaos.toList
          )
        }
      }.toList
    })

    db.run(query)
  }

  def getOfferData(offerId: OfferId): Future[Option[OfferPageDBResult]] = {
    logger.info("Starting getOfferData call")

    val action = for {
      offerResult <- offerQuery if offerResult.id === offerId.value
      guideResult <- guideQuery if guideResult.id === offerResult.guideId
      locationResult <- locationQuery if locationResult.id === offerResult.locationId
      imageResult <- imageQuery if imageResult.locationId === offerResult.id || imageResult.guideId === offerResult.guideId || imageResult.locationId === offerResult.locationId
    } yield (offerResult, guideResult, imageResult, locationResult)

    val query = action.result.map((rows: Seq[(OfferDAO, GuideDAO, ImageDAO, LocationDAO)]) => {
      val groupedOffers: Map[Long, Seq[(OfferDAO, GuideDAO, ImageDAO, LocationDAO)]] = rows.groupBy(_._1.id)

      val specificOfferOpt: Option[Seq[(OfferDAO, GuideDAO, ImageDAO, LocationDAO)]] = groupedOffers.keys.find(_ == offerId.value).flatMap(dao => groupedOffers.get(dao))

      specificOfferOpt.map(results => {
        val offerDAO = results.head._1
        val guideDAO = results.head._2
        val guideImage = results.find(_._3.guideId.exists(_ == guideDAO.id)).getOrElse(throw new Exception("guide has no image"))._3
        val offerImages = results.filter(_._3.offerId.exists(_ == offerId.value)).map(_._3)
        val locationDAO = results.head._4
        val locationImages = results.filter(_._3.locationId.exists(_ == locationDAO.id)).map(_._3)

        OfferPageDBResult(
          offer = offerDAO,
          guide = GuideAndImage(
            guide = guideDAO,
            image = guideImage
          ),
          offerImages = offerImages.toList,
          location = LocationAndImages(
            location = locationDAO,
            images = locationImages.toList
          )
        )
      })
    })

    db.run(query)
  }
}

case class OfferPageDBResult(
  offer: OfferDAO,
  guide: GuideAndImage,
  offerImages: List[ImageDAO],
  location: LocationAndImages
)

case class GuideAndImage(
  guide: GuideDAO,
  image: ImageDAO
)

case class LocationAndImages(
  location: LocationDAO,
  images: List[ImageDAO]
)