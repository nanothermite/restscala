package utils

import javax.inject.Inject

import com.mohiva.play.silhouette.api.SecuredErrorHandler
import controllers.routes
import play.api.http.DefaultHttpErrorHandler
import play.api.i18n.Messages
import play.api.mvc.Results._
import play.api.mvc.{RequestHeader, Result}
import play.api.routing.Router
import play.api.{Configuration, OptionalSourceMapper}

import scala.concurrent.Future
import scala.util.Random

/**
 * A secured error handler.
 */
class ErrorHandler @Inject()(
  env: play.api.Environment,
  config: Configuration,
  sourceMapper: OptionalSourceMapper,
  router: javax.inject.Provider[Router])
  extends DefaultHttpErrorHandler(env, config, sourceMapper, router)
  with SecuredErrorHandler {

  /**
   * Called when a user is not authenticated.
   *
   * As defined by RFC 2616, the status code of the response should be 401 Unauthorized.
   *
   * @param request The request header.
   * @param messages The messages for the current language.
   * @return The result to send to the client.
   */
  override def onNotAuthenticated(request: RequestHeader, messages: Messages): Option[Future[Result]] = {
    Some(Future.successful(Redirect(routes.ApplicationController.signIn())))
  }

  /**
   * Called when a user is authenticated but not authorized.
   *
   * As defined by RFC 2616, the status code of the response should be 403 Forbidden.
   *
   * @param request The request header.
   * @param messages The messages for the current language.
   * @return The result to send to the client.
   */
  override def onNotAuthorized(request: RequestHeader, messages: Messages): Option[Future[Result]] = {
    Some(Future.successful(Redirect(routes.ApplicationController.signIn()).flashing("error" -> Messages("access.denied")(messages))))
  }

  private def ranInts(n: Int): Array[Int] = {
    val range = (1 to n).toList
    val ints =
      for (
        x <- range
      ) yield Random.nextInt(x)
    ints.toArray
  }

  private def posNegZer(n: List[Int]): List[Int] = n.filter{_ > 0} ++ n.filter{_ <= 0}
}
