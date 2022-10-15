package el.ka.someapp.view

import android.view.View
import androidx.databinding.BindingAdapter
import el.ka.someapp.data.State

@BindingAdapter("app:isLoad")
fun visibleLoader(view: View, state: State) {
  view.visibility = if (state == State.LOADING) View.VISIBLE else View.GONE
}