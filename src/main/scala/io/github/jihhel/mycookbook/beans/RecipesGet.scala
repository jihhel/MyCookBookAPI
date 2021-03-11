package io.github.jihhel.mycookbook.beans

import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder
import io.github.jihhel.mycookbook.beans.dao.tables.Recipes

final case class RecipesGet(recipes: List[Recipes])

object RecipesGet {
  implicit val encoder: Encoder[RecipesGet] = deriveEncoder
}
