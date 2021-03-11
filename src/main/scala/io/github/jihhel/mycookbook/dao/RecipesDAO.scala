package io.github.jihhel.mycookbook.dao

import cats.effect.IO
import io.github.jihhel.mycookbook.beans.RecipePost
import io.github.jihhel.mycookbook.beans.dao.tables.Recipes

trait RecipesDAO {

  def insertRecipe(recipe: RecipePost): IO[Recipes]
  def selectRecipes: IO[List[Recipes]]
}

object RecipesDAO {

  def impl(connection: Connection) = new RecipesDAO {
    import connection._

    override def insertRecipe(recipe: RecipePost): IO[Recipes] = makeIO {
      for {
        id <- run {
          query[Recipes]
            .insert(lift(Recipes(0L, recipe.name, recipe.ingredients, recipe.url)))
            .returningGenerated(_.id)
        }
        recipe <- run {
          query[Recipes]
            .filter(_.id == lift(id))
        }.map(_.head)
      } yield recipe
    }

    override def selectRecipes: IO[List[Recipes]] = makeIO {
      run {
        query[Recipes]
      }
    }
  }
}