package errors.workshop

import zio._
import java.sql.ResultSet
import java.net.http
import java.io.IOException
import java.io.FileInputStream

/*
 * BEST PRACTICE #1
 *
 * At the edge of your onion architecture, expose all _possibly_ failing errors
 * in the typed error channel. If some error could possibly be recovered from
 * in some circumstance, then make sure it's exposed.
 */
object ExposeAll extends ZIOAppDefault {
  /*
   * EXERCISE
   *
   * Fix what's wrong with the following function's type signature, and update
   * its implementation to match.
   */
  def readFile1(path: String): UIO[String] = ZIO.blocking {
    ZIO.succeed {
      val source = scala.io.Source.fromFile(path)

      try source.getLines().mkString("\n")
      finally source.close()
    }
  }

  /*
   * EXERCISE
   *
   * Fix what's wrong with the following function's type signature, and update
   * its implementation to match.
   */
  def readFile2(path: String): IO[Throwable, String] = ZIO.blocking {
    ZIO.attempt {
      val source = scala.io.Source.fromFile(path)

      try source.getLines().mkString("\n")
      finally source.close()
    }
  }

  def run =
    for {
      _ <- readFile1("foo").exit.debug
      _ <- readFile2(null).exit.debug
    } yield ()
}

/*
 * BEST PRACTICE #2
 *
 * As you move closer to the center of the onion, move errors that _might_
 * have been recoverable at an outer layer (but which are no longer recoverable
 * at this layer) into the non-recoverable error channel.
 */
object InnerRefinement {
  trait User
  trait Database {
    def doQuery(query: String): IO[Throwable, ResultSet]
  }

  final case class UserRepo(database: Database) {
    /*
     * EXERCISE
     *
     * Fix this error type.
     */
    def getUserById(id: Int): IO[Throwable, User] = ???
  }

  final case class UserSession(repo: UserRepo) {
    /*
     * EXERCISE
     *
     * Fix this error type.
     */
    def currentUser: IO[Throwable, User] = ???
  }
}

/*
 * BEST PRACTICE #3
 *
 * At the edge between your system and an external system, always sandbox
 * errors, to ensure better inter-system diagnostic capabilities.
 */
object Sandboxing {
  sealed trait HttpErrorCode
  object HttpErrorCode {
    final case class InternalServerError(message: String) extends HttpErrorCode
  }
  final case class HttpRequest(headers: Map[String, String], body: Chunk[Byte])
  trait HttpResponse
  object HttpResponse {
    final case class Success(body: Chunk[Byte], headers: Map[String, String]) extends HttpResponse
    final case class Failure(code: HttpErrorCode)                             extends HttpResponse
  }

  def internalLaunchServer(f: HttpRequest => UIO[HttpResponse]) =
    Console.printLine("Launching server")

  def myRouteHandler(request: HttpRequest): UIO[HttpResponse] = ???

  /*
   * EXERCISE
   *
   * Fix the lack of sandboxing in the following wireup function.
   */
  def run =
    internalLaunchServer(myRouteHandler(_))
}

/*
 * BEST PRACTICE #4
 *
 * When cost-effective, design error hierarchies so that effects with different
 * errors automatically unify in an information-preserving way.
 */
object ErrorHierarchies {
  trait User
  trait Permission
  trait Resource

  /*
   * EXERCISE
   *
   * Use sealed traits to design an error hierarchy that will automatically
   * broaden errors using subtype unification.
   */
  final case class AuthenticationError(user: User) extends Exception(s"User ${user} is not authenticated")

  final case class AuthorizationError(user: User, permission: Permission, resource: Resource)
      extends Exception(s"User $user does not have permission $permission on resource $resource")

  final case class UserNotFound(email: String) extends Exception(s"User with email $email not found")

  final case class PaymentDenied(user: User, amount: Double) extends Exception(s"Payment was denied for $user")

  final case class CreditCardAddressIncorrect(user: User)
      extends Exception(s"Credit card address for $user is incorrect")

  final case class CreditCardNumberIncorrect(user: User) extends Exception(s"Credit card number for $user is incorrect")

  final case class CreditCardExpirationDateIncorrect(user: User)
      extends Exception(s"Credit card expiration date for $user is incorrect")
}

/*
 * BEST PRACTICE #5
 *
 * Do not log in the middle, only log at the edge. Trust the ZIO error model to
 * propagate errors losslessly.
 */
object LosslessErrors extends ZIOAppDefault {
  def openFile(path: String): IO[IOException, FileInputStream] =
    ZIO.attemptBlockingIO(new FileInputStream(path))

  def run =
    openFile("foo").tapErrorCause(ZIO.logErrorCause(_))
}
