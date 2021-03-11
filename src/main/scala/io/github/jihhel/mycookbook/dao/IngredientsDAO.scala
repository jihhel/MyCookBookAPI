package io.github.jihhel.mycookbook.dao

import cats.effect.IO
import io.github.jihhel.mycookbook.beans.IngredientPost
import io.github.jihhel.mycookbook.beans.dao.tables.Ingredients

trait IngredientsDAO {

  def insertIngredient(ingredient: IngredientPost): IO[Ingredients]
  def selectIngredients: IO[List[Ingredients]]
}

object IngredientsDAO {

  def impl(connection: Connection) = new IngredientsDAO {
    import connection._

    override def insertIngredient(ingredient: IngredientPost): IO[Ingredients] = makeIO {
      for {
        id <- run {
          query[Ingredients]
            .insert(lift(Ingredients(0L, ingredient.name)))
            .returningGenerated(_.id)
        }
        ingredient <- run {
          query[Ingredients]
            .filter(_.id == lift(id))
        }.map(_.head)
      } yield ingredient
    }

    override def selectIngredients: IO[List[Ingredients]] = makeIO {
      run {
        query[Ingredients]
          .sortBy(_.name)
      }
    }
  }
}