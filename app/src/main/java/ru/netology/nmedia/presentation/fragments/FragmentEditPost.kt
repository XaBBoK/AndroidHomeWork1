package ru.netology.nmedia.presentation.fragments

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentEditPostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.isNewPost
import ru.netology.nmedia.presentation.EditPostViewModel
import ru.netology.nmedia.presentation.PostViewModel
import ru.netology.nmedia.utils.viewBinding


const val INTENT_EXTRA_POST = "POST-DATA"

class FragmentEditPost : Fragment(R.layout.fragment_edit_post) {

    private val binding: FragmentEditPostBinding by viewBinding(FragmentEditPostBinding::bind)

    private val editPostViewModel: EditPostViewModel by viewModels {
        EditPostViewModel.Factory(
            this, post = arguments?.getParcelable(
                INTENT_EXTRA_POST
            ) ?: Post()
        )
    }

    private val viewModel by viewModels<PostViewModel>({ requireParentFragment() })


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = editPostViewModel

        //загрузка черновика НОВОГО сообщения
        loadDraft()

        setupListeners()
    }


    private fun loadDraft() {
        editPostViewModel.data.value?.isNewPost()?.let {
            viewModel.draft?.apply {
                editPostViewModel.content = this
            }
        }
    }

    private fun saveDraft() {
        editPostViewModel.data.value?.isNewPost()?.let {
            viewModel.draft = binding.text.text.toString()
        }
    }

    private fun setupListeners() {
        binding.ok.setOnClickListener {
            editPostViewModel.data.value?.let { post ->
                //очистка черновика НОВОГО сообщения
                editPostViewModel.data.value?.isNewPost()?.let {
                    viewModel.draft = null
                }

                viewModel.addOrEditPost(post)
            }

            findNavController().navigateUp()
        }

        //обработка нажатия кнопки Назад, сохранение черновика НОВОГО сообщения
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            saveDraft()

            findNavController().navigateUp()
        }
    }
}