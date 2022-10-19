package el.ka.someapp.data.model

data class User(
  var uid: String = "",
  var fullName: String = "",
  var email: String = "",
  val allowedProjects: List<String> = listOf()
)
