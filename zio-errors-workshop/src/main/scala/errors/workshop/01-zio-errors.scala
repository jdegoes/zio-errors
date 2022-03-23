package errors.workshop

import zio._
import java.util.Properties
import java.io.IOException
import java.io.FileInputStream

object ZIOFailure extends ZIOAppDefault {
  trait Config

  /*
   * EXERCISE
   *
   * Use `ZIO.fail` to fail with a string error message.
   */
  def loadConfig: IO[String, Config] = TODO

  def run = loadConfig.flatMap(Console.printLine(_))
}

object ZIOCatchAll extends ZIOAppDefault {
  trait Config

  def loadConfig: IO[String, Config] = ZIO.fail("Cannot load config")

  /*
   * EXERCISE
   *
   * Use `ZIO#catchAll` to catch the error and print it to the console.
   */
  def run = loadConfig
}

object ZIODie extends ZIOAppDefault {
  trait Config

  /*
   * EXERCISE
   *
   * Use `ZIO.die` to fail with a `NullPointerException`.
   */
  def loadConfig: IO[String, Config] = TODO

  def run = loadConfig.exit.flatMap(Console.printLine(_))
}

object ZIOSandbox extends ZIOAppDefault {
  trait Config

  def loadConfig: IO[String, Config] = ZIO.succeed(throw new NullPointerException("The configuration path is null"))

  /*
   * EXERCISE
   *
   * Use `ZIO#sandbox`, followed by `ZIO#catchAll`, to intercept the fatal
   * error and print it to the console.
   *
   * Then refactor the code to use `catchAllCause`, which composes the
   * previous two functions for you.
   */
  def run = loadConfig
}

object ZIORefinement extends ZIOAppDefault {
  trait Config

  def loadSomeConfig[C](path: String, converter: Properties => C): IO[Throwable, C] =
    ZIO.attemptBlockingIO {
      val stream = new java.io.FileInputStream(path)

      val props = new Properties()

      try props.load(stream)
      finally stream.close()

      converter(props)
    }

  val myConfigReader: Properties => Config = _ => new Config {}

  final case class ConfigNotFoundError(path: String) extends RuntimeException(s"Config not found at $path")

  /*
   * EXERCISE
   *
   * Use `ZIO#refineOrDie` to refine the error to a `ConfigNotFoundError` by
   * converting some subset of `Throwable` to this error type, and ignoring
   * the rest of `Throwable`.
   */
  def loadMyConfig: IO[ConfigNotFoundError, Config] = TODO

  def run = loadMyConfig
}

object ZIORefinementTo extends ZIOAppDefault {
  trait Config

  /*
   * EXERCISE
   *
   * Using `ZIO.attempt` and `ZIO#refineToOrDie`, create a constructor that
   * will only fail with recoverable errors of type `IOException`.
   */
  def attemptIO[A](code: => A): IO[IOException, A] = TODO

  def run = attemptIO(new FileInputStream("/tmp/config.properties")).exit.flatMap(Console.printLine(_))
}

object ZIOCause extends ZIOAppDefault {
  val error1 = Cause.fail("Uh oh, error 1!")

  val error2 = Cause.fail("Uh oh, error 1!")

  val differentError3 = Cause.fail(42)

  val defect1 = Cause.die(new Throwable("Uh oh, defect 1!"))

  val defect2 = Cause.die(new Throwable("Uh oh, defect 1!"))

  /*
   * EXERCISE
   *
   * Use `++` to combine `error1` and `error2` sequentially.
   */
  lazy val combinedErrors1 = TODO

  /*
   * EXERCISE
   *
   * Use `&&` to combine `error1` and `error2` in parallel.
   */
  lazy val combinedErrors2 = TODO

  /*
   * EXERCISE
   *
   * Try to use `++` to combine `error1` and `differentError3` sequentially.
   * Look at the types.
   */
  lazy val combinedErrors3 = error1 ++ differentError3

  def run =
    Console.printLine(combinedErrors1) *>
      Console.printLine(combinedErrors2) *>
      Console.printLine(combinedErrors3)
}

object ZIOZooming {
  val z1: IO[String, Option[Int]] = ZIO.succeed(Some(1))

  /*
   * EXERCISE
   *
   * Use `ZIO#some` to drill into the `Option` in the value channel.
   */
  lazy val z2: IO[Option[String], Int] = z1.TODO

  /*
   * EXERCISE
   *
   * Use `ZIO#unsome` to undo the previous transformation.
   */
  lazy val z3: IO[String, Option[Int]] = z2.TODO

  val z4: IO[String, Either[Boolean, Int]] = ZIO.succeed(Right(1))

  /*
   * EXERCISE
   *
   * Use `ZIO#left` to drill into the `Left` in the value channel.
   */
  lazy val z5: IO[Either[String, Int], Boolean] = z4.TODO

  /*
   * EXERCISE
   *
   * Use `ZIO#unleft` to undo the previous transformation.
   */
  lazy val z6: IO[String, Either[Boolean, Int]] = z5.TODO

  val z7: IO[String, Either[Boolean, Option[Int]]] = ZIO.succeed(Right(Some(1)))

  /*
   * EXERCISE
   *
   * Use the appropriate operators to zoom into the `Int` inside `z7`. Insert
   * a type for the IO.
   */
  lazy val z8: TODO = z7.TODO

  val z9: IO[String, Either[Boolean, Option[List[Int]]]] = ZIO.succeed(Right(Some(1 :: Nil)))

  /*
   * EXERCISE
   *
   * Use the appropriate operators to zoom into the `Int` inside `z9`. Insert
   * a type for the IO.
   */
  lazy val z10: TODO = z9.TODO

  /*
   * EXERCISE
   *
   * Finally, undo the transformation you did to `z10`.
   */
  lazy val z11: IO[String, Either[Boolean, Option[List[Int]]]] = (z10: Any).TODO
}
