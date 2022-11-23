package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.utils.formatNumber

typealias OnLikeListener = (Post) -> Unit
typealias OnShareListener = (Post) -> Unit

class PostsAdapter(
    private val likeClickListener: OnLikeListener,
    private val shareClickListener: OnShareListener
) :
    ListAdapter<Post, PostViewHolder>(PostItemCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return PostViewHolder(
            CardPostBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), likeClickListener, shareClickListener
        )
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    val likeClickListener: OnLikeListener,
    val shareClickListener: OnShareListener
) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {

        binding.apply {
            author.text = post.author
            published.text = post.published
            content.text = post.content
            heartIcon.setImageResource(if (post.likedByMe) R.drawable.ic_baseline_favorite_red_24 else R.drawable.ic_baseline_favorite_border_24)
            likes.text = formatNumber(post.likes)
            shares.text = formatNumber(post.shares)
        }

        binding.apply {
            heartIcon.setOnClickListener {
                likeClickListener(post)
            }

            shareButton.setOnClickListener {
                shareClickListener(post)
            }

        }
    }
}

class PostItemCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean =
        oldItem.id == newItem.id


    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean =
        oldItem == newItem
}