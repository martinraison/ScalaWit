package com.scalawit

import scala.text._
import Document._

trait Printable[T] { self: T =>
  // Macros only have access to weak type tags, so this has to be implemented by the concrete type
  // More elegant solutions are welcome!
  def getMappable: Mappable[T]
  def getLabel: String

  def pretty[T] = Printable.pretty(self.toDocument)

  def toDocument: Document = {
    val printedFields: Seq[(String, Either[String,Document])] = (getMappable.toMap(self) map { case (key, value) =>
      value match {
        case Some(obj) => Some((key,Printable.toEither(obj)))
        case None => None
        case obj => Some((key,Printable.toEither(obj)))
      }
    }).flatten.toSeq
    nest(4, printedFields.foldLeft(text(getLabel))({ (document, kv) =>
      val (key, value) = kv
      val nextDoc = value match {
        case Left(string) => text(string)
        case Right(doc) => doc
      }
      document :/: group(s"${key} = " :: nextDoc)
    }))
  }
}
object Printable {

  private def toEither[T](obj: T): Either[String, Document] = obj match {
    case value: Printable[T] => Right(value.toDocument)
    case value: Seq[_] => Right(getDocumentForSeq(value))
    case value => Left(value.toString)
  }

  def getDocumentForSeq(sequence: Seq[_]) = {
    nest(4, sequence.foldLeft(text(""))({ (document, element) =>
      val nextDoc = element match {
        case value: Printable[_] => value.toDocument
        case value => text(value.toString)
      }
      document :/: group("* " :: nextDoc)
    }))
  }

  def pretty(document: Document) = {
    val writer = new java.io.StringWriter
    document.format(1, writer)
    println(writer.toString)
  }
}
