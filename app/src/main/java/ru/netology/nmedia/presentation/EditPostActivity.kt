package ru.netology.nmedia.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.databinding.ActivityEditPostBinding
import ru.netology.nmedia.dto.Post

const val INTENT_EXTRA_POST = "POST-DATA"

class EditPostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditPostBinding

    private val viewModel: PostEditViewModel by viewModels {
        PostEditViewModel.Factory(
            this, post = this.intent.getParcelableExtra(
                INTENT_EXTRA_POST
            ) ?: Post()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewModel = viewModel

        setupListeners()
    }

    private fun setupListeners() {
        binding.ok.setOnClickListener {
            val intent = Intent()

            if (binding.text.text.isNullOrEmpty()) {
                setResult(RESULT_CANCELED, intent)
            } else {
                intent.putExtra(INTENT_EXTRA_POST, viewModel.data.value)
                setResult(RESULT_OK, intent)
            }
            finish()
        }
    }
}