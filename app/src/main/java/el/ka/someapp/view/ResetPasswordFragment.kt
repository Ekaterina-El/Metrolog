package el.ka.someapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import el.ka.someapp.R
import el.ka.someapp.data.model.ErrorApp
import el.ka.someapp.data.model.State
import el.ka.someapp.databinding.FragmentResetPasswordBinding
import el.ka.someapp.view.adapters.ErrorAdapter
import el.ka.someapp.viewmodel.ResetPasswordViewModel

class ResetPasswordFragment: BaseFragment() {
  private lateinit var binding: FragmentResetPasswordBinding
  private lateinit var viewModel: ResetPasswordViewModel
  private lateinit var errorAdapter: ErrorAdapter

  private val errorsObserver = Observer<Array<ErrorApp>> {
    errorAdapter.setErrors(it.toList())
  }
  private val stateObserver = Observer<State> {
    if (it == State.AWAITING) navigate(R.id.action_resetPasswordFragment_to_loginFragment)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    super.onCreateView(inflater, container, savedInstanceState)
    return binding.root
  }

  override fun initFunctionalityParts() {
    binding = FragmentResetPasswordBinding.inflate(layoutInflater)
    viewModel = ViewModelProvider(this)[ResetPasswordViewModel::class.java]
    errorAdapter = ErrorAdapter()
  }

  override fun inflateBindingVariables() {
    binding.apply {
      lifecycleOwner = viewLifecycleOwner
      master = this@ResetPasswordFragment
      errorAdapter = this@ResetPasswordFragment.errorAdapter
      viewModel = this@ResetPasswordFragment.viewModel
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    viewModel.errors.observe(viewLifecycleOwner, errorsObserver)
    viewModel.state.observe(viewLifecycleOwner, stateObserver)

  }

  override fun onDestroy() {
    super.onDestroy()
    viewModel.errors.removeObserver { errorsObserver }
    viewModel.state.removeObserver { errorsObserver }
  }

  override fun onBackPressed() {}
}