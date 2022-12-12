package el.ka.someapp.view.adapters

import android.annotation.SuppressLint
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputLayout
import el.ka.someapp.R
import el.ka.someapp.data.model.ErrorApp
import el.ka.someapp.data.model.State
import el.ka.someapp.data.model.User
import el.ka.someapp.data.model.UserRole
import el.ka.someapp.data.model.measuring.MeasuringCondition
import el.ka.someapp.data.model.measuring.MeasuringKind
import el.ka.someapp.data.model.measuring.MeasuringState
import el.ka.someapp.data.model.role.*
import el.ka.someapp.viewmodel.StatePassword

@BindingAdapter("app:isLoad")
fun visibleLoader(view: View, state: State) {
  view.visibility = if (state == State.LOADING) View.VISIBLE else View.GONE
}

@BindingAdapter("app:loadText")
fun loadText(textView: TextView, textId: Int) {
  textView.text = if (textId > 0) textView.context.getString(textId) else ""
}

@BindingAdapter("app:eyeState")
fun setEyeState(image: ImageView, state: Boolean) {
  val imageID = if (state) R.drawable.ic_eye else R.drawable.ic_close_eye
  image.setImageResource(imageID)
}

@BindingAdapter("app:showPassword")
fun showPassword(editText: EditText, state: Boolean) {
  if (state) {
    editText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
  } else {
    editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
  }
  editText.setSelection(editText.text.length)
}

@BindingAdapter("app:defenderIndicatorState")
fun setDefenderState(view: View, isActive: Boolean) {
  val drawableId = if (isActive) R.drawable.defender_circle_active else R.drawable.defender_circle
  view.background = view.context.getDrawable(drawableId)
}

@BindingAdapter("app:defenderStateText")
fun setDefenderStateText(textView: TextView, state: StatePassword) {
  val stringId = when (state) {
    StatePassword.NEW, StatePassword.NEW_WITH_ERROR -> R.string.defender_create_pass
    StatePassword.REPEAT_NEW -> R.string.defener_repeat_password
    StatePassword.NO_NEW -> R.string.defender_no_new
    else -> R.string.loading
  }

  textView.setText(stringId)
}

@BindingAdapter("app:resetPasswordText")
fun showResetPasswordText(textView: TextView, state: State) {
  textView.visibility = if (state == State.AWAITING_CONTINUE) View.VISIBLE else View.GONE
}

@BindingAdapter("app:userUrl")
fun showUserUrl(imageView: ImageView, user: User?) {
  loadInto(imageView, url = user?.profileImageUrl, placeholder = R.drawable.profile_placeholder)
}

@BindingAdapter("app:backgroundUrl")
fun showBackground(imageView: ImageView, user: User?) {
  loadInto(imageView, url = user?.backgroundImageUrl, placeholder = R.drawable.placeholder)
}

@SuppressLint("SetTextI18n")
@BindingAdapter("showKind")
fun showKind(textView: TextView, kind: MeasuringKind) {
  val context = textView.context
  val kindString = when (kind) {
    MeasuringKind.MEASURE -> context.getString(R.string.measure)
    MeasuringKind.MEASURING_DEVICE -> context.getString(R.string.measure_device)
    MeasuringKind.MEASURING_TRANSDUCERS -> context.getString(R.string.measuring_transducers)
    MeasuringKind.MEASURING_SYSTEM -> context.getString(R.string.measuring_system)
  }

  textView.text = "${context.getString(R.string.kind)}: $kindString"
}


@SuppressLint("SetTextI18n")
@BindingAdapter("showStatus")
fun showStatus(textView: TextView, state: MeasuringState) {
  val context = textView.context
  val stateString = when (state) {
    MeasuringState.CALIBRATION -> context.getString(R.string.calibration)
    MeasuringState.VERIFICATION -> context.getString(R.string.verification)
    MeasuringState.CERTIFICATION -> context.getString(R.string.certification)
  }

  textView.text = "${context.getString(R.string.state)}: $stateString"
}


