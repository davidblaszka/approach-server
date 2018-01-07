//
// Copyright (c) 2011-2017 by Curalate, Inc.
//

package com.theapproach.server.db

import com.google.inject.Inject
import com.theapproach.server.model.LocationId
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import slick.jdbc.JdbcProfile

case class ImageDAO(
  id: Long,
  url: String,
  created: Long,
  offerId: Option[Long],
  locationId: Option[Long],
  guideId: Option[Long],
  reviewId: Option[Long],
  position: Option[Long]
)

case class LocationDAO(
  id: Long,
  parentLocationId: Option[Long],
  created: Long,
  modified: Long,
  metadataId: Long,
  title: String,
  locationType: Long,
  zoneId: Option[Long],
  zoneName: Option[String],
  areaId: Option[Long],
  areaName: Option[String],
  regionId: Long,
  regionName: String,
  state: String,
  country: String
)

case class ReviewDAO(
  id: Long,
  locationId: Option[Long],
  user_id: Long,
  created: Long,
  title: String,
  reviewText: Option[String],
  rating: Option[Double]
)

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

    def route = foreignKey("location", locationId, locationQuery)(_.id)
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

  protected lazy val imageQuery: TableQuery[ImageTable] = TableQuery[ImageTable]
  protected lazy val locationQuery: TableQuery[LocationTable] = TableQuery[LocationTable]
  protected lazy val reviewQuery: TableQuery[ReviewTable] = TableQuery[ReviewTable]
  protected lazy val db = Database.forConfig("database")


  def getLocationData(id: LocationId): Future[List[LocationAndImage]] = {
    val action = for {
      locationResult <- locationQuery if locationResult.id === id.value || locationResult.parentLocationId === id.value
      imageResult <- imageQuery if imageResult.locationId === locationResult.id
    } yield (locationResult, imageResult)

    val query = action.result.map((rows: Seq[(LocationDAO, ImageDAO)]) => {
      val locationToImage = rows.groupBy(_._1).mapValues(_.map(_._2))

      locationToImage.map {
        case (locationResult, imageResults) => {
          LocationAndImage(
            location = locationResult,
            images = imageResults.toList
          )
        }
      }.toList
    })

    db.run(query)
  }

  def getReviewDataForLocation(id: LocationId): Future[List[LocationReviewResponse]] = {
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

  def getSingle(): Future[Any] = {
    val q = reviewQuery.filter(_.id === 1L).result

    db.run(q)
  }
}

case class LocationAndImage(
  location: LocationDAO,
  images: List[ImageDAO]
)