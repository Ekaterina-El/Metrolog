package el.ka.someapp.data.model

import el.ka.someapp.R

data class ErrorApp(
  val textId: Int
)

object Errors {
  val invalidEmail = ErrorApp(textId = R.string.invalidEmail)
  val invalidFullName = ErrorApp(textId = R.string.invalidFullName)
  val alreadyTakenEmail = ErrorApp(textId = R.string.alreadyTakenEmail)
  val invalidPassword = ErrorApp(textId = R.string.invalidPassword)
  val noEqualPassword = ErrorApp(textId = R.string.noEqualPassword)

  val invalidCredentials = ErrorApp(textId = R.string.invalidCredentialsRegistration)
  val weakPassword = ErrorApp(textId = R.string.weekPassword)
  val somethingWrong = ErrorApp(textId = R.string.somethingWrong)
  val collisionUser = ErrorApp(textId = R.string.collisionUser)

}