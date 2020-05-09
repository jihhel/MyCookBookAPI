package io.github.jihhel.mycookbook

import cats.effect.IO
import io.circe.syntax._
import io.github.jihhel.mycookbook.beans.Recipe
import org.http4s.{HttpRoutes, Status, Uri}
import org.http4s.circe._
import org.http4s.dsl.impl.Root
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.jsoup.Jsoup
import org.http4s.client.Client
import org.http4s.client.dsl.io._
import org.http4s.dsl.io.GET
import org.http4s.util.CaseInsensitiveString
import org.jsoup.nodes.Element

import scala.jdk.CollectionConverters._

trait MarmitonProxy {
  def getMarmiton(queryString: String): IO[List[Recipe]]
}

object MarmitonProxy {

  private val marmitonURI = uri"https://www.marmiton.org/recettes/recherche.aspx"

  def routes(proxy: MarmitonProxy): HttpRoutes[IO] = {
    HttpRoutes.of[IO] {
      case req @ GET -> Root / "marmiton" =>
        for {
          greeting <- proxy.getMarmiton(req.queryString)
          resp <- Ok(greeting.asJson)
        } yield resp
    }
  }

  def impl(client: Client[IO]): MarmitonProxy = new MarmitonProxy {
    def getMarmiton(queryString: String): IO[List[Recipe]] = {
      for {
        query <- IO.pure(s"$marmitonURI?$queryString")
        request = GET(Uri.unsafeFromString(query))
        html <- client.fetch(request) { response =>
          response.status match {
            case Status(200) =>
              response.as[String]
            case Status(301) =>
              response.headers.get(CaseInsensitiveString("location")) match {
                case Some(location) =>
                  client.expect[String](location.value)
                case None => IO.pure("")
              }
          }
        }
        recipes <- parseHTML(html)
      } yield recipes
    }
  }

  private def parseHTML(html: String): IO[List[Recipe]] = IO {
    val parser = Jsoup.parse(html)
    val recipesCards = parser.select(".recipe-card").asScala

    recipesCards.map { recipeCard =>
      if (canParseCard(recipeCard)) {
        Option(parseCard(recipeCard))
      } else {
        Option.empty[Recipe]
      }
    }.toList.flatten
  }

  private def parseCard(recipeCard: Element): Recipe = {
    val card = recipeCard.select(".recipe-card")
    val name = card.select(".recipe-card__title").text()
    val ingredientsStr = card.select(".recipe-card__description").text()
    val ingredients = ingredientsStr.substring(ingredientsStr.indexOf(":") + 1, ingredientsStr.indexOf(".")).split(",").map(_.trim).toList
    val maybeImage = card.select("img").asScala.headOption.map(_.attr("src"))
    val tags = card.select(".recipe-card__tags").select("li").asScala.map(_.text()).toList
    val duration = card.select(".recipe-card__duration__value").text()
    val path = card.select(".recipe-card-link").attr("href")
    val url = s"$marmitonURI$path"

    Recipe(name, ingredients, duration, maybeImage, tags, url)
  }

  private def canParseCard(card: Element): Boolean = {
    card.select(".recipe-card__description").text().contains("Ingrédients : ")
  }
}