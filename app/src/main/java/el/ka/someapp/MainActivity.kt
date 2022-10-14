package el.ka.someapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import el.ka.someapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
  private lateinit var binding: ActivityMainBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)
    binding.apply {
      master = this@MainActivity
    }
  }

  fun toLogin() {
    val intent = Intent(this, LoginActivity::class.java)
    startActivity(intent)
  }

  fun toRegistration() {
    val intent = Intent(this, RegistrationActivity::class.java)
    startActivity(intent)
  }
}