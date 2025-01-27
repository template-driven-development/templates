// 1. Create and inject {{ .controller.package }}.{{ .controller.simpleClassName }}
// 2. Add the following to conf/routes: "-> {{ .uriPattern }} {{ .router.package }}.{{ .router.simpleClassName }}"

package {{ .router.package }}

import javax.inject.{Inject, Singleton}
import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird.{GET, POST, PUT, DELETE, UrlContext}

@Singleton
class {{ .router.simpleClassName }} @Inject() (controller: {{ .controller.package }}.{{ .controller.simpleClassName }}) extends SimpleRouter {

  override def routes: Routes = {
    case GET(p"/")       => controller.index
    case POST(p"/")      => controller.create
    case GET(p"/$id")    => controller.show(id)
    case PUT(p"/$id")    => controller.update(id)
    case DELETE(p"/$id") => controller.destroy(id)
  }

}
