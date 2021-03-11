package io.github.jihhel.mycookbook.services

import cats.effect.IO
import io.circe.syntax._
import io.github.jihhel.mycookbook.beans.{RecipePost, RecipesGet}
import io.github.jihhel.mycookbook.beans.dao.tables.Recipes
import io.github.jihhel.mycookbook.dao.RecipesDAO
import org.http4s.{EntityDecoder, HttpRoutes}
import org.http4s.circe._
import org.http4s.dsl.impl.Root
import org.http4s.dsl.io.{->, /, GET, Ok, _}

trait RecipesService {
  def createRecipe(recipe: RecipePost): IO[Recipes]
  def getRecipes: IO[List[Recipes]]
}

object RecipesService {
  implicit val recipePostEntityDecoder: EntityDecoder[IO, RecipePost] =
    jsonOf[IO, RecipePost]

  def routes(service: RecipesService): HttpRoutes[IO] = {
    HttpRoutes.of[IO] {
      case GET -> Root / "recipes" =>
        for {
          recipes <- service.getRecipes
          resp <- Ok(RecipesGet(recipes).asJson)
        } yield resp
      case req @ POST -> Root / "recipe" =>
        for {
          post <- req.as[RecipePost]
          result <- Created(service.createRecipe(post).map(_.asJson))
        } yield result
    }
  }

  def impl(dao: RecipesDAO): RecipesService = new RecipesService {
    override def createRecipe(recipe: RecipePost): IO[Recipes] = {
      dao.insertRecipe(recipe)
    }

    override def getRecipes: IO[List[Recipes]] = {
      dao.selectRecipes
    }
  }
}