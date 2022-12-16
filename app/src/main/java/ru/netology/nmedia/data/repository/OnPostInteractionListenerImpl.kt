package ru.netology.nmedia.data.repository

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import ru.netology.nmedia.R
import ru.netology.nmedia.presentation.ResultContracts.EditOrNewPostResultContract
import ru.netology.nmedia.adapter.OnPostInteractionListener
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.presentation.PostViewModel

class OnPostInteractionListenerImpl(
    private val viewModel: PostViewModel,
    activity: AppCompatActivity
) :
    OnPostInteractionListener {
    private val editOrNewPostLauncher =
        activity.registerForActivityResult(EditOrNewPostResultContract()) { post ->
            post
                ?.let {
                    viewModel.addOrEditPost(
                        it
                    )
                }
                ?: Toast.makeText(activity, "Текст пустой!", Toast.LENGTH_LONG).show()
        }


    override fun onLike(post: Post) {
        viewModel.likeById(post.id)
    }

    override fun onShare(post: Post, view: View) {
        //viewModel.shareById(post.id)
        //val content = posts.find { it.id == id }?.content ?: return

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
                    //viewModel.editPost(post)

                    editOrNewPostLauncher.launch(post)

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


}