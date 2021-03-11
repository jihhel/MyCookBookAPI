package io.github.jihhel.mycookbook

import cats.effect.{Blocker, ExitCode, IO, IOApp, Resource}
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import io.github.jihhel.mycookbook.dao.{Connection, IngredientsDAO, RecipesDAO}
import io.github.jihhel.mycookbook.services.{IngredientsService, RecipesService}
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.CORS
import cats.implicits._

object Main extends IOApp {

  def run(args: List[String]): IO[ExitCode] = {
    val config = Config.load.unsafeRunSync()

    makeDatabaseTransactor(config.db).use { xa =>
      val dbConnection = new Connection(xa)
      val dao = RecipesDAO.impl(dbConnection)
      val ingredientsDAO = IngredientsDAO.impl(dbConnection)
      val recipes = RecipesService.impl(dao)
      val ingredients = IngredientsService.impl(ingredientsDAO)
      val httpApp = CORS(RecipesService.routes(recipes) <+> IngredientsService.routes(ingredients)).orNotFound

      for {
        ec <- (for {
          //client <- BlazeClientBuilder[IO](global).stream
          exitCode <- BlazeServerBuilder[IO]
            .bindHttp(config.port.number, "0.0.0.0")
            .withHttpApp(httpApp)
            .serve
        } yield exitCode).drain.compile.drain.as(ExitCode.Success)
      } yield ec
    }
  }

  def makeDatabaseTransactor(config: DBConfig): Resource[IO, HikariTransactor[IO]] = {
    for {
      connectEC <- ExecutionContexts.fixedThreadPool[IO](size = 10)
      blocker <- Blocker[IO]
      xa <- HikariTransactor
        .newHikariTransactor[IO](config.driver,
          s"jdbc:${config.url}",
          config.user,
          config.pwd.value,
          connectEC,
          blocker)
    } yield xa
  }
}