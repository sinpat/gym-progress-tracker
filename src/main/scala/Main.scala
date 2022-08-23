import org.http4s._
import org.http4s.headers.`Content-Type`
import org.http4s.dsl.io._
import cats.effect.unsafe.IORuntime
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import com.comcast.ip4s.{ipv4, port}
import web.HTML
import cats._
import cats.effect._
import cats.implicits._
import cats.effect.unsafe.implicits.global
import java.io.File
import doobie._
import doobie.implicits._

object DBConfig {
  private def confValue(key: String) = Either.fromOption(
    sys.env.get(key),
    s"Variable '$key' not specified in environment."
  )

  lazy val dbTransactor = for {
    postgresURL <- confValue("DB_URL")
    postgresUser <- confValue("DB_USER")
    postgresPwd <- confValue("DB_PWD")
  } yield Transactor.fromDriverManager[IO](
    "org.postgresql.Driver", // driver classname
    postgresURL, // connect URL (driver-specific)
    postgresUser, // user
    postgresPwd // password
  )

  def unsafeDBTransactor =
    dbTransactor.fold(msg => throw new Exception(msg), identity)

}

class Repo(xa: Transactor[IO]) {
  def healthCheck = "healthy".pure[ConnectionIO].transact(xa)
}

object Main extends IOApp {
  def service(repo: Repo) = HttpRoutes
    .of[IO] {
      case GET -> Root / "health" => repo.healthCheck.flatMap(_ => Ok())
      case GET -> Root / name =>
        Ok(
          HTML.root(
            "",
            s"Hello, $name"
          ),
          `Content-Type`.apply(MediaType.text.html)
        )
    }
    .orNotFound

  def run(args: List[String]): IO[ExitCode] = {
    val repo = Repo(DBConfig.unsafeDBTransactor)
    EmberServerBuilder
      .default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8080")
      .withHttpApp(service(repo))
      .build
      .use(_ => IO.never)
      .as(ExitCode.Success)
  }
}
