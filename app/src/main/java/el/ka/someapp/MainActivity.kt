package el.ka.someapp

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(R.layout.activity_main)  {
  var loadingDialog: Dialog? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    setTheme(R.style.Theme_SomeApp)
    super.onCreate(savedInstanceState)
  }
}