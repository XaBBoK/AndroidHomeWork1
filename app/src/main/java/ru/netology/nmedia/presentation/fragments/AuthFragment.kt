package ru.netology.nmedia.presentation.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentAuthBinding
import ru.netology.nmedia.error.ApiAppError
import ru.netology.nmedia.error.NetworkAppError
import ru.netology.nmedia.presentation.AuthScreenState
import ru.netology.nmedia.presentation.AuthViewModel


class AuthFragment : Fragment(R.layout.fragment_auth) {
    private val binding: FragmentAuthBinding by viewBinding(FragmentAuthBinding::bind)
    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //добавляем верхнее меню с кнопкой назад
        /*(activity as? AppCompatActivity)?.apply {
            setSupportActionBar(binding.toolbar)
            setupActionBarWithNavControllerDefault()
        }*/

        binding.lifecycleOwner = viewLifecycleOwner

        binding.viewModel = authViewModel

        setupListeners()

        lifecycleScope.launch {
            authViewModel.uiState.collect {
                when (it) {
                    is AuthScreenState.AuthScreenNormal -> {
                        showNormal()
                    }

                    is AuthScreenState.AuthScreenRequesting -> {
                        showRequesting()
                    }

                    is AuthScreenState.AuthScreenError -> {
                        showError(it)
                    }

                    is AuthScreenState.AuthScreenMustNavigateUp -> {
                        showNavigateUp()
                    }
                }
            }
        }
    }

    private fun setupListeners() {
        binding.submitButton.setOnClickListener {
            authViewModel.login()
        }
    }

    private fun showNavigateUp() {
        findNavController().navigateUp()
    }

    private fun showNormal() {
        binding.submitButton.isEnabled = true
    }

    private fun showRequesting() {
        binding.submitButton.isEnabled = false
    }

    private fun showError(it: AuthScreenState.AuthScreenError) {
        val error =
            when (it.error) {
                is ApiAppError -> {
                    getString(R.string.check_login_password_message)
                }

                is NetworkAppError -> {
                    getString(R.string.check_internet_connection_text)
                }

                else -> {
                    getString(R.string.unknown_error_text)
                }
            }

        Snackbar.make(
            requireContext(),
            binding.snackBarCoordinator,
            error,
            Snackbar.LENGTH_SHORT
        )
            .setAction(getString(R.string.ok_button_text)) {}
            .show()

        binding.submitButton.isEnabled = true
    }
}

