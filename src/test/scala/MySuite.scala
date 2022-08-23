import cats._
import cats.effect._
import doobie._
import org.http4s._
import org.http4s.implicits._
import web.HTML
import cats.effect.unsafe.implicits.global

// For more information on writing tests, see
// https://scalameta.org/munit/docs/getting-started.html
class MySuite extends munit.FunSuite {
  def check[A](
      actual: IO[Response[IO]],
      expectedStatus: Status,
      expectedBody: Option[A]
  )(implicit
      ev: EntityDecoder[IO, A]
  ): Boolean = {
    val actualResp = actual.unsafeRunSync()
    val statusCheck = actualResp.status == expectedStatus
    val bodyCheck = expectedBody.fold[Boolean](
      // Verify Response's body is empty.
      actualResp.body.compile.toVector.unsafeRunSync().isEmpty
    )(expected => actualResp.as[A].unsafeRunSync() == expected)
    statusCheck && bodyCheck
  }

  val repo = Repo(DBConfig.unsafeDBTransactor)
  val service = Main.service(repo)

  test("health check succeeds") {
    val healthRes = service(Request(method = Method.GET, uri = uri"/health"))
    assert(check[String](healthRes, Status.Ok, None))
  }

  test("home page works") {
    val name = "some-name"
    val homePage =
      service(
        Request(method = Method.GET, uri = Uri.unsafeFromString(s"/$name"))
      )
    assert(
      check[String](
        homePage,
        Status.Ok,
        Some(
          HTML.root(
            "",
            s"Hello, $name"
          )
        )
      )
    )
  }
}
