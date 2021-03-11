package io.github.jihhel.mycookbook.beans

import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder

final case class IngredientPost(name: String)

object IngredientPost {
  implicit val decoder: Decoder[IngredientPost] = deriveDecoder
}
