package el.ka.someapp.utils

import android.util.Patterns
import el.ka.someapp.data.model.ErrorApp
import el.ka.someapp.data.model.Errors

object Verificator {
  fun checkPassword(password: String, repeatPassword: String): Array<ErrorApp> {
    val listOfErrors = mutableListOf<ErrorApp>()
    if (password.isEmpty() || password.length < 8) {
      listOfErrors.add(Errors.invalidPassword)
    } else if (password != repeatPassword) {
      listOfErrors.add(Errors.noEqualPassword)
    }
    return listOfErrors.toTypedArray()
  }

  fun checkName(fullName: String): Array<ErrorApp> {
    val listOfErrors = mutableListOf<ErrorApp>()
    if (fullName.isEmpty() || fullName.trim().split(" ").size < 2) {
      listOfErrors.add(Errors.invalidFullName)
    }
    return listOfErrors.toTypedArray()
  }

  fun checkEmail(email: String): Array<ErrorApp> {
    val listOfErrors = mutableListOf<ErrorApp>()
    if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
      listOfErrors.add(Errors.invalidEmail)
    }
    return listOfErrors.toTypedArray()
  }

  fun checkForRegistration(
    email: String,
    fullName: String,
    password: String,
    repeatPassword: String
  ): Array<ErrorApp> {
    val listOfErrors = mutableListOf<ErrorApp>()
    listOfErrors.addAll(checkEmail(email = email))
    listOfErrors.addAll(checkName(fullName = fullName))
    listOfErrors.addAll(
      checkPassword(
        password = password,
        repeatPassword = repeatPassword
      )
    )

    return listOfErrors.toTypedArray()
  }
}