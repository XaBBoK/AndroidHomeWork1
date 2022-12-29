package ru.netology.nmedia.presentation.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.presentation.fragments.INTENT_EXTRA_POST

class ActivityApp : AppCompatActivity(R.layout.activity_app) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_app)

        handleIntent()
    }

    private fun handleIntent() {
        intent?.let {
            val text = it.getStringExtra(Intent.EXTRA_TEXT)
            intent.removeExtra(Intent.EXTRA_TEXT)

            if (it.action != Intent.ACTION_SEND)
                return@let

            if (!text.isNullOrBlank()) {
                findNavController(R.id.nav_host_fragment).navigate(
                    R.id.feedFragmentToEditPostFragment,
                    bundleOf(Pair(INTENT_EXTRA_POST, Post().copy(content = text)))
                )
            } else {
                Snackbar.make(
                    findViewById(android.R.id.content),
                    "Ошибка",
                    BaseTransientBottomBar.LENGTH_INDEFINITE
                )
                    .setAction("OK") {
                        finish()
                    }
                    .show()
                return
            }
        }
    }
}