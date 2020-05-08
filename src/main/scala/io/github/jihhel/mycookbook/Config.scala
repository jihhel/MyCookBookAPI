package io.github.jihhel.mycookbook

import cats.effect.{Async, ContextShift, IO}
import ciris._
final case class Port(number: Int) extends AnyVal

object Port {
  val load: ConfigValue[Port] = env("PORT")
    .or(prop("PORT"))
    .default("8080")
    .as[Int]
    .map(Port(_))
}

final case class Config(port: Port)

object Config {
  def load(implicit F: Async[IO], context: ContextShift[IO]): IO[Config] = {
    (for {
      port <- Port.load
    } yield Config(port)).load
  }
}
