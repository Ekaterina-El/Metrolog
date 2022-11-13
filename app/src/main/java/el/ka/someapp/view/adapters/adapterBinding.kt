package el.ka.someapp.view.adapters

import android.text.Html
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
  Glide
    .with(imageView.context)
    .load(user?.profileImageUrl)
    .placeholder(R.drawable.profile_placeholder)
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

@BindingAdapter("app:showError")
fun showError(textView: TextView, error: ErrorApp?) {
  textView.text = if (error != null)
    textView.context.getString(error.textId) else ""
}