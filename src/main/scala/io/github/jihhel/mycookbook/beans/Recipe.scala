package io.github.jihhel.mycookbook.beans
import io.circe._, io.circe.generic.semiauto._

final case class Recipe(name: String, ingredients: List[String], duration: String, picture: Option[String], tags: List[String])

object Recipe {
  implicit val encoder: Encoder[Recipe] = deriveEncoder
}