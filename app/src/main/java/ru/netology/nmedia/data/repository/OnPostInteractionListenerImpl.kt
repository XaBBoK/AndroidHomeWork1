package ru.netology.nmedia.data.repository

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.presentation.PostViewModel
import ru.netology.nmedia.presentation.fragments.INTENT_EXTRA_IMAGE_URI
import ru.netology.nmedia.presentation.fragments.INTENT_EXTRA_POST


class OnPostInteractionListenerImpl(
    private val viewModel: PostViewModel,
    private val fragment: Fragment
) :
    OnPostInteractionListener {

    override fun onLike(post: Post) {
        viewModel.likeById(post.id)
    }

    override fun onEdit(post: Post) {
        fragment.findNavController().navigate(
            R.id.action_global_editPostFragment,
            bundleOf(Pair(INTENT_EXTRA_POST, post))
        )
    }

    override fun onShare(post: Post, view: View) {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, post.content)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(intent, "Поделиться контентом")
        view.context.startActivity(shareIntent)
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
                    onEdit(post)
                    true
                }
                else -> {
                    false
                }
            }

        }
        popupMenu.show()
    }

    override fun onVideo(post: Post, view: View) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(post.video))
            view.context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Log.e(
                "postVideo",
                "Activity for Intent with url ${post.video.ifBlank { "NULL" }} not found"
            )
        }

    }

    override fun onPostDetails(post: Post) {
        fragment.findNavController().navigate(
            R.id.feedFragmentToPostDetailsFragment,
            bundleOf(Pair(INTENT_EXTRA_POST, post))
        )
    }

    override fun onImageViewerFullscreen(image: String) {
        fragment.findNavController().navigate(
            R.id.action_global_imageViewerFragment,
            bundleOf(Pair(INTENT_EXTRA_IMAGE_URI, image))
        )
    }


}