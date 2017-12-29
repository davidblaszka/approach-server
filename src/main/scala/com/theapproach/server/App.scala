package com.theapproach.server

import com.google.inject.{Inject, Module}
import com.theapproach.server.db.TheApproachDb
import com.theapproach.server.modules.Modules
import com.twitter.finagle.http.Request
import com.twitter.finatra.http.routing.HttpRouter
import com.twitter.finatra.http.{Controller, HttpServer}
import com.twitter.util.{Future => TwitterFuture}
import scala.concurrent.ExecutionContext.Implicits.global

object App extends ApproachServer {}

class ApproachServer extends HttpServer {

  override def modules: Seq[Module] = Modules()

  override protected def configureHttp(router: HttpRouter): Unit = {
    val controller = injector.instance(classOf[ApproachController])

    router.add(controller)
  }
}

class ApproachController @Inject()(
  db: TheApproachDb
) extends Controller {

  get("/hello") { request: Request =>
    "Fitman says hello"
  }

  get("/users") { request: Request =>
    db.getUsers()
  }

  get("/route_page/:id") { request: Request =>
    db.getUsers()
  }

}
