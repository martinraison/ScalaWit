package com.scalawit

import dispatch._
import play.api.libs.json.{Writes, Json, Reads}
import dispatch.Req
import scala.concurrent.ExecutionContext.Implicits.global

class Client(token: String) {
  val witHost = host(Config.DOMAIN)
  private val headers = Map(
    "Content-type" -> "application/json",
    "Authorization" -> s"Bearer ${token}",
    "Accept" -> s"application/vnd.wit.${Config.API_VERSION}"
  )

  def httpGet[T](path: Seq[String], params: Map[String, String] = Map())(implicit reads: Reads[T]): Future[Either[WitError, T]] = {
    val request = makeUrl(path) <<? params <:< headers secure
    val resultFut = Http(request OK as.String).either
    resultFut.map {
      _.fold(ex => Left(WitError.getWitError(ex)),
        content => Json.parse(content).asOpt[T].map(Right(_)).getOrElse(Left(WitResponseParsingError(content))))
    }
  }

  def httpPost[T](path: Seq[String], data: T)(implicit writes: Writes[T]): Future[Either[WitError, String]] = {
    httpAction(makeUrl(path).POST << Json.toJson(data).toString)
  }

  def httpPut[T](path: Seq[String], data: T)(implicit writes: Writes[T]): Future[Either[WitError, String]] = {
    httpAction(makeUrl(path).PUT << Json.toJson(data).toString)
  }

  def httpDelete(path: Seq[String]): Future[Either[WitError, String]] = {
    httpAction(makeUrl(path).DELETE)
  }

  private def httpAction[T](request: Req): Future[Either[WitError, String]] = {
    val completeRequest = request <:< headers secure
    val resultFut = Http(completeRequest OK as.String).either
    resultFut.map { _.fold(ex => Left(WitError.getWitError(ex)), Right(_)) }
  }

  private def makeUrl(path: Seq[String]) = path.foldLeft(witHost)(_ / _)

}
