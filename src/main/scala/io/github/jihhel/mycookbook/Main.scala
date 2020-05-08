package io.github.jihhel.mycookbook

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder

import scala.concurrent.ExecutionContext.global

object Main extends IOApp {
  def run(args: List[String]): IO[ExitCode] = {
    for {
      config <- Config.load
      ec <- (for {
        client <- BlazeClientBuilder[IO](global).stream
        marmiton = MarmitonProxy.impl(client)
        httpApp = MarmitonProxy.routes(marmiton).orNotFound
        exitCode <- BlazeServerBuilder[IO]
          .bindHttp(config.port.number, "0.0.0.0")
          .withHttpApp(httpApp)
          .serve
      } yield exitCode).drain.compile.drain.as(ExitCode.Success)
    } yield ec
  }
}