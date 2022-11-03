package el.ka.someapp.data.model

import el.ka.someapp.R
import el.ka.someapp.data.model.Errors.reservedValue

data class ErrorApp(
  val textId: Int
)

object Errors {
  val reservedValue = ErrorApp(textId = R.string.reservedValue)
  val emptyFieldNameValue = ErrorApp(textId = R.string.field_job_name_error)

  val nonUniqueName = ErrorApp(textId = R.string.nonUniqueName)
  val invalidEmail = ErrorApp(textId = R.string.invalidEmail)
  val invalidFullName = ErrorApp(textId = R.string.invalidFullName)
  val invalidPassword = ErrorApp(textId = R.string.invalidPassword)
  val noEqualPassword = ErrorApp(textId = R.string.noEqualPassword)

  val invalidCredentials = ErrorApp(textId = R.string.invalidCredentialsRegistration)
  val weakPassword = ErrorApp(textId = R.string.weekPassword)
  val somethingWrong = ErrorApp(textId = R.string.somethingWrong)
  val collisionUser = ErrorApp(textId = R.string.collisionUser)

  val noFoundUser = ErrorApp(textId = R.string.noFoundUser)

  val invalidUser = ErrorApp(textId = R.string.invalidUser)
  val invalidEmailOrPassword = ErrorApp(textId = R.string.invalidEmailOrPassword)
  val noVerifiedEmail = ErrorApp(textId = R.string.noVerifiedEmail)

  val userAlreadyHasAccess = ErrorApp(textId = R.string.user_aready_has_access)
}