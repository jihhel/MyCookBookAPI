package io.github.jihhel.mycookbook.beans
import io.circe._, io.circe.generic.semiauto._

final case class RecipePost(name: String,
                            ingredients: List[Long],
                            url: String)

object RecipePost {
  implicit val decoder: Decoder[RecipePost] = deriveDecoder
}