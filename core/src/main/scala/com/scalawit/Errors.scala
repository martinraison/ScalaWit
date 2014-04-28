package com.scalawit

import dispatch.StatusCode

sealed abstract class WitError
case class WitMalformedRequestError(message: String) extends WitError
case class WitAuthenticationError(message: String) extends WitError
case class WitRequestTimeoutError(message: String) extends WitError
case class WitResourceNotFoundError(message: String) extends WitError
case class WitBadRequestError(message: String) extends WitError
case class WitServerSideError(message: String) extends WitError
case class WitResponseParsingError(content: String) extends WitError {
  override def toString = "Could not parse the following content: " + content
}

object WitError {
  def getWitError(ex: Throwable) = ex match {
      case StatusCode(400) => WitMalformedRequestError(ex.getMessage)
      case StatusCode(401) => WitAuthenticationError(ex.getMessage)
      case StatusCode(404) => WitResourceNotFoundError(ex.getMessage)
      case StatusCode(408) => WitRequestTimeoutError(ex.getMessage)
      case StatusCode(500) => WitServerSideError(ex.getMessage)
      case StatusCode(508) => WitServerSideError(ex.getMessage)
      case _ => WitBadRequestError(ex.getMessage)
  }
}
