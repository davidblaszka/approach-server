//
// Copyright (c) 2011-2017 by Curalate, Inc.
//

package com.theapproach.server.model

import com.theapproach.server.db.{ImageDAO, LocationDAO}
import org.joda.time.{DateTime, DateTimeZone}

object ImageConversions {

  def fromDAO(dao: ImageDAO): Image = {
    Image(
      id = ImageId(dao.id),
      url = dao.url,
      created = new DateTime(dao.created, DateTimeZone.UTC),
      offerId = dao.offerId.map(OfferId),
      locationId = dao.locationId.map(LocationId),
      guideId = dao.guideId.map(GuideId)
    )
  }
}

object LocationConversions {
  def fromDAO(dao: LocationDAO): Location = {
    Location(
      id = LocationId(dao.id),
      created = new DateTime(dao.created, DateTimeZone.UTC),
      modified = new DateTime(dao.modified, DateTimeZone.UTC),
      metadataId = LocationMetadataId(dao.metadataId),
      title = dao.title,
      locationType = dao.locationType,
      zoneId = dao.zoneId.map(LocationId),
      zoneName = dao.zoneName,
      areaId = dao.areaId.map(LocationId),
      areaName = dao.areaName,
      regionId = LocationId(dao.regionId),
      regionName = dao.regionName,
      state = State(dao.state),
      country = Country(dao.country)
    )
  }
}