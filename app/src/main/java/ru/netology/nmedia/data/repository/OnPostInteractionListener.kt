package ru.netology.nmedia.data.repository

import android.view.View
import ru.netology.nmedia.dto.Post

interface OnPostInteractionListener {
    fun onLike(post: Post)
    fun onEdit(post: Post)
    fun onShare(post: Post, view: View)
    fun onMore(post: Post, view: View)
    fun onVideo(post: Post, view: View)
    fun onPostDetails(post: Post)
    fun onImageViewerFullscreen(image: String)
}