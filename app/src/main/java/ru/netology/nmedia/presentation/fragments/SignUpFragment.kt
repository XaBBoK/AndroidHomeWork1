package ru.netology.nmedia.presentation.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentSignUpBinding
import ru.netology.nmedia.error.ApiAppError
import ru.netology.nmedia.error.NetworkAppError
import ru.netology.nmedia.presentation.SignUpError
import ru.netology.nmedia.presentation.SignUpScreenState
import ru.netology.nmedia.presentation.SignUpViewModel
import ru.netology.nmedia.utils.setupActionBarWithNavControllerDefault
import ru.netology.nmedia.utils.viewBinding

class SignUpFragment : Fragment(R.layout.fragment_sign_up) {
    private val binding: FragmentSignUpBinding by viewBinding(FragmentSignUpBinding::bind)
    private val signupViewModel: SignUpViewModel by viewModels()
    private lateinit var photoLauncher: ActivityResultLauncher<Intent>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //добавляем верхнее меню с кнопкой назад
        (activity as? AppCompatActivity)?.apply {
            setSupportActionBar(binding.toolbar)
            setupActionBarWithNavControllerDefault()
        }

        binding.lifecycleOwner = viewLifecycleOwner

        binding.viewModel = signupViewModel

        photoLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                when (it.resultCode) {
                    ImagePicker.RESULT_ERROR -> {
                        Toast.makeText(requireContext(), "photo error", Toast.LENGTH_SHORT)
                            .show()
                    }

                    else -> {
                        val uri = it.data?.data ?: return@registerForActivityResult
                        signupViewModel.changeAvatarPhoto(uri.toFile())
                    }
                }
            }

        setupListeners()
        subscribe()
    }

    private fun subscribe() {
        lifecycleScope.launch {
            signupViewModel.uiState.collect {
                when (it) {
                    is SignUpScreenState.SignUpScreenRequesting -> {
                        showRequesting()
                    }

                    is SignUpScreenState.SignUpScreenNormal -> {
                        showNormal()
                    }

                    is SignUpScreenState.SignUpScreenError -> {
                        showError(it)

                        //очищаем состояние ошибки чтобы не показывать ее при смене конфигурации
                        signupViewModel.clearErrorState()
                    }

                    is SignUpScreenState.SignUpScreenMustNavigateUp -> {
                        showNavigateUp()
                    }
                }
            }
        }

        signupViewModel.data.observe(viewLifecycleOwner) {
            if (it?.avatar == null) {
                binding.avatarButtonClear.isVisible = false
                binding.avatar.setImageResource(R.drawable.missing_avatar)
            } else {
                binding.avatarButtonClear.isVisible = true
                it.avatar.let {file ->
                    binding.avatar.setImageURI(file?.toUri())
                }
            }
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

    private fun showError(it: SignUpScreenState.SignUpScreenError) {
        val error =
            when (it.error) {
                is SignUpError.ExceptionError -> {
                    handleExceptionError(it.error)
                }

                is SignUpError.PasswordEmptyError -> {
                    "не введен пароль"
                }

                is SignUpError.PasswordConfirmEmptyError -> {
                    "не введено подтверждение пароля"
                }

                is SignUpError.PasswordsDifferentError -> {
                    "пароли не совпадают"
                }

                is SignUpError.LoginEmptyError -> {
                    "не введен логин"
                }

                is SignUpError.NameEmptyError -> {
                    "не введено имя"
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

    private fun handleExceptionError(error: SignUpError.ExceptionError) =
        when (error.exception) {
            is ApiAppError -> {
                getString(R.string.user_already_exists_message)
            }

            is NetworkAppError -> {
                getString(R.string.check_internet_connection_text)
            }
            else -> {
                getString(R.string.unknown_error_text)
            }
        }

    private fun setupListeners() {
        binding.submitButton.setOnClickListener {
            signupViewModel.register()
        }

        binding.avatarButton.setOnClickListener {
            ImagePicker.with(this)
                .galleryOnly()
                .crop(1f, 1f)
                .compress(256)
                .createIntent(photoLauncher::launch)
        }

        binding.avatarButtonClear.setOnClickListener {
            signupViewModel.changeAvatarPhoto(null)
        }
    }
}