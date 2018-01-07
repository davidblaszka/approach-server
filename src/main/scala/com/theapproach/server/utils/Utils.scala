//
// Copyright (c) 2011-2017 by Curalate, Inc.
//

package com.theapproach.server.utils

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class IterableForGroupableMap[S, T](t: Iterable[(S, T)]) {
  def toGroupedMap: Map[S, Iterable[T]] = {
    t.groupBy(_._1).mapValues(_.map(_._2))
  }
}


object Timing {
  protected val logger = org.slf4j.LoggerFactory.getLogger(getClass)

  def latency[T](key: String, future: Future[T]): Future[T] = {
    val start = System.currentTimeMillis()
    future.onComplete({
      case _ => logger.info(s"$key took ${System.currentTimeMillis() - start} ms")
    })
    future
  }
}