package ru.netology.nmedia.presentation.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.data.repository.OnPostInteractionListenerImpl
import ru.netology.nmedia.data.repository.PostRepositorySQLiteRoomImpl
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.presentation.PostViewModel
import ru.netology.nmedia.utils.hideKeyboard
import ru.netology.nmedia.utils.viewBinding
import java.util.*

class FeedFragment : Fragment(R.layout.fragment_feed) {
    private val binding: FragmentFeedBinding by viewBinding(FragmentFeedBinding::bind)

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment,
        factoryProducer = { PostViewModel.Factory(this, PostRepositorySQLiteRoomImpl(requireContext())) }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribe()
        setupListeners()
        binding.editOrigGroup.visibility = View.GONE
    }


    private fun setupListeners() {
        binding.submitButton.setOnClickListener {
            val content = binding.postContent.text.toString().trim()
            if (content.isEmpty()) {
                Toast.makeText(requireContext(), "Введите текст поста!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.addOrEditPost(
                Post(
                    author = "me",
                    content = content,
                    published = Date().toString()
                )
            )
        }

        binding.submitButton.setOnLongClickListener {
            findNavController().navigate(
                R.id.feedFragmentToEditPostFragment
            )
            true
        }

        binding.cancelEditingButton.setOnClickListener {
            viewModel.cancelEditPost()
        }

    }

    private fun subscribe() {
        val adapter = PostsAdapter(OnPostInteractionListenerImpl(viewModel, this))
        binding.postList.adapter = adapter
        viewModel.data.observe(viewLifecycleOwner) { posts ->
            adapter.submitList(posts)
        }

        viewModel.edited.observe(viewLifecycleOwner) {
            if (it == null) {
                binding.postContent.setText("")
                binding.editingOrigContent.text = ""
                //binding.editOrigGroup.visibility = View.GONE
                this.hideKeyboard()
                binding.postContent.clearFocus()
            } else {
                binding.postContent.setText(it.content)
                binding.postContent.requestFocus()

                binding.editingOrigContent.text = it.content
                //binding.editOrigGroup.visibility = View.VISIBLE
            }
        }
    }
}