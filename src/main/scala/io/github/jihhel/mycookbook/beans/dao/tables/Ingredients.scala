package io.github.jihhel.mycookbook.beans.dao.tables

import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder

final case class Ingredients(id: Long, name: String)

object Ingredients {
  implicit val encoder: Encoder[Ingredients] = deriveEncoder
}