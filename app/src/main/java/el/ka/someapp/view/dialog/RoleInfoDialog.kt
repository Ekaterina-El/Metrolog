package el.ka.someapp.view.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Typeface
import android.view.ViewGroup.LayoutParams
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import el.ka.someapp.R
import el.ka.someapp.data.model.UserRole
import el.ka.someapp.data.model.role.AccessType
import el.ka.someapp.data.model.role.hasRole

class RoleInfoDialog(context: Context) : Dialog(context) {

  private var secondaryColor: Int = 0
  private lateinit var font: Typeface
  private lateinit var root: LinearLayout

  init {
    initDialog()
  }

  private fun initDialog() {
    setContentView(R.layout.role_info_dialog)
    window!!.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
    window!!.setWindowAnimations(R.style.Slide)
    setCancelable(true)

    root = findViewById(R.id.dialog_root)
    initLists()
  }


  private fun initLists() {
    initViewProperties()

    initList(UserRole.HEAD)
    initList(UserRole.EDITOR_1)
    initList(UserRole.EDITOR_2)
    initList(UserRole.READER)
  }

  private fun getAccessedAuthority(role: UserRole): List<Int> {
    val authorities = mutableListOf<Int>()
    if (hasRole(role, AccessType.CHANGE_NODE_NAME))
      authorities.add(Authority.CHANGE_NODE_NAME.stringId)
    if (hasRole(role, AccessType.DELETE_NODE)) authorities.add(Authority.DELETE_NODE.stringId)
    if (hasRole(role, AccessType.ADD_JOB_FIELD)) authorities.add(Authority.ADD_JOB_FIELD.stringId)
    if (hasRole(role, AccessType.ADD_JOB_FIELD)) authorities.add(Authority.ADD_JOB_FIELD.stringId)

    if (hasRole(role, AccessType.EDIT_READER) && hasRole(role, AccessType.EDIT_EDITOR_2) && hasRole(
        role,
        AccessType.EDIT_EDITOR_1
      )
    ) {
      if (hasRole(role, AccessType.EDIT_HEAD)) authorities.add(Authority.EDIT_JOB_FIELD.stringId)
      else authorities.add(Authority.EDIT_JOB_FIELD_WITHOUT_HEAD.stringId)
    }

    if (hasRole(
        role,
        AccessType.DELETE_READER
      )
    ) authorities.add(Authority.DELETE_JOB_FIELD_WITHOUT_HEAD.stringId)
    if (hasRole(role, AccessType.CREATE_NODE)) authorities.add(Authority.CREATE_NODE.stringId)
    if (hasRole(role, AccessType.ADD_USER)) authorities.add(Authority.ADD_USER_BY_EMAIL.stringId)
    if (hasRole(role, AccessType.DELETE_USER)) authorities.add(Authority.DELETE_USER.stringId)
    if (hasRole(role, AccessType.ADD_MEASURING)) authorities.add(Authority.ADD_MEASURING.stringId)
    if (hasRole(role, AccessType.EDIT_MEASURING)) authorities.add(Authority.EDIT_MEASURING.stringId)
    if (hasRole(
        role,
        AccessType.DELETE_MEASURING
      )
    ) authorities.add(Authority.DELETE_MEASURING.stringId)
    if (hasRole(role, AccessType.VIEW_MEASURING)) authorities.add(Authority.VIEW_MEASURING.stringId)
    if (hasRole(
        role,
        AccessType.EXPORT_MEASURING
      )
    ) authorities.add(Authority.EXPORT_MEASURING.stringId)

    return authorities.toList()
  }

  private fun initViewProperties() {
    font = ResourcesCompat.getFont(context, R.font.montserrat_regular)!!
    secondaryColor = context.getColor(R.color.secondary_color)
  }

  private fun initList(userRole: UserRole) {
    val titleId = when (userRole) {
      UserRole.HEAD -> R.string.head
      UserRole.EDITOR_1 -> R.string.role_editor_1
      UserRole.EDITOR_2 -> R.string.role_editor_2
      UserRole.READER -> R.string.role_reader
    }

    val title = createItem(titleId, true)
    root.addView(title)

    val items = getAccessedAuthority(userRole)
    items.forEachIndexed { _, i ->
      val view = createItem(i)
      root.addView(view)
    }
  }

  private fun createItem(it: Int, isTitle: Boolean = false): TextView {
    val textView = TextView(
      context, null, 0,
      if (isTitle) R.style.text_roles_headers else R.style.text_roles_content
    )
    textView.text = context.getString(
      if (isTitle) R.string.header else R.string.list_item,
      context.getString(it)
    )
    return textView
  }

  fun openDialog() {
    show()
  }

  companion object {
    @SuppressLint("StaticFieldLeak")
    private var dialog: RoleInfoDialog? = null
    fun getInstance(context: Context): RoleInfoDialog {
      dialog = if (dialog == null) RoleInfoDialog(context) else dialog
      return dialog!!
    }
  }

  enum class Authority(val stringId: Int) {
    CHANGE_NODE_NAME(R.string.change_node_name),
    DELETE_NODE(R.string.delete_node_info),
    ADD_JOB_FIELD(R.string.add_job_field_info),
    EDIT_JOB_FIELD(R.string.edit_job_field_info_all),
    EDIT_JOB_FIELD_WITHOUT_HEAD(R.string.edit_job_field_info_without_head),
    DELETE_JOB_FIELD_WITHOUT_HEAD(R.string.delete_job_field_info_without_head),
    CREATE_NODE(R.string.create_node),
    ADD_USER_BY_EMAIL(R.string.add_user_by_email),
    DELETE_USER(R.string.delete_user),
    ADD_MEASURING(R.string.add_measuring_info),
    EDIT_MEASURING(R.string.edit_measuring_info),
    DELETE_MEASURING(R.string.delete_measuring_info),
    VIEW_MEASURING(R.string.view_measuring_info),
    EXPORT_MEASURING(R.string.export_measuring_info),
  }
}