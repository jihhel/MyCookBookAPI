package io.github.jihhel.mycookbook.services

import cats.effect.IO
import io.circe.syntax._
import io.github.jihhel.mycookbook.beans.IngredientPost
import io.github.jihhel.mycookbook.beans.dao.tables.Ingredients
import io.github.jihhel.mycookbook.dao.IngredientsDAO
import org.http4s.{EntityDecoder, HttpRoutes}
import org.http4s.circe._
import org.http4s.dsl.impl.Root
import org.http4s.dsl.io.{->, /, GET, Ok, _}

trait IngredientsService {

  def createIngredient(ingredient: IngredientPost): IO[Ingredients]
  def getIngredients: IO[List[Ingredients]]
}

object IngredientsService {
  implicit val ingredientPostEntityDecoder: EntityDecoder[IO, IngredientPost] =
    jsonOf[IO, IngredientPost]

  def routes(service: IngredientsService): HttpRoutes[IO] = {
    HttpRoutes.of[IO] {
      case GET -> Root / "ingredients" =>
        for {
          ingredients <- service.getIngredients
          resp <- Ok(ingredients.asJson)
        } yield resp
      case req @ POST -> Root / "ingredient" =>
        for {
          post <- req.as[IngredientPost]
          result <- Created(service.createIngredient(post).map(_.asJson))
        } yield result
    }
  }

  def impl(dao: IngredientsDAO): IngredientsService = new IngredientsService {
    override def createIngredient(recipe: IngredientPost): IO[Ingredients] = {
      dao.insertIngredient(recipe)
    }

    override def getIngredients: IO[List[Ingredients]] = {
      dao.selectIngredients
    }
  }
}