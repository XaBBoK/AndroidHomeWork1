package ru.netology.nmedia.presentation.fragments

import android.os.Bundle
import android.view.View
import android.view.View.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.data.repository.OnPostInteractionListenerImpl
import ru.netology.nmedia.data.repository.PostRepositoryHTTPImpl
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.presentation.PostViewModel
import ru.netology.nmedia.presentation.ScreenState
import ru.netology.nmedia.utils.hideKeyboard
import ru.netology.nmedia.utils.runWhenReady
import ru.netology.nmedia.utils.viewBinding

class FeedFragment : Fragment(R.layout.fragment_feed) {
    private val binding: FragmentFeedBinding by viewBinding(FragmentFeedBinding::bind)

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment,
        factoryProducer = { PostViewModel.Factory(this, PostRepositoryHTTPImpl(requireContext())) }
    )

    private lateinit var adapter: PostsAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribe()
        setupListeners()
        binding.editOrigGroup.visibility = GONE
    }


    private fun setupListeners() {
        binding.submitButton.setOnClickListener {
            val content = binding.postContent.text.toString().trim()
            if (content.isEmpty()) {
                Toast.makeText(requireContext(), "Введите текст поста!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.addOrEditPost(
                Post(content = content)
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

        binding.postListSwipeRefresh.setOnRefreshListener {
            binding.postListSwipeRefresh.isRefreshing = false
            viewModel.loadPosts()
        }

        binding.retryButton.setOnClickListener {
            viewModel.loadPosts()
        }

    }

    private fun showError(state: ScreenState.Error) {
        if (!state.needReload) {
            Snackbar.make(
                requireContext(),
                requireView(),
                getString(R.string.SNACKBAR_ERROR) + "\n" + state.message,
                Snackbar.LENGTH_INDEFINITE
            )
                .setTextMaxLines(10)
                .setAction("OK") {

                }
                .show()

            viewModel.changeState(ScreenState.Working())
            return
        }

        binding.errorGroup.visibility = VISIBLE
        binding.progress.visibility = GONE
        binding.postList.visibility = INVISIBLE
        binding.submitButton.isEnabled = false
        binding.postContent.isEnabled = false
    }

    private fun showWorking(state: ScreenState.Working) {
        if (state.moveRecyclerViewPointerToTop) {
            binding.postList.layoutManager?.scrollToPosition(0)
            viewModel.changeState(ScreenState.Working())
            //binding.postListSwipeRefresh.isRefreshing = false
        }

        binding.errorGroup.visibility = GONE
        binding.progress.visibility = GONE
        binding.submitButton.isEnabled = true
        binding.postContent.isEnabled = true
        binding.clickPreventer.visibility = GONE
        //binding.postListSwipeRefresh.visibility = VISIBLE
    }

    private fun showLoading() {
        binding.progress.visibility = VISIBLE
        binding.errorGroup.visibility = GONE
        binding.submitButton.isEnabled = false
        binding.postContent.isEnabled = false

        binding.emptyText.visibility = INVISIBLE

        binding.clickPreventer.visibility = VISIBLE
        binding.postList.visibility = VISIBLE
        //binding.postListSwipeRefresh.visibility = INVISIBLE
    }

    private fun subscribe() {
        adapter = PostsAdapter(OnPostInteractionListenerImpl(viewModel, this))
        binding.postList.adapter = adapter
        viewModel.data.observe(viewLifecycleOwner) { posts ->
            binding.postList.runWhenReady {
                //показываем текст-заглушку при пустом списке
                binding.emptyText.visibility =
                    if (viewModel.data.value?.size == 0) VISIBLE else GONE
            }

            //сортировка вывода списка
            adapter.submitList(posts.sortedByDescending { it.published })
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ScreenState.Error -> showError(state)
                is ScreenState.Loading -> showLoading()
                is ScreenState.Working -> showWorking(state)
            }
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