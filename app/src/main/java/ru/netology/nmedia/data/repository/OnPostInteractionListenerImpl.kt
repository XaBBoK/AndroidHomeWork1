package ru.netology.nmedia.data.repository

import android.view.View
import androidx.appcompat.widget.PopupMenu
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.OnPostInteractionListener
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.presentation.PostViewModel

class OnPostInteractionListenerImpl(private val viewModel: PostViewModel) :
    OnPostInteractionListener {
    override fun onLike(post: Post) {
        viewModel.likeById(post.id)
    }

    override fun onShare(post: Post) {
        viewModel.shareById(post.id)
    }

    override fun onMore(post: Post, view: View) {
        val popupMenu = PopupMenu(view.context, view)
        popupMenu.inflate(R.menu.moremenu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.removePost -> {
                    viewModel.removeById(post.id)
                    true
                }
                R.id.editPost -> {
                    viewModel.editPost(post)
                    true
                }
                else -> {
                    false
                }
            }

        }
        popupMenu.show()
    }
}