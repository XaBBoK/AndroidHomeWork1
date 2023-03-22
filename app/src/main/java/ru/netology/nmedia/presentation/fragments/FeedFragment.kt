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
    private var scrollOnNextSubmit: Boolean = false
        get() {
            if (field) {
                field = false
                return true
            }

            return false
        }


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
            //binding.postListSwipeRefresh.isRefreshing = false
            viewModel.loadPosts()
        }

        binding.retryButton.setOnClickListener {
            viewModel.loadPosts()
        }

    }

    private fun showError(state: ScreenState.Error) {
        if (state.repeatAction == null) {

            Snackbar.make(
                requireContext(),
                binding.snackBarCoordinator,
                getString(R.string.SNACKBAR_ERROR),
                Snackbar.LENGTH_LONG
            )
                .setTextMaxLines(10)
                .setAction("OK") {

                }
                .show()
        } else {
            Snackbar.make(
                requireContext(),
                binding.snackBarCoordinator,
                state.repeatText ?: getString(R.string.SNACKBAR_ERROR),
                Snackbar.LENGTH_LONG
            ).setAction(getString(R.string.repeat)) {
                state.repeatAction.apply { this() }
            }
                .show()
        }

        //binding.errorGroup.visibility = VISIBLE
        //binding.progress.visibility = GONE
        //binding.postList.visibility = INVISIBLE

        //binding.submitButton.isEnabled = false
        //binding.postContent.isEnabled = false

        viewModel.changeState(ScreenState.Working())
    }

    private fun showWorking(state: ScreenState.Working) {
        if (state.moveRecyclerViewPointerToTop) {
            scrollOnNextSubmit = true
            viewModel.changeState(ScreenState.Working())
        }

        binding.emptyText.visibility =
            if (viewModel.data.value?.posts?.size == 0) VISIBLE else GONE

        binding.postListSwipeRefresh.isRefreshing = false
        //binding.errorGroup.visibility = GONE
        //binding.progress.visibility = GONE
        binding.submitButton.isEnabled = true
        binding.postContent.isEnabled = true
        //binding.clickPreventer.visibility = GONE
        //binding.postListSwipeRefresh.visibility = VISIBLE
    }

    private fun showLoading(state: ScreenState.Loading) {
        //binding.progress.visibility = VISIBLE
        //binding.errorGroup.visibility = GONE
        binding.submitButton.isEnabled = false
        binding.postContent.isEnabled = false

        binding.emptyText.visibility = INVISIBLE

        //binding.clickPreventer.visibility = VISIBLE
        //binding.postList.visibility = VISIBLE
        //binding.postListSwipeRefresh.visibility = INVISIBLE
        //if (state.showRefreshAnimation)
        binding.postListSwipeRefresh.isRefreshing = true
    }

    private fun subscribe() {
        adapter = PostsAdapter(OnPostInteractionListenerImpl(viewModel, this))
        binding.postList.adapter = adapter
        viewModel.data.observe(viewLifecycleOwner) { data ->
            binding.postList.runWhenReady {
                //показываем текст-заглушку при пустом списке
                binding.emptyText.visibility =
                    if (viewModel.data.value?.posts?.size == 0) VISIBLE else GONE
            }

            //сортировка вывода списка
            adapter.submitList(data.posts.sortedByDescending { it.published })

            if (scrollOnNextSubmit) {
                binding.postList.runWhenReady {
                    binding.postList.layoutManager?.scrollToPosition(0)
                }
            }
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ScreenState.Error -> showError(state)
                is ScreenState.Loading -> showLoading(state)
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