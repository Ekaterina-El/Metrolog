package el.ka.someapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import el.ka.someapp.databinding.ActivityRegistrationBinding

class RegistrationActivity : AppCompatActivity() {
  private lateinit var binding: ActivityRegistrationBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    binding = ActivityRegistrationBinding.inflate(layoutInflater)
    binding.master = this
    setContentView(binding.root)
  }

  fun registration() {
    binding.layoutLoader.visibility = View.VISIBLE
  }
}