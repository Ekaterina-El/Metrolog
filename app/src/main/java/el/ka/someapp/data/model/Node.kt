package el.ka.someapp.data.model

data class Node(
  val id: String = "",
  var name: String = "",
  val rootNodeId: String? = null,
  val level: Int = 0,
  var usersHaveAccess: List<String> = listOf(),
  var children: List<String> = listOf(),
  var jobs: List<JobField> = listOf(),
  var measuring: List<String> = listOf()
)