//
// Copyright (c) 2011-2018 by Curalate, Inc.
//

package com.theapproach.server.db

import com.google.inject.Inject
import com.theapproach.server.model.LocationId
import scala.concurrent.Future
import slick.jdbc.JdbcProfile
import com.google.inject.Inject
import com.theapproach.server.model.LocationId
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import slick.jdbc.JdbcProfile

class ReviewDb @Inject()(
  sqlDriver: JdbcProfile,
) extends DbAccess(sqlDriver) {
  import sqlDriver.api._

//  lazy val db2 = Database.forConfig("database")
//
//
//  def getSingle(): Future[Any] = {
//    val q = reviewQuery.filter(_.id === 1L).result
//
//    db2.run(q)
//  }
//
//  def getReviewDataForLocation(id: LocationId): Future[List[LocationReviewResponse]] = {
//    val action = for {
//      (reviews, images) <- reviewQuery.filter(_.locationId === id.value) joinLeft imageQuery on (_.id === _.reviewId)
//    } yield (reviews, images)
//
//
//    val query = action.result.map((rows: Seq[(ReviewDAO, Option[ImageDAO])]) => {
//      val locationToImage: Map[ReviewDAO, Seq[ImageDAO]] = rows.groupBy(_._1).mapValues(_.flatMap(_._2))
//
//      locationToImage.map {
//        case (reviewDao, imageDaos) => {
//          LocationReviewResponse(
//            review = reviewDao,
//            images = imageDaos.toList
//          )
//        }
//      }.toList
//    })
//
//    db.run(query)
//  }
}

case class LocationReviewResponse(
  review: ReviewDAO,
  images: List[ImageDAO]
)