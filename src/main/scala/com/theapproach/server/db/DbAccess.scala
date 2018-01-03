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
  guideId: Option[Long]
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

class DbAccess @Inject()(val driver: JdbcProfile) {

  import driver.api._

  class ImageTable(tag: Tag) extends Table[ImageDAO](tag, "image") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def url = column[String]("url")

    def timeCreated = column[Long]("created")

    def offerId = column[Option[Long]]("offer_id")

    def locationId = column[Option[Long]]("location_id")

    def guideId = column[Option[Long]]("guide_id")

    def * = (id, url, timeCreated, offerId, locationId, guideId) <> (ImageDAO.tupled, ImageDAO.unapply)

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

  val imageQuery: TableQuery[ImageTable] = TableQuery[ImageTable]
  val locationQuery: TableQuery[LocationTable] = TableQuery[LocationTable]
  val db = Database.forConfig("database")


  def getDataForLocationPage(id: LocationId): Future[Option[RoutePageData]] = {
    val action = for {
      locationResult <- locationQuery if locationResult.id === id.value
      imageResult <- imageQuery if imageResult.locationId === locationResult.id
    } yield (locationResult, imageResult)

    val query = action.result.map((rows: Seq[(LocationDAO, ImageDAO)]) => {
      val imageDaos = rows.map(_._2)

      for {
        locationDao <- rows.headOption.map(_._1)
      } yield {
        RoutePageData(
          location = locationDao,
          images = imageDaos.toList
        )
      }
    })

    db.run(query)
  }
}

case class RoutePageData(
  location: LocationDAO,
  images: List[ImageDAO]
)