@SuppressLint("SetTextI18n")
@BindingAdapter("showCondition")
fun showCondition(textView: TextView, condition: MeasuringCondition) {
  val context = textView.context
  val conditionString = when (condition) {
    MeasuringCondition.IN_WORK -> context.getString(R.string.in_work)
    MeasuringCondition.REPAIR -> context.getString(R.string.repair)
    MeasuringCondition.MOTHBALLED -> context.getString(R.string.mothballed)
  }

  textView.text = "${context.getString(R.string.conditionTitle)}: $conditionString"
}

fun loadInto(imageView: ImageView, url: String?, placeholder: Int) {
  Glide
    .with(imageView.context)
    .load(url)
    .placeholder(placeholder)
    .into(imageView)
}

@BindingAdapter("app:imageUrl")
fun showImage(imageView: ImageView, url: String) {
  Glide
    .with(imageView.context)
    .load(url)
    .placeholder(R.drawable.profile_placeholder)
    .into(imageView)
}

@BindingAdapter("app:jobPosition")
fun setJobPositionName(textView: TextView, positionName: String) {
  textView.text =
    if (positionName == UserRole.HEAD.roleName)
      textView.context.getString(R.string.head)
    else positionName
}

@SuppressLint("SetTextI18n")
@BindingAdapter("app:jobRole")
fun setJobRole(textView: TextView, role: UserRole) {
  val roleStringID = roleToStringID(role)
  if (role != UserRole.HEAD) {
    val ctx = textView.context
    textView.text = "[${ctx.getString(roleStringID)}]"
  }
}

fun roleToStringID(role: UserRole): Int {
  return when (role) {
    UserRole.READER -> R.string.role_reader
    UserRole.EDITOR_2 -> R.string.role_editor_2
    UserRole.EDITOR_1 -> R.string.role_editor_1
    UserRole.HEAD -> R.string.head
  }
}

@SuppressLint("SetTextI18n")
@BindingAdapter("app:nodeRole")
fun setUserNodeRole(textView: TextView, role: UserRole?) {
  if (role == null) return

  val roleStringID = roleToStringID(role)
  val ctx = textView.context
  val roleLabel = ctx.getString(R.string.role)
  val roleString = ctx.getString(roleStringID)
  textView.text = "$roleLabel: $roleString"
}

@BindingAdapter("app:showError")
fun showError(textView: TextView, error: ErrorApp?) {
  textView.text = if (error != null)
    textView.context.getString(error.textId) else ""
}

@BindingAdapter("app:hasAccessToEdit")
fun hasAccessToEdit(view: View, role: UserRole?) {
  hasAccess(view, role, AccessType.CHANGE_NODE_NAME)
}

@BindingAdapter("app:hasAccessToDelete")
fun hasAccessToDelete(view: View, role: UserRole?) {
  hasAccess(view, role, AccessType.DELETE_NODE)
}

@BindingAdapter("app:hasAccessToAddJobField")
fun hasAccessToAddJobField(view: View, role: UserRole?) {
  hasAccess(view, role, AccessType.ADD_JOB_FIELD)
}

@BindingAdapter("app:hasAccessToAddNode")
fun hasAccessToAddNode(view: View, role: UserRole?) {
  hasAccess(view, role, AccessType.CREATE_NODE)
}

@BindingAdapter("app:hasAccessToAddUser")
fun hasAccessToAddUser(view: View, role: UserRole?) {
  hasAccess(view, role, AccessType.ADD_USER)
}

@BindingAdapter("app:hasAccessToEditMeasuring")
fun hasAccessToEditMeasuring(view: View, role: UserRole?) {
  hasAccess(view, role, AccessType.EDIT_MEASURING)
}


@BindingAdapter("app:hasAccessToAddMeasuring")
fun hasAccessToAddMeasuring(view: View, role: UserRole?) {
  hasAccess(view, role, AccessType.ADD_MEASURING)
}


@BindingAdapter("app:showFieldError")
fun showError2(view: TextInputLayout, error: ErrorApp?) {
  view.error = if (error != null) view.context.getString(error.textId) else null
}

@BindingAdapter("app:stringRes")
fun stringRes(textView: TextView, resId: Int) {
  textView.text = textView.context.getString(resId)
}