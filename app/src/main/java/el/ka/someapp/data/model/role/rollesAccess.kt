package el.ka.someapp.data.model.role

import android.view.View
import el.ka.someapp.data.model.UserRole

val rolesHasAccessToChangeName = listOf(UserRole.HEAD, UserRole.EDITOR_1)
val rolesHasAccessToDeleteNode = listOf(UserRole.HEAD)
val rolesHasAddJobField = listOf(UserRole.HEAD, UserRole.EDITOR_1)
val rolesHasAccessToEditHead = listOf(UserRole.HEAD)
val rolesHasAccessToDeleteHead = listOf<UserRole>()

val rolesHasAccessToEditEditor_1 = listOf(UserRole.HEAD, UserRole.EDITOR_1)
val rolesHasAccessToDeleteEditor_1 = listOf(UserRole.HEAD, UserRole.EDITOR_1)

val rolesHasAccessToEditEditor_2 = listOf(UserRole.HEAD, UserRole.EDITOR_1)
val rolesHasAccessToDeleteEditor_2 = listOf(UserRole.HEAD, UserRole.EDITOR_1)

val rolesHasAccessToEditReader = listOf(UserRole.HEAD, UserRole.EDITOR_1)
val rolesHasAccessToDeleteReader = listOf(UserRole.HEAD, UserRole.EDITOR_1)

val rolesHasAccessToAddUser = listOf(UserRole.HEAD, UserRole.EDITOR_1)
val rolesHasAccessToDeleteUser = listOf(UserRole.HEAD, UserRole.EDITOR_1)

val rolesHasAccessToCreateNode = listOf(UserRole.HEAD, UserRole.EDITOR_1, UserRole.EDITOR_2)
val rolesHasAccessToAddMeasuring = listOf(UserRole.HEAD, UserRole.EDITOR_1, UserRole.EDITOR_2)
val rolesHasAccessToEditMeasuring = listOf(UserRole.HEAD, UserRole.EDITOR_1, UserRole.EDITOR_2)
val rolesHasAccessToDeleteMeasuring = listOf(UserRole.HEAD, UserRole.EDITOR_1, UserRole.EDITOR_2)
val rolesHasAccessToViewMeasuring = listOf(UserRole.HEAD, UserRole.EDITOR_1, UserRole.EDITOR_2, UserRole.READER)
val rolesHasAccessToExportMeasuring =
  listOf(UserRole.HEAD, UserRole.EDITOR_1, UserRole.EDITOR_2, UserRole.READER)
val rolesHasAccessToExitFromProject = listOf(UserRole.EDITOR_1, UserRole.EDITOR_2, UserRole.READER)

fun hasAccess(view: View, role: UserRole?, accessType: AccessType) {
  val access = if (role != null) hasRole(role, accessType) else null
  view.visibility = if (access != null && access) View.VISIBLE else View.GONE
}

fun hasRole(role: UserRole, accessType: AccessType): Boolean {
  val listOfAccess = when (accessType) {
    AccessType.CHANGE_NODE_NAME -> rolesHasAccessToChangeName
    AccessType.CREATE_NODE -> rolesHasAccessToCreateNode
    AccessType.DELETE_NODE -> rolesHasAccessToDeleteNode
    AccessType.ADD_JOB_FIELD -> rolesHasAddJobField
    AccessType.EDIT_HEAD -> rolesHasAccessToEditHead
    AccessType.DELETE_HEAD -> rolesHasAccessToDeleteHead

    AccessType.EDIT_EDITOR_1 -> rolesHasAccessToEditEditor_1
    AccessType.DELETE_EDITOR_1 -> rolesHasAccessToDeleteEditor_1

    AccessType.EDIT_EDITOR_2 -> rolesHasAccessToEditEditor_2
    AccessType.DELETE_EDITOR_2 -> rolesHasAccessToDeleteEditor_2

    AccessType.EDIT_READER -> rolesHasAccessToEditReader
    AccessType.DELETE_READER -> rolesHasAccessToDeleteReader

    AccessType.ADD_USER -> rolesHasAccessToAddUser
    AccessType.DELETE_USER -> rolesHasAccessToDeleteUser

    AccessType.ADD_MEASURING -> rolesHasAccessToAddMeasuring
    AccessType.EDIT_MEASURING -> rolesHasAccessToEditMeasuring
    AccessType.DELETE_MEASURING -> rolesHasAccessToDeleteMeasuring
    AccessType.VIEW_MEASURING -> rolesHasAccessToViewMeasuring
    AccessType.EXPORT_MEASURING -> rolesHasAccessToExportMeasuring

    AccessType.EXIT_FROM_PROJECT -> rolesHasAccessToExitFromProject
  }

  return listOfAccess.contains(role)
}
