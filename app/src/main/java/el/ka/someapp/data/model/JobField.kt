package el.ka.someapp.data.model

data class JobField(
  var jobName: String,
  var jobRole: UserRole,
  var userId: String,
  val isDefault: Boolean = false
) {
  companion object {
    fun getDefaultHead(userId: String) = JobField(
      jobRole = UserRole.HEAD,
      userId = userId,
      isDefault = true,
      jobName = UserRole.HEAD.roleName
    )
  }
}