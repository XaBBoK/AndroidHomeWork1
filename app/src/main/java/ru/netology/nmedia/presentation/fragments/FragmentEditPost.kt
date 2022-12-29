package ru.netology.nmedia.presentation.fragments

import android.os.Bundle
import android.view.View
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
    }

    private fun setupListeners() {
        binding.ok.setOnClickListener {
            editPostViewModel.data.value?.let { post ->
                viewModel.addOrEditPost(post)
            }
            findNavController().navigateUp()
        }
    }
}