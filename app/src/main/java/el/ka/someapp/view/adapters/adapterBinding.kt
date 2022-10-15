package el.ka.someapp.view

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import el.ka.someapp.data.State

@BindingAdapter("app:isLoad")
fun visibleLoader(view: View, state: State) {
  view.visibility = if (state == State.LOADING) View.VISIBLE else View.GONE
}

@BindingAdapter("app:loadText")
fun loadText(textView: TextView, textId: Int) {
  textView.text = if (textId > 0) textView.context.getString(textId) else ""
}