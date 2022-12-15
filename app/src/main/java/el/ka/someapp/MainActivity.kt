package el.ka.someapp

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import el.ka.someapp.data.model.Exporter

class MainActivity : AppCompatActivity(R.layout.activity_main)  {
  var loadingDialog: Dialog? = null
  var exporter = Exporter()

  override fun onCreate(savedInstanceState: Bundle?) {
    setTheme(R.style.Theme_SomeApp)
    super.onCreate(savedInstanceState)
  }
}