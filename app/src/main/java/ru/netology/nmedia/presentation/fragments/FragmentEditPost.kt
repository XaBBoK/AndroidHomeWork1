package ru.netology.nmedia.presentation.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentEditPostBinding
import ru.netology.nmedia.dto.Post
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

        setupListeners()
        loadDraft()
        setupSubscribe()
    }

    //загрузка черновика НОВОГО сообщения
    private fun loadDraft() {
        editPostViewModel.data.value?.isNewPost()?.let {
            viewModel.draft?.apply {
                editPostViewModel.content = this
            }

        }
    }

    private fun setupSubscribe() {
        //сохранение черновика НОВОГО сообщения
        editPostViewModel.data.observe(viewLifecycleOwner) {
            it?.isNewPost()?.let {
                viewModel.draft = it.content
            }
        }

        viewModel.fragmentEditPostEdited.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }
    }


    private fun setupListeners() {
        binding.ok.setOnClickListener {
            editPostViewModel.data.value?.let { post ->
                //очистка черновика НОВОГО сообщения
                post.isNewPost()?.let {
                    viewModel.draft = null
                }

                try {
                    binding.ok.isEnabled = false
                    Toast.makeText(requireContext(), getString(R.string.sendming_message_toast), Toast.LENGTH_SHORT)
                        .show()

                    viewModel.addOrEditPost(post)
                } catch (e: Exception) {
                    binding.ok.isEnabled = true
                    Log.e("addOrEditPost", e.message.toString())
                }
            }
        }

        //обработка нажатия кнопки Назад
        //сохранение реализовано через editPostViewModel.data.observe
        //        requireActivity().onBackPressedDispatcher.addCallback(this) {
        //            findNavController().navigateUp()
        //        }
    }
}