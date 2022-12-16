package ru.netology.nmedia.presentation

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.databinding.ActivityIntentHandlerBinding

class IntentHandlerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityIntentHandlerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntentHandlerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        handleIntent()
    }

    private fun handleIntent() {
        intent?.let {
            val text = it.getStringExtra(Intent.EXTRA_TEXT)

            if (it.action == Intent.ACTION_SEND && !text.isNullOrBlank()) {
                binding.postText.text = text
            } else {
                Snackbar.make(binding.root, "Ошибка", LENGTH_INDEFINITE)
                    .setAction("OK") {
                        finish()
                    }
                    .show()
                return
            }
        }
    }
}