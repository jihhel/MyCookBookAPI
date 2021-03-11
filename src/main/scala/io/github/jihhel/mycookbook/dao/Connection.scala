package io.github.jihhel.mycookbook.dao

import cats.effect.IO
import doobie.free.connection.ConnectionIO
import doobie.hikari.HikariTransactor
import doobie.quill.DoobieContext
import io.getquill.SnakeCase
import doobie.implicits._

final class Connection(xa: HikariTransactor[IO]) extends DoobieContext.Postgres(SnakeCase) {

  def makeIO[A]: ConnectionIO[A] => IO[A] = { res =>
    res.transact(xa)
  }
}
