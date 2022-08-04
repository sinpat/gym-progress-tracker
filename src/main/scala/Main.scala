import org.http4s._
import org.http4s.headers.`Content-Type`
import org.http4s.dsl.io._
import cats.effect.unsafe.IORuntime
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import com.comcast.ip4s.{ipv4, port}
import web.HTML._
import cats._
import cats.effect._
import cats.implicits._
import cats.effect.unsafe.implicits.global
import fs2.io.file.{Files, Path}
import fs2.text
import java.io.File

object Main extends IOApp {

  val helloWorldService = HttpRoutes
    .of[IO] {
      case GET -> Root / name => {
        val filePath = s"./data/${name}.json"
        if (!File(filePath).exists()) {
          NotFound()
        } else {
          val content = Files[IO]
            .readAll(Path(filePath))
            .through(text.utf8.decode)
            .map(htmlRoot("", _))
          Ok(
            content,
            `Content-Type`.apply(MediaType.text.html)
          )
        }
      }
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
