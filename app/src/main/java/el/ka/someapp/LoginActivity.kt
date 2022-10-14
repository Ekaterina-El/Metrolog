package el.ka.someapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import el.ka.someapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
  private lateinit var binding: ActivityLoginBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    binding = ActivityLoginBinding.inflate(layoutInflater)
    setContentView(binding.root)

    binding.apply {
      master = this@LoginActivity
    }

  }

  fun login() {
    binding.layoutLoader.visibility = View.VISIBLE
  }
}