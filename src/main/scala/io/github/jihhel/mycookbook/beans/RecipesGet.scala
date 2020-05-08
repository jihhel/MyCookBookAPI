package io.github.jihhel.mycookbook.beans

import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder

final case class RecipesGet(recipes: List[Recipe])

object RecipesGet {
  implicit val encoder: Encoder[RecipesGet] = deriveEncoder
}
