package com.theapproach.server

import com.google.inject.{Inject, Module}
import com.theapproach.server.api.{LocationApi, TripApi}
import com.theapproach.server.model.{LocationId, TripId}
import com.theapproach.server.modules.Modules
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.filters.{CommonFilters, LoggingMDCFilter, TraceIdMDCFilter}
import com.twitter.finatra.http.routing.HttpRouter
import com.twitter.finatra.http.{Controller, HttpServer}
import com.twitter.util.{Future => TwitterFuture}
import io.paradoxical.finatra.HttpServiceBase
import io.paradoxical.finatra.swagger.ApiDocumentationConfig
import scala.concurrent.ExecutionContext.Implicits.global
import com.theapproach.server.utils.Timing.latency

object App extends ApproachServer {}

class ApproachServer extends HttpServiceBase {

  override def documentation = new ApiDocumentationConfig {
    override val description: String = "Sample"
    override val title: String = "API"
    override val version: String = "1.0"
  }

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
  locationApi: LocationApi,
  tripApi: TripApi
) extends Controller {

  get("/location_page/:id") { request: Request =>
    val locationId = LocationId(request.params("id").toLong)

    logger.info(s"Called location_page/${locationId.value}")

    latency("location_page/${locationId.value}", locationApi.getLocationAndAssociatedData(locationId))
  }

  get("/trip_page/:id") { request: Request =>
    val tripId = TripId(request.params("id").toLong)

    logger.info(s"Called trip_page/${tripId.value}")

    latency(s"trip_page/${tripId.value}", tripApi.getTripPageData(tripId))

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
