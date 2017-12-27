//
// Copyright (c) 2011-2017 by Curalate, Inc.
//

package com.theapproach.server.modules

import com.google.inject.{Module, Provides, Singleton}
import com.twitter.inject.TwitterModule
import scala.concurrent.ExecutionContext
import slick.jdbc.{H2Profile, JdbcProfile, PostgresProfile}

object Modules {
  def apply()(implicit executionContext: ExecutionContext): List[Module] = List(
    DbModules
  )
}

object DbModules extends TwitterModule {

  @Singleton
  @Provides
  def jdbcDriver(): JdbcProfile = PostgresProfile
}