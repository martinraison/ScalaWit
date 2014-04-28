package com.scalawit

import org.joda.time._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import org.joda.time.format.ISODateTimeFormat

case class WitContext(
  states: Option[Seq[String]] = None,
  referenceTime: Option[DateTime] = Some(new DateTime()),
  timezone: Option[String] = None,
  entities: Option[Seq[WitEntityDescription]] = None) extends Printable[WitContext] {
  override def getMappable = implicitly[Mappable[WitContext]]
  override def getLabel: String = "Context"
}

object WitContext {
  implicit val writesWitContext: Writes[WitContext] = (
    (__ \ 'states).writeNullable[Seq[String]] and
    (__ \ 'reference_time).writeNullable[DateTime] and
    (__ \ 'timezone).writeNullable[String] and
    (__ \ 'entities).writeNullable[Seq[WitEntityDescription]]
    )(unlift(WitContext.unapply _))
}

case class WitMessage(
  msgId: String,
  msgBody: String,
  outcome: Option[WitOutcome]) extends Printable[WitMessage] {
  override def getMappable = implicitly[Mappable[WitMessage]]
  override def getLabel: String = "Message"
}

object WitMessage {
  implicit val readsWitMessage: Reads[WitMessage] = (
    (__ \ 'msg_id).read[String] and
    (__ \ 'msg_body).read[String] and
    (__ \ 'outcome).readNullable[WitOutcome]
    )(WitMessage.apply _)
}

case class WitOutcome(
  intent: String,
  entities: Seq[WitEntity],
  confidence: Double) extends Printable[WitOutcome] {
  override def getMappable = implicitly[Mappable[WitOutcome]]
  override def getLabel: String = "Outcome"
}

object WitOutcome {
  implicit val readsWitOutcome: Reads[WitOutcome] = (
    (__ \ 'intent).read[String] and
    (__ \ 'entities).read[Map[String,PartialWitEntities]].map(processPartialEntities _)  and
    (__ \ 'confidence).read[Double]
    )(WitOutcome.apply _)

  private def processPartialEntities(data: Map[String,PartialWitEntities]): Seq[WitEntity] = {
    data.flatMap { case (id, partial) =>
      partial.entities map { _.toWitEntity(id) }
    }.toSeq
  }
}

case class WitIntent(
  id: String,
  name: String,
  doc: String,
  metadata: Option[String],
  entities: Option[Seq[WitEntity]],
  expression: Option[Seq[WitExpression]]) extends Printable[WitIntent] {
  override def getMappable = implicitly[Mappable[WitIntent]]
  override def getLabel: String = "Intent"
}

object WitIntent {
  implicit val readsWitIntent: Reads[WitIntent] = (
    (__ \ 'id).read[String] and
    (__ \ 'name).read[String] and
    (__ \ 'doc).read[String] and
    (__ \ 'metadata).readNullable[String] and
    (__ \ 'entities).readNullable[Seq[WitEntity]] and
    (__ \ 'expressions).readNullable[Seq[WitExpression]]
    )(WitIntent.apply _)
}

case class WitEntity(
  id: String,
  role: Option[String],
  value: Option[String],
  from: Option[DateTime],
  to: Option[DateTime],
  start: Option[Int],
  end: Option[Int],
  body: Option[String],
  metadata: Option[String],
  suggested: Option[Boolean]) extends Printable[WitEntity] {
  override def getMappable = implicitly[Mappable[WitEntity]]
  override def getLabel: String = "Entity"
}

object WitEntity {
  implicit val readsWitEntity: Reads[WitEntity] = (
    ((__ \ 'wisp).read[String] orElse (__ \ 'id).read[String]) and
      __.read[PartialWitEntity])((id, partialEntity) => partialEntity.toWitEntity(id))
}

case class WitEntityDescription(
  id: String,
  doc: String,
  values: Seq[WitEntityValue],
  builtin: Boolean = false) extends Printable[WitEntityDescription] {
  override def getMappable = implicitly[Mappable[WitEntityDescription]]
  override def getLabel: String = "Description"
}

object WitEntityDescription {
  implicit val readsWitEntityDescription: Reads[WitEntityDescription] = (
    (__ \ 'id).read[String] and
    (__ \ 'doc).read[String] and
    (__ \ 'values).read[Seq[WitEntityValue]] and
    ((__ \ 'builtin).read[Boolean] orElse DummyRead[Boolean](false))
    )(WitEntityDescription.apply _)
  implicit val writesWitEntityDescription: Writes[WitEntityDescription] = (
    (__ \ 'id).write[String] and
    (__ \ 'doc).write[String] and
    (__ \ 'values).write[Seq[WitEntityValue]]
    )(desc => (desc.id, desc.doc, desc.values))
}

case class WitEntityValue(
  value: String,
  expressions: Seq[String],
  metadata: Option[String]) extends Printable[WitEntityValue] {
  override def getMappable = implicitly[Mappable[WitEntityValue]]
  override def getLabel: String = "Entity"
}

object WitEntityValue {
  implicit val formatWitEntityValue: Format[WitEntityValue] = (
    (__ \ 'value).format[String] and
    (__ \ 'expressions).format[Seq[String]] and
    (__ \ 'metadata).formatNullable[String]
    )(WitEntityValue.apply _, unlift(WitEntityValue.unapply _))
}

case class WitExpression(
  id: String,
  body: String,
  entities: Seq[WitEntity]) extends Printable[WitExpression] {
  override def getMappable = implicitly[Mappable[WitExpression]]
  override def getLabel: String = "Expression"
}

object WitExpression {
  implicit val readsWitExpression: Reads[WitExpression] = (
    (__ \ 'id).read[String] and
    (__ \ 'body).read[String] and
    (__ \ 'entities).read[Seq[WitEntity]]
    )(WitExpression.apply _)
}

case class WitExpressionDescription(expression: String) extends Printable[WitExpressionDescription] {
  override def getMappable = implicitly[Mappable[WitExpressionDescription]]
  override def getLabel: String = "Expression"
}
object WitExpressionDescription {
  implicit val writesWitExpressionDescription = Json.writes[WitExpressionDescription] // only works because the field name is "expression"
}

/* The data structures below are only used during parsing */

private case class PartialWitEntities(entities: Seq[PartialWitEntity])

private object PartialWitEntities {
  implicit val readsPartialWitEntities: Reads[PartialWitEntities] =
    (__.read[Seq[PartialWitEntity]] or __.read[PartialWitEntity].map(Seq(_))).map(PartialWitEntities.apply _)
}

private case class PartialWitEntity(
  value: Option[String],
  role: Option[String],
  from: Option[DateTime],
  to: Option[DateTime],
  start: Option[Int],
  end: Option[Int],
  body: Option[String],
  metadata: Option[String],
  suggested: Option[Boolean]) {
  def toWitEntity(id: String) = WitEntity(id, role, value, from, to, start, end, body, metadata, suggested)
}

private object PartialWitEntity {
  implicit def readsDateTime: Reads[DateTime] = __.read[String].map(ISODateTimeFormat.dateTime().parseDateTime(_))
  implicit val readsPartialWitEntity: Reads[PartialWitEntity] = (
    ((__ \ 'value).readNullable[String] orElse DummyReadOpt[String]())  and
    (__ \ 'role).readNullable[String] and
    ((__ \ 'value \ 'from).readNullable[DateTime] orElse DummyReadOpt[DateTime]()) and
    ((__ \ 'value \ 'to).readNullable[DateTime] orElse DummyReadOpt[DateTime]()) and
    (__ \ 'start).readNullable[Int] and
    (__ \ 'end).readNullable[Int] and
    (__ \ 'body).readNullable[String] and
    (__ \ 'metadata).readNullable[String] and
    (__ \ 'suggested).readNullable[Boolean]
    )(PartialWitEntity.apply _)
}

private case class DummyRead[T](default: T) extends Reads[T] {
  def reads(json: JsValue) = JsSuccess(default)
}
private case class DummyReadOpt[T]() extends Reads[Option[T]] {
  def reads(json: JsValue) = JsSuccess(None)
}
