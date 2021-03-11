package io.github.jihhel.mycookbook.beans.dao.tables

import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder

final case class Recipes(id: Long, name: String, ingredients: List[Long], url: String)

object Recipes {
  implicit val encoder: Encoder[Recipes] = deriveEncoder
}