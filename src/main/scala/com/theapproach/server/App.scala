package com.theapproach.server

import com.google.inject.{Inject, Module}
import com.theapproach.server.api.RouteApi
import com.theapproach.server.db.TheApproachDb
import com.theapproach.server.model.RouteId
import com.theapproach.server.modules.Modules
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.filters.{CommonFilters, LoggingMDCFilter, TraceIdMDCFilter}
import com.twitter.finatra.http.routing.HttpRouter
import com.twitter.finatra.http.{Controller, HttpServer}
import com.twitter.util.{Future => TwitterFuture}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object App extends ApproachServer {}

class ApproachServer extends HttpServer {

  override def modules: Seq[Module] = Modules()

  override protected def configureHttp(router: HttpRouter): Unit = {

    router
      .filter[CommonFilters]
      .filter[LoggingMDCFilter[Request, Response]]
      .filter[TraceIdMDCFilter[Request, Response]]
      .add[CorsFilter, ApproachController]
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

import com.twitter.finagle.{Service, SimpleFilter}

class CorsFilter extends SimpleFilter[Request, Response] {
  override def apply(request: Request, service: Service[Request, Response]): TwitterFuture[Response] = {
// TODO major security hole
    service(request).map {
      response =>
        response.headerMap
          .add("access-control-allow-origin", "*")
          .add("access-control-allow-headers", "accept, content-type")
          .add("access-control-allow-methods", "GET")

        response
    }
  }
}
