package petclinic

import akka.http.scaladsl.server.Directives._

trait PetClinicService {
  def route =
    get {
      complete("Hello!")
    }
}

object PetClinicService extends PetClinicService
