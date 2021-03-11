package io.github.jihhel.mycookbook

import cats.effect.{Async, ContextShift, IO}
import ciris._
import cats.implicits._

final case class Port(number: Int) extends AnyVal

object Port {
  val load: ConfigValue[Port] = env("PORT")
    .or(prop("PORT"))
    .default("8080")
    .as[Int]
    .map(Port(_))
}

final case class DBConfig(user: String,
                          pwd: Secret[String],
                          driver: String,
                          host: String,
                          port: Int,
                          name: String) {

  val url: String = s"postgresql://$host:$port/$name"
}

object DBConfig {
  lazy val load: ConfigValue[DBConfig] = {
    (
      env("DB_USER"),
      env("DB_PWD").secret,
      env("PSQL_DRIVER"),
      env("DB_HOST"),
      env("DB_PORT").default("5432").as[Int],
      env("DB_NAME")
    ).mapN { (dbUser, dbPwd, driver, host, port, name) =>
      DBConfig(dbUser, dbPwd, driver, host, port, name)
    }
  }
}

final case class Config(port: Port, db: DBConfig)

object Config {
  def load(implicit F: Async[IO], context: ContextShift[IO]): IO[Config] = {
    (for {
      port <- Port.load
      db <- DBConfig.load
    } yield Config(port, db)).load
  }
}
