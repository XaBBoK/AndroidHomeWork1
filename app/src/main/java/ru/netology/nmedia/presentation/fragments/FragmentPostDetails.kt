package ru.netology.nmedia.presentation.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.data.repository.OnPostInteractionListenerImpl
import ru.netology.nmedia.databinding.FragmentPostDetailsBinding
import ru.netology.nmedia.presentation.PostViewModel
import ru.netology.nmedia.utils.viewBinding


class FragmentPostDetails : Fragment(R.layout.fragment_post_details) {

    private val binding: FragmentPostDetailsBinding by viewBinding(FragmentPostDetailsBinding::bind)

    private val viewModel by viewModels<PostViewModel>({ requireParentFragment() })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val onInteractionListener = OnPostInteractionListenerImpl(viewModel, this)

        binding.post = arguments?.getParcelable(
            INTENT_EXTRA_POST
        )

        viewModel.data.observe(viewLifecycleOwner) { posts ->
            val p = posts.firstOrNull { it.id == binding.post?.id }

            if (p == null) {
                //в списке нет поста с нужным id, выходим
                findNavController().navigateUp()
            } else {
                //выводим данные с "нового" поста
                binding.post = p
            }
        }

        binding.apply {
            binding.post?.let { post ->
                heartIcon.setOnClickListener {
                    onInteractionListener.onLike(post)
                }

                shareButton.setOnClickListener {
                    onInteractionListener.onShare(post, it)
                }

                more.setOnClickListener {
                    onInteractionListener.onMore(post, it)
                }

                videoPreview.setOnClickListener {
                    onInteractionListener.onVideo(post, it)
                }
            }
        }

    }


}