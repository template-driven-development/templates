// 1. Create {{ .model.package }}.{{ .model.simpleClassName }} with an implicit upickle.default.Writer
// 2. Create {{ .form.package }}.{{ .form.simpleClassName }} with an implicit upickle.default.Reader
// 3. Create {{ .router.simpleClassName }}Functions implementation
// 4. Add instance of {{ .router.simpleClassName }} to cask.Main.allRoutes

package {{ .router.package }}

class {{ .router.simpleClassName }}(functions: {{ .router.simpleClassName }}Functions) extends cask.Routes {
  @cask.get("{{ .uri }}")
  def index() =
    functions.index() match {
      case {{ .router.simpleClassName }}Functions.IndexResult.Success(value) =>
        cask.Response(
          upickle.default.write(value),
          headers = Seq("Content-Type" -> "application/json")
        )
      case {{ .router.simpleClassName }}Functions.IndexResult.Failure(cause) =>
        cask.Response(cause.getMessage, statusCode = 500)
    }

  @cask.post("{{ .uri }}")
  def create(request: cask.Request) = {
    val form = upickle.default.read[{{ .form.package }}.{{ .form.simpleClassName }}](request.text())
    functions.create(form) match {
      case {{ .router.simpleClassName }}Functions.CreateResult.Success(value) =>
        cask.Response(
          upickle.default.write(value),
          statusCode = 201,
          headers = Seq("Content-Type" -> "application/json")
        )
      case {{ .router.simpleClassName }}Functions.CreateResult.Failure(cause) =>
        cask.Response(cause.getMessage, statusCode = 500)
    }
  }

  @cask.getJson("{{ .uri }}/:id")
  def show(id: String) =
    functions.show(id) match {
      case {{ .router.simpleClassName }}Functions.ShowResult.Success(value) =>
        cask.Response(
          upickle.default.write(value),
          headers = Seq("Content-Type" -> "application/json")
        )
      case {{ .router.simpleClassName }}Functions.ShowResult.NotFound =>
        cask.Response("", statusCode = 404)
      case {{ .router.simpleClassName }}Functions.ShowResult.Failure(cause) =>
        cask.Response(cause.getMessage, statusCode = 500)
    }

  @cask.put("{{ .uri }}/:id")
  def update(id: String, request: cask.Request) = {
    val form = upickle.default.read[{{ .form.package }}.{{ .form.simpleClassName }}](request.text())
    functions.update(id, form) match {
      case {{ .router.simpleClassName }}Functions.UpdateResult.Success(value) =>
        cask.Response(
          upickle.default.write(value),
          headers = Seq("Content-Type" -> "application/json")
        )
      case {{ .router.simpleClassName }}Functions.UpdateResult.NotFound =>
        cask.Response("", statusCode = 404)
      case {{ .router.simpleClassName }}Functions.UpdateResult.Failure(cause) =>
        cask.Response(cause.getMessage, statusCode = 500)
    }
  }

  @cask.delete("{{ .uri }}/:id")
  def delete(id: String) =
    functions.delete(id) match {
      case {{ .router.simpleClassName }}Functions.DeleteResult.Success =>
        cask.Response("", statusCode = 204)
      case {{ .router.simpleClassName }}Functions.DeleteResult.NotFound =>
        cask.Response("", statusCode = 404)
      case {{ .router.simpleClassName }}Functions.DeleteResult.Failure(cause) =>
        cask.Response(cause.getMessage, statusCode = 500)
    }

  initialize()
}

object {{ .router.simpleClassName }}Functions {
  trait IndexResult
  object IndexResult {
    case class Success(value: List[{{ .model.package }}.{{ .model.simpleClassName }}]) extends IndexResult
    case class Failure(cause: Throwable) extends IndexResult
  }

  trait CreateResult
  object CreateResult {
    case class Success(value: {{ .model.package }}.{{ .model.simpleClassName }}) extends CreateResult
    case class Failure(cause: Throwable) extends CreateResult
  }

  trait ShowResult
  object ShowResult {
    case class Success(value: {{ .model.package }}.{{ .model.simpleClassName }}) extends ShowResult
    case object NotFound extends ShowResult
    case class Failure(cause: Throwable) extends ShowResult
  }

  trait UpdateResult
  object UpdateResult {
    case class Success(value: {{ .model.package }}.{{ .model.simpleClassName }}) extends UpdateResult
    case object NotFound extends UpdateResult
    case class Failure(cause: Throwable) extends UpdateResult
  }

  trait DeleteResult
  object DeleteResult {
    case object Success extends DeleteResult
    case object NotFound extends DeleteResult
    case class Failure(cause: Throwable) extends DeleteResult
  }
}

trait {{ .router.simpleClassName }}Functions {
  def index(): {{ .router.simpleClassName }}Functions.IndexResult
  def create(form: {{ .form.package }}.{{ .form.simpleClassName }}): {{ .router.simpleClassName }}Functions.CreateResult
  def show(id: String): {{ .router.simpleClassName }}Functions.ShowResult
  def update(id: String, form: {{ .form.package }}.{{ .form.simpleClassName }}): {{ .router.simpleClassName }}Functions.UpdateResult
  def delete(id: String): {{ .router.simpleClassName }}Functions.DeleteResult
}
