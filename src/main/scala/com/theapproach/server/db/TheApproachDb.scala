//
// Copyright (c) 2011-2017 by Curalate, Inc.
//

package com.theapproach.server.db

import slick.jdbc.H2Profile.api._
import scala.concurrent.ExecutionContext.Implicits.global
import com.google.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import slick.jdbc.JdbcProfile

@Singleton
class TheApproachDb @Inject()(val driver: JdbcProfile) {

  import driver.api._

  class Users(tag: Tag) extends Table[(Int, String)](tag, "users") {
    def id = column[Int]("id")
    def username = column[String]("username")
    def * = (id, username)
  }

  val users: TableQuery[Users] = TableQuery[Users]
  val db = Database.forConfig("database")

  def getUsers(): Future[Seq[String]] = {
    val query = users.map(_.username).result
    db.run(query)
  }
}
