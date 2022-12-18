package el.ka.someapp.data.model

data class User(
  var uid: String = "",
  var fullName: String = "",
  var profileImageUrl: String = "",
  var backgroundImageUrl: String = "",
  var email: String = "",
  var allowedProjects: List<String> = listOf(),
  var availabilityNodes: List<String> = listOf()
)
