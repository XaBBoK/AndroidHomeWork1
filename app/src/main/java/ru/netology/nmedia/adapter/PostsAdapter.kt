package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.View.*
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.presentation.OnPostInteractionListener
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.AttachmentType
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.utils.formatNumber
import ru.netology.nmedia.utils.load

class PostsAdapter(
    private val onInteractionListener: OnPostInteractionListener
) : ListAdapter<Post, PostViewHolder>(PostItemCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return PostViewHolder(
            CardPostBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ), onInteractionListener
        )
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnPostInteractionListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {

        binding.apply {
            author.text = post.author
            published.text = post.published.toString()
            content.text = post.content
            heartIcon.isChecked = post.likedByMe
            heartIcon.text = formatNumber(post.likes)
            heartIcon.isToggleCheckedStateOnClick = false
            //heartIcon.setImageResource(if (post.likedByMe) R.drawable.ic_baseline_favorite_red_24 else R.drawable.ic_baseline_favorite_border_24)
            //likes.text = formatNumber(post.likes)
            shareButton.text = formatNumber(post.shares)
            postVideoGroup.visibility = if (post.video.isNotEmpty()) VISIBLE else GONE
            more.isVisible = post.ownedByMe
            avatar.load(
                url = post.withBaseUrls().authorAvatar,
                placeholder = R.drawable.ic_avatar_placeholder,
                //roundedCornersRadius = 36
            )

            post.withBaseUrls().attachment?.takeIf { it.type == AttachmentType.IMAGE }?.let {
                attachmentImage.visibility = VISIBLE
                attachmentImage.load(
                    url = it.url,
                    placeholder = R.drawable.ic_loading_placeholder
                )
            } ?: let {
                attachmentImage.visibility = GONE
            }
        }

        binding.apply {
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

            root.setOnClickListener {
                onInteractionListener.onPostDetails(post)
            }

            attachmentImage.setOnClickListener {
                post.withBaseUrls().attachment?.url?.let { image ->
                    onInteractionListener.onImageViewerFullscreen(image)
                }
            }
        }
    }
}

class PostItemCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }
}