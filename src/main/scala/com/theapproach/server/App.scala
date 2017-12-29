package com.theapproach.server

import com.google.inject.{Inject, Module}
import com.theapproach.server.api.RouteApi
import com.theapproach.server.db.TheApproachDb
import com.theapproach.server.model.RouteId
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
  api: RouteApi
) extends Controller {

  get("/hello") { request: Request =>
    "Fitman says hello"
  }

  get("/route_page/:id") { request: Request =>
    val routeId = RouteId(request.params("id").toLong)

    logger.debug(s"Called route_page/${routeId.value}")

    api.getRouteAndAssociatedData(routeId)
  }

}
