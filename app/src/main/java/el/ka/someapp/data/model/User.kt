package el.ka.someapp.data.model

data class User(
  var uid: String = "",
  var fullName: String = "",
  val profileImageUrl: String = "",
  var email: String = "",
  var allowedProjects: List<String> = listOf()
)
