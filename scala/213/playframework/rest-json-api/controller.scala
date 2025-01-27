// 1. Create {{ .model.package }}.{{ .model.simpleClassName }} with an implicit JSON Format
// 2. Create {{ .form.package }}.{{ .form.simpleClassName }} with an implicit JSON Reads
// 3. Create and inject {{ .controller.simpleClassName }}Handler implementation

package {{ .controller.package }}

import javax.inject.{Inject, Singleton}
import play.api.libs.json.{Json, Format, Reads}
import play.api.mvc._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class {{ .controller.simpleClassName }} @Inject() (
    handler: {{ .controller.simpleClassName }}Handler,
    cc: ControllerComponents,
    implicit val ec: ExecutionContext
) extends AbstractController(cc) {

  def index = Action.async {
    handler.index
      .recover({{ .controller.simpleClassName }}HandlerIndexResult.Failure(_))
      .map {
        case {{ .controller.simpleClassName }}HandlerIndexResult.Success(value) => Ok(Json.toJson(value))
        case {{ .controller.simpleClassName }}HandlerIndexResult.Failure(e) => InternalServerError(e.getMessage)
      }
  }

  def create = Action.async(parse.json[{{ .form.package }}.{{ .form.simpleClassName }}]) { req =>
    handler
      .create(req.body)
      .recover { {{ .controller.simpleClassName }}HandlerCreateResult.Failure(_) }
      .map {
        case {{ .controller.simpleClassName }}HandlerCreateResult.Success(value) => Created(Json.toJson(value))
        case {{ .controller.simpleClassName }}HandlerCreateResult.Failure(e) => InternalServerError(e.getMessage)
      }
  }

  def show(id: String) = Action.async {
    handler
      .show(id)
      .recover { {{ .controller.simpleClassName }}HandlerShowResult.Failure(_) }
      .map {
        case {{ .controller.simpleClassName }}HandlerShowResult.Success(value) => Ok(Json.toJson(value))
        case {{ .controller.simpleClassName }}HandlerShowResult.NotFound => NotFound
        case {{ .controller.simpleClassName }}HandlerShowResult.Failure(e) => InternalServerError(e.getMessage)
      }
  }

  def update(id: String) = Action.async(parse.json[{{ .form.package }}.{{ .form.simpleClassName }}]) { req =>
    handler
      .update(id, req.body)
      .recover { {{ .controller.simpleClassName }}HandlerUpdateResult.Failure(_) }
      .map {
        case {{ .controller.simpleClassName }}HandlerUpdateResult.Success(value) => Ok(Json.toJson(value))
        case {{ .controller.simpleClassName }}HandlerUpdateResult.NotFound => NotFound("")
        case {{ .controller.simpleClassName }}HandlerUpdateResult.Failure(e) => InternalServerError(e.getMessage)
      }
  }

  def destroy(id: String) = Action.async {
    handler
      .destroy(id)
      .recover { {{ .controller.simpleClassName }}HandlerDestroyResult.Failure(_) }
      .map {
        case {{ .controller.simpleClassName }}HandlerDestroyResult.Success => NoContent
        case {{ .controller.simpleClassName }}HandlerDestroyResult.NotFound => NotFound
        case {{ .controller.simpleClassName }}HandlerDestroyResult.Failure(e) => InternalServerError(e.getMessage)
      }
  }
}

sealed abstract class {{ .controller.simpleClassName }}HandlerIndexResult
object {{ .controller.simpleClassName }}HandlerIndexResult {
  final case class Success(value: List[{{ .model.package }}.{{ .model.simpleClassName }}]) extends {{ .controller.simpleClassName }}HandlerIndexResult
  final case class Failure(cause: Throwable) extends {{ .controller.simpleClassName }}HandlerIndexResult
}

sealed abstract class {{ .controller.simpleClassName }}HandlerCreateResult
object {{ .controller.simpleClassName }}HandlerCreateResult {
  final case class Success(value: {{ .model.package }}.{{ .model.simpleClassName }}) extends {{ .controller.simpleClassName }}HandlerCreateResult
  final case class Failure(cause: Throwable) extends {{ .controller.simpleClassName }}HandlerCreateResult
}

sealed abstract class {{ .controller.simpleClassName }}HandlerShowResult
object {{ .controller.simpleClassName }}HandlerShowResult {
  final case class Success(value: {{ .model.package }}.{{ .model.simpleClassName }}) extends {{ .controller.simpleClassName }}HandlerShowResult
  final case object NotFound extends {{ .controller.simpleClassName }}HandlerShowResult
  final case class Failure(cause: Throwable) extends {{ .controller.simpleClassName }}HandlerShowResult
}

sealed abstract class {{ .controller.simpleClassName }}HandlerUpdateResult
object {{ .controller.simpleClassName }}HandlerUpdateResult {
  final case class Success(value: {{ .model.package }}.{{ .model.simpleClassName }}) extends {{ .controller.simpleClassName }}HandlerUpdateResult
  final case object NotFound extends {{ .controller.simpleClassName }}HandlerUpdateResult
  final case class Failure(cause: Throwable) extends {{ .controller.simpleClassName }}HandlerUpdateResult
}

sealed abstract class {{ .controller.simpleClassName }}HandlerDestroyResult
object {{ .controller.simpleClassName }}HandlerDestroyResult {
  final case object Success extends {{ .controller.simpleClassName }}HandlerDestroyResult
  final case object NotFound extends {{ .controller.simpleClassName }}HandlerDestroyResult
  final case class Failure(cause: Throwable) extends {{ .controller.simpleClassName }}HandlerDestroyResult
}

trait {{ .controller.simpleClassName }}Handler {

  def index: Future[{{ .controller.simpleClassName }}HandlerIndexResult]
  def create(form: {{ .form.package }}.{{ .form.simpleClassName }}): Future[{{ .controller.simpleClassName }}HandlerCreateResult]
  def show(id: String): Future[{{ .controller.simpleClassName }}HandlerShowResult]
  def update(id: String, form: {{ .form.package }}.{{ .form.simpleClassName }}): Future[{{ .controller.simpleClassName }}HandlerUpdateResult]
  def destroy(id: String): Future[{{ .controller.simpleClassName }}HandlerDestroyResult]

}
