import cats.effect._
import org.http4s._
import org.http4s.headers.`Content-Type`
import org.http4s.dsl.io._
import cats.effect.unsafe.IORuntime
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import com.comcast.ip4s.{ipv4, port}
import web.HTML._

object Main extends IOApp {

  val helloWorldService = HttpRoutes
    .of[IO] { case GET -> Root / "hello" / name =>
      Ok(
        htmlRoot("", s"Hello $name"),
        `Content-Type`.apply(MediaType.text.html)
      )
    }
    .orNotFound

  def run(args: List[String]): IO[ExitCode] =
    EmberServerBuilder
      .default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8080")
      .withHttpApp(helloWorldService)
      .build
      .use(_ => IO.never)
      .as(ExitCode.Success)
}
