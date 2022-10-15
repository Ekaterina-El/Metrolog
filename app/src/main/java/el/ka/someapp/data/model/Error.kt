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
  val somethingWrong = ErrorApp(textId = R.string.somethingWrong)
}