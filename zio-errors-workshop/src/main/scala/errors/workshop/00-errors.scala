package errors.workshop

import zio._

/**
 * In functional programming, an error type is one that models the effect of
 * "short-circuiting", also called "early return".
 */
object EarlyReturn extends ZIOAppDefault {

  /**
   * EXERCISE
   *
   * Implement a data type that has the power of early return.
   *
   * Discuss how this data type differs from other error types you are familiar
   * with.
   */
  sealed trait EarlyReturn[+E, +A] {
    def map[E1 >: E, B](f: A => B): EarlyReturn[E1, B] = TODO

    def flatMap[E1 >: E, B](f: A => EarlyReturn[E1, B]): EarlyReturn[E1, B] = TODO

    def run: Either[E, A] = TODO
  }
  object EarlyReturn {
    def apply[A](a: A): EarlyReturn[Nothing, A]       = TODO
    def earlyReturn[A](a: A): EarlyReturn[A, Nothing] = TODO
  }

  def program(number: Int) =
    for {
      v1 <- EarlyReturn(number)
      v2 <- if (v1 % 2 == 0) EarlyReturn.earlyReturn("Returned eariest!") else EarlyReturn(number * 41)
      v3 <- if (v2 % 2 == 0) EarlyReturn.earlyReturn("Returned earlier!") else EarlyReturn(())
    } yield "Did not return early!"

  def run =
    Console.printLine(program(42).run)
}

/**
 * The Scala standard library includes three pure error types and other types
 * with fallibility baked-in.
 */
object StandardErrorTypes extends ZIOAppDefault {
  import scala.util._

  /*
   * EXERCISE
   *
   * Find the pure error types in Scala.
   */
  type PureErrorType1[+A] = TODO

  type PureErrorType2[+A] = TODO

  type PureErrorType3[+E, +A] = TODO

  /*
   * EXERCISE
   *
   * Find an async error type built into Scala.
   */
  type AsyncErrorType[+A] = TODO

  /*
   * EXERCISE
   *
   * Find a non-obvious fallible type that can be used to solve problems
   * involving non-determinism.
   */
  type NonDetErrorType[+A] = TODO

  /*
   * EXERCISE
   *
   * Prove the `NonDetErrorType` is a fallible type by demonstrating short-circuiting behavior.
   */
  def run = Console.printLine("StandardErrorTypes")
}

/**
 * Error types have different "sizes", and their size determines the range of
 * use cases that they are applicable to.
 */
object ErrorSizes extends ZIOAppDefault {
  import scala.util._

  /*
   * EXERCISE
   *
   * Identify the smallest error type (A + 1).
   */
  type SmallestErrorType[+A] = TODO

  sealed trait EmailValidationError
  object EmailValidationError {
    case object MissingDomain   extends EmailValidationError
    case object MissingUsername extends EmailValidationError
    case object InvalidDomain   extends EmailValidationError
    case object InvalidUsername extends EmailValidationError
  }

  /*
   * EXERCISE
   *
   * Identify a small error type that has size (A + 4).
   */
  type Validated[+A] = TODO

  /*
   * EXERCISE
   *
   * Sometimes, two error types both support infinite error values. In this
   * case, we regard type constructor `T1` as "bigger" than type constructor
   * `T2` if all values of type `T2` can be losslessly converted to values of
   * type `T1`. Here, "lossless" means the conversion can in theory be
   * inverted (stack traces and mutability may make this difficult in practice).
   *
   * "Prove" which of the following types is bigger than the other by defining
   * functions that embed one into the other.
   */
  type Type1[+A]     = Try[A]
  type Type2[+E, +A] = Either[E, A]

  def type1ToType2[A](type1: Type1[A]): Type2[Any, A] = TODO // These types are incorrect
  def type2ToType1[A](type2: Type2[Any, A]): Type1[A] = TODO // These types are incorrect

  def run = {
    val try1 = Try(32)

    Console.printLine(type2ToType1(type1ToType2(try1)) == try1)
  }
}

/*
 * EXERCISE
 *
 * There are different ways to represent the same information in error types.
 * Equivalence of two different error types can sometimes be used to facilitate
 * easier error handling, especially with ZIO.
 */
object EquivalentErrorTypes extends ZIOAppDefault {
  /*
   * EXERCISE
   *
   * Prove that `T1` and `T2` are equivalent by implementing two conversion
   * functions that losslessly convert between them.
   */
  type T1 = Either[String, Int]
  type T2 = Either[Int, String]

  def t1ToT2(t1: T1): T2 = TODO
  def t2ToT1(t2: T2): T1 = TODO

  /*
   * EXERCISE
   *
   * Prove that `T3` and `T4` are equivalent by implementing two conversion
   * functions that losslessly convert between them.
   */
  type T3 = Either[String, Option[Int]]
  type T4 = Either[Option[String], Int]

  def t3ToT4(t3: T3): T4 = TODO
  def t4ToT3(t4: T4): T3 = TODO

  def run =
    Console.printLine(t3ToT4(Right(Some(42))) == Right(42))
}

/*
 * The "theoretically best" error type is the smallest possible error type that
 * can losslessly distinguish between all failure causes that are relevant to
 * the application.
 */
object TheoreticallyBestErrorType extends ZIOAppDefault {
  /*
   * EXERCISE
   *
   * Identify the "theoretically best" error type for the following method, and
   * then implement it using the ZIO System service.
   *
   * Discuss advantages and disadvantages of your chosen type.
   */
  def getEnvAsInt(name: String): ZIO[System, TODO, Int] = TODO

  def run =
    for {
      _ <- getEnvAsInt("JAVA_HOME2").exit.debug
      _ <- getEnvAsInt("PATH").exit.debug
    } yield ()
}
