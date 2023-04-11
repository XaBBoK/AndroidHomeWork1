package ru.netology.nmedia.presentation.fragments

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.View.*
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.badge.ExperimentalBadgeUtils
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.data.repository.OnPostInteractionListenerImpl
import ru.netology.nmedia.data.repository.PostRepositoryHTTPImpl
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.presentation.AuthViewModel
import ru.netology.nmedia.presentation.PostViewModel
import ru.netology.nmedia.presentation.ScreenState
import ru.netology.nmedia.utils.hideKeyboard
import ru.netology.nmedia.utils.runWhenReady
import ru.netology.nmedia.utils.setupActionBarWithNavControllerDefault
import ru.netology.nmedia.utils.viewBinding

@ExperimentalBadgeUtils
class FeedFragment : Fragment(R.layout.fragment_feed) {
    private val binding: FragmentFeedBinding by viewBinding(FragmentFeedBinding::bind)

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment,
        factoryProducer = { PostViewModel.Factory(this, PostRepositoryHTTPImpl(requireContext())) }
    )

    private val authViewModel by viewModels<AuthViewModel>()

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

        //добавляем верхнее меню с кнопкой назад
        (activity as? AppCompatActivity)?.apply {
            setSupportActionBar(binding.toolbar)
            supportActionBar?.setHomeButtonEnabled(false)
            setupActionBarWithNavControllerDefault()
        }
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)

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

            lifecycleScope.launch {
                if (!AppAuth.getInstance().isAuth()) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.please_login_to_write_posts_message),
                        Toast.LENGTH_SHORT
                    ).show()
                    findNavController().navigate(
                        R.id.action_global_authFragment
                    )
                } else {
                    viewModel.addOrEditPost(
                        Post(content = content)
                    )
                    return@launch
                }
            }
        }

        binding.submitButton.setOnLongClickListener {
            lifecycleScope.launch {
                if (!AppAuth.getInstance().isAuth()) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.please_login_to_write_posts_message),
                        Toast.LENGTH_SHORT
                    ).show()
                    findNavController().navigate(
                        R.id.action_global_authFragment
                    )
                } else {
                    findNavController().navigate(
                        R.id.feedFragmentToEditPostFragment,
                        bundleOf(Pair(INTENT_EXTRA_POST, Post()))
                    )
                    return@launch
                }
            }

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

        binding.unreadMessagesButton.setOnClickListener {
            viewModel.setAllVisible()
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

    @OptIn(ExperimentalBadgeUtils::class)
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
                    //binding.postList.layoutManager?.scrollToPosition(0)
                    binding.postList.smoothScrollToPosition(0)
                }
            }
        }

        val badgeDrawable: BadgeDrawable by lazy {
            val b = BadgeDrawable.create(requireActivity())
            b.badgeGravity = BadgeDrawable.TOP_END
            b.isVisible = false
            b
        }

        viewModel.newerCount.observe(viewLifecycleOwner) {
            /*Toast.makeText(context, "Новые сообщения: $it", Toast.LENGTH_LONG)
                .show()

            binding.postList.runWhenReady {
                //binding.postList.smoothScrollToPosition(0)
                binding.postList.layoutManager?.scrollToPosition(0)
            }*/


            badgeDrawable.isVisible = false

            if (requireNotNull(it) > 0) {

                badgeDrawable.number = it.toInt()
                badgeDrawable.isVisible = true

                BadgeUtils.attachBadgeDrawable(badgeDrawable, binding.unreadMessagesFrame)
                //badgeDrawable.setVerticalOffset(20);
                //badgeDrawable.setHorizontalOffset(15);


                binding.unreadMessagesButton.visibility = VISIBLE
            } else {
                BadgeUtils.detachBadgeDrawable(badgeDrawable, binding.unreadMessagesFrame)
                binding.unreadMessagesButton.visibility = INVISIBLE
                badgeDrawable.isVisible = false
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

        var previousMenuProvider: MenuProvider? = null

        authViewModel.data.observe(viewLifecycleOwner) {
            previousMenuProvider?.let {
                requireActivity().removeMenuProvider(it)
            }

            requireActivity().addMenuProvider(object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.menu_auth, menu)

                    menu.setGroupVisible(R.id.authorized, authViewModel.authorized)
                    menu.setGroupVisible(R.id.unauthorized, !authViewModel.authorized)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                    when (menuItem.itemId) {
                        R.id.login -> {
                            findNavController().navigate(
                                R.id.action_global_authFragment
                            )
                            true
                        }

                        R.id.register -> {
                            findNavController().navigate(
                                R.id.SignUpFragment
                            )
                            true
                        }

                        R.id.logout -> {
                            AlertDialog.Builder(requireContext())
                                .setMessage(getString(R.string.are_you_sure_want_to_logout_message))
                                .setNegativeButton(getString(R.string.no_answer_text)) { _, _ -> }
                                .setPositiveButton(getString(R.string.yes_answer_text)) { _, _ ->
                                    AppAuth.getInstance().removeAuth()
                                }
                                .create()
                                .show()

                            true
                        }
                        else -> false
                    }
            }.also { previousMenuProvider = it }, viewLifecycleOwner)
        }
    }
}