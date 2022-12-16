package ru.netology.nmedia.presentation.ResultContracts

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.presentation.EditPostActivity
import ru.netology.nmedia.presentation.INTENT_EXTRA_POST

class EditOrNewPostResultContract : ActivityResultContract<Post, Post?>() {
    override fun createIntent(context: Context, input: Post): Intent {
        return Intent(context, EditPostActivity::class.java).putExtra(INTENT_EXTRA_POST, input)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Post? {
        if (resultCode == Activity.RESULT_OK) {
            return intent?.getParcelableExtra(INTENT_EXTRA_POST)
        } else {
            return null
        }
    }
}