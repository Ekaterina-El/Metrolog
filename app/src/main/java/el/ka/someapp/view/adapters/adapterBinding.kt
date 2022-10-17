package el.ka.someapp.view.adapters

import android.annotation.SuppressLint
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import el.ka.someapp.R
import el.ka.someapp.data.model.State
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
  val stringId = when(state) {
    StatePassword.NEW -> R.string.defender_create_pass
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