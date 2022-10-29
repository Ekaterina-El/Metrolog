package el.ka.someapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(R.layout.activity_main)  {
  override fun onCreate(savedInstanceState: Bundle?) {
    setTheme(R.style.Theme_SomeApp);
    super.onCreate(savedInstanceState)
  }
}