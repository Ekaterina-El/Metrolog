package el.ka.someapp.data.model

data class SpinnerItem(
  val key: String,
  val value: Any
) {
  override fun toString(): String = key
}