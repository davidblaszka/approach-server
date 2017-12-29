//
// Copyright (c) 2011-2017 by Curalate, Inc.
//

package com.theapproach.server.db

import com.google.inject.Inject
import scala.concurrent.Future
import slick.jdbc.JdbcProfile


class LocationDb @Inject()(val driver: JdbcProfile) {

  import driver.api._

  class Locations(tag: Tag) extends Table[(Int, String)](tag, "users") {
    def id = column[Int]("id")
    def username = column[String]("username")
    def * = (id, username)
  }

  val users: TableQuery[Locations] = TableQuery[Locations]
  val db = Database.forConfig("database")

  def getUsers(): Future[Seq[String]] = {
    val query = users.map(_.username).result
    db.run(query)
  }
}

