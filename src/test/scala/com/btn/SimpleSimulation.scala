package com.btn

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

import scala.concurrent.duration._

class SimpleSimulation extends Simulation {

  val baseUrl = "http://localhost:8080"
  val customAuthHeader = "Basic ---"


  val statusSuccess = "SUCCESS"

  val httpProtocol: HttpProtocolBuilder = http
    .baseURL(baseUrl)
    .inferHtmlResources()
    .acceptHeader("*/*")
    .acceptEncodingHeader("gzip,deflate")
    .contentTypeHeader("application/json;charset=UTF-8")
    .userAgentHeader("Apache-HttpClient/4.5.2 (Java/1.8.0_151)")

  val headers = Map(
    "CustomAuthHeader" -> customAuthHeader
)

  val scn: ScenarioBuilder = scenario("RecordedSimulation")
    .exec(http("request_0")
      .post("/api/method")
      .headers(headers)
      .body(RawFileBody("request_1.txt"))
      .check(status.is(200), jsonPath("$..status").ofType[String].is(statusSuccess)))
    .exec(http("request_1")
      .post("/api/method2")
      .headers(headers)
      .body(RawFileBody("request_2.txt"))
      .check(status.is(200), jsonPath("$..status").ofType[String].is(statusSuccess)))

  setUp(scn.inject(
    //		Warm up - fill cache
    atOnceUsers(1),
    //		Wait for previous request
    nothingFor(2 seconds),
    //		Run scenario
    rampUsersPerSec(1) to 20 during (100 seconds),
    constantUsersPerSec(10) during (100 seconds)
  ).protocols(httpProtocol))
}