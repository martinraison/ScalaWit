package com.scalawit

import dispatch._
import play.api.libs.json._
import javax.sound.sampled._
import scala.text.Document

class Wit(token: String) {

  private val client = new Client(token)

  def getMessage(message: String, id: Option[String] = None, context: Option[WitContext] = None): Future[Either[WitError, WitMessage]] = {
    client.httpGet[WitMessage](Seq("message"), paramsForGetMessage(message, id, context))
  }

  def getMessageRaw(message: String, id: Option[String] = None, context: Option[WitContext] = None): Future[Either[WitError, String]] = {
    client.httpGet[String](Seq("message"), paramsForGetMessage(message, id, context))
  }

  def postSpeech(stream: AudioInputStream): Future[Either[WitError, String]] = {
    client.httpPostAudio(stream)
  }

  def getMessageById(id: String): Future[Either[WitError, WitMessage]] = {
    client.httpGet[WitMessage](Seq("messages", id))
  }

  def getMessageByIdRaw(id: String): Future[Either[WitError, String]] = {
    client.httpGet[String](Seq("messages", id))
  }

  def getIntents: Future[Either[WitError, Seq[WitIntent]]] = {
    client.httpGet[Seq[WitIntent]](Seq("intents"))
  }

  def getIntentsRaw: Future[Either[WitError, String]] = {
    client.httpGet[String](Seq("intents"))
  }

  // todo: check return type
  def getCorpus: Future[Either[WitError, Seq[WitExpression]]] = {
    client.httpGet[Seq[WitExpression]](Seq("corpus"))
  }

  def getCorpusRaw: Future[Either[WitError, String]] = {
    client.httpGet[String](Seq("corpus"))
  }

  def getEntities: Future[Either[WitError, Seq[String]]] = {
    client.httpGet[Seq[String]](Seq("entities"))
  }

  def getEntitiesRaw: Future[Either[WitError, String]] = {
    client.httpGet[String](Seq("entities"))
  }

  def getEntity(id: String): Future[Either[WitError, WitEntityDescription]] = {
    client.httpGet[WitEntityDescription](Seq("entities", id))
  }

  def getEntityRaw(id: String): Future[Either[WitError, String]] = {
    client.httpGet[String](Seq("entities", id))
  }

  def postEntity(entityDescription: WitEntityDescription): Future[Either[WitError, String]] = {
    client.httpPost[WitEntityDescription](Seq("entities"), entityDescription)
  }

  def putEntity(entityDescription: WitEntityDescription): Future[Either[WitError, String]] = {
    client.httpPut[WitEntityDescription](Seq("entities", entityDescription.id), entityDescription)
  }

  def deleteEntity(entityId: String): Future[Either[WitError, String]] = {
    client.httpDelete(Seq("entities", entityId))
  }

  def postValue(entityId: String, value: WitEntityValue): Future[Either[WitError, String]] = {
    client.httpPost[WitEntityValue](Seq("entities", entityId, "values"), value)
  }

  def deleteValue(entityId: String, value: String): Future[Either[WitError, String]] = {
    client.httpDelete(Seq("entities", entityId, "values", value))
  }

  def postExpression(entityId: String, value: String, expression: String): Future[Either[WitError, String]] = {
    client.httpPost[WitExpressionDescription](Seq("entities", entityId, "values", value), WitExpressionDescription(expression))
  }

  def deleteExpression(entityId: String, value: String, expression: String): Future[Either[WitError, String]] = {
    client.httpDelete(Seq("entities", entityId, "values", value, "expressions", expression))
  }

  private def paramsForGetMessage(message: String, id: Option[String], context: Option[WitContext]) = {
    val paramOpts = Seq(
      Some(("q",message)),
      id map { value => ("msg_id", value)},
      context map { context => ("context", Json.toJson(context).toString) }
    )
    paramOpts.flatten.foldLeft(Map.empty[String, String])(_ + _)
  }
  // todo: doc
}

object Wit {
  // Pretty printing
  def pretty[T](obj: Printable[T]) = obj.pretty
  implicit def pretty(obj: Seq[Printable[_]]) = Printable.pretty(Printable.getDocumentForSeq(obj))
}
