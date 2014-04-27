package com.scalawit

import org.scalatest._
import com.scalawit._
import play.api.libs.json.Json
import scala.io.Source

class ScalaWitTest extends FlatSpec with Matchers {

  "Wit" should "parse response" in {
    // Small coverage (only checks that parsing returns something)
    val response1 = Source.fromFile("resources/response1.txt").mkString
    Json.parse(response1).asOpt[WitMessage] should be ('defined)
    val response2 = Source.fromFile("resources/response2.txt").mkString
    Json.parse(response2).asOpt[Seq[WitIntent]] should be ('defined)
    val response3 = Source.fromFile("resources/response3.txt").mkString
    Json.parse(response3).asOpt[WitIntent] should be ('defined)
  }
}
