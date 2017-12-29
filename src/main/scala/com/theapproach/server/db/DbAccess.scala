//
// Copyright (c) 2011-2017 by Curalate, Inc.
//

package com.theapproach.server.db

import com.google.inject.Inject
import com.theapproach.server.model.RouteId
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import slick.jdbc.JdbcProfile

case class ImageDAO(
  id: Long,
  url: String,
  created: Long,
  offerId: Option[Long],
  routeId: Option[Long],
  guideId: Option[Long]
)

case class RouteDAO(
  id: Long,
  locationId: Long,
  title: String
)

class IterableForGroupableMap[S, T](t: Iterable[(S, T)]) {
  def toGroupedMap: Map[S, Iterable[T]] = {
    t.groupBy(_._1).mapValues(_.map(_._2))
  }
}


class DbAccess @Inject()(val driver: JdbcProfile) {

  import driver.api._

  class ImageTable(tag: Tag) extends Table[ImageDAO](tag, "image") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def url = column[String]("url")

    def timeCreated = column[Long]("created")

    def offerId = column[Option[Long]]("offer_id")

    def routeId = column[Option[Long]]("route_id")

    def guideId = column[Option[Long]]("guide_id")

    def * = (id, url, timeCreated, offerId, routeId, guideId) <> (ImageDAO.tupled, ImageDAO.unapply)

    def route = foreignKey("route", routeId, routes)(_.id)
  }

  class RouteTable(tag: Tag) extends Table[RouteDAO](tag, "route") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def locationId = column[Long]("location_id")

    def title = column[String]("title")

    def * = (id, locationId, title) <> (RouteDAO.tupled, RouteDAO.unapply)
  }

  val routes: TableQuery[RouteTable] = TableQuery[RouteTable]
  val images: TableQuery[ImageTable] = TableQuery[ImageTable]
  val db = Database.forConfig("database")

  def getRouteById(id: RouteId): Future[Option[RouteDAO]] = {
    val query = routes.filter(_.id === id.value).result
    db.run(query).map(
      _.headOption
    )
  }

  def getDataForRoutePage(id: RouteId): Future[Option[RoutePageData]] = {
    val query = routes
      .filter(_.id === id.value)
      .join(images).on(_.id === _.routeId)
      .result

    val action = for {
      queryResult <- query
    } yield {
      queryResult.groupBy(_._1).map {
        case (route: RouteDAO, associatedData: Seq[(RouteDAO, ImageDAO)]) => {
          val images = associatedData.map(_._2)

          RoutePageData(
            route = route,
            images = images.toList
          )
        }
      }.headOption
    }

    db.run(action)
  }
}

case class RoutePageData(
  route: RouteDAO,
  images: List[ImageDAO]
)

