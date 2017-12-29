//
// Copyright (c) 2011-2017 by Curalate, Inc.
//

package com.theapproach.server.utils

class IterableForGroupableMap[S, T](t: Iterable[(S, T)]) {
  def toGroupedMap: Map[S, Iterable[T]] = {
    t.groupBy(_._1).mapValues(_.map(_._2))
  }
}