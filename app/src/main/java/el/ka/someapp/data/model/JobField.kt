package el.ka.someapp.data.model

data class JobField(
  var jobName: String = "",
  var jobRole: UserRole = UserRole.READER,
  var userId: String = "",
  val default: Boolean = false
) {
  companion object {
    fun getDefaultHead(userId: String) = JobField(
      jobRole = UserRole.HEAD,
      userId = userId,
      default = true,
      jobName = UserRole.HEAD.roleName
    )
  }
}