package io.github.jihhel.mycookbook.beans

import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder
import io.github.jihhel.mycookbook.beans.dao.tables.Ingredients

final case class IngredientsGet(ingredients: List[Ingredients])

object IngredientsGet {
  implicit val encoder: Encoder[IngredientsGet] = deriveEncoder
}

