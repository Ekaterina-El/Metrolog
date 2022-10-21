package el.ka.someapp.data.model

data class Node(
  val id: String = "",
  val name: String = "",
  val rootNodeId: String? = null,
  val level: Int = 0,
  val readers: List<String> = listOf(),
  val editors: List<String> = listOf(),
  val head: List<String> = listOf(),
  val children: List<String> = listOf()
)