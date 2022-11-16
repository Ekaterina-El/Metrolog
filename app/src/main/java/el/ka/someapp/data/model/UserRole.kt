package el.ka.someapp.data.model

enum class UserRole(val roleName: String, val priority: Int) {
  HEAD("head", 0),
  EDITOR_1("editor 1", 1),
  EDITOR_2("editor 2", 3),
  READER("reader", 4)
}