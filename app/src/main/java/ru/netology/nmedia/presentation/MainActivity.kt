package ru.netology.nmedia.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import ru.netology.nmedia.data.repository.PostRepositoryInMemoryImpl
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.utils.formatNumber

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    private val viewModel: PostViewModel by lazy {
        ViewModelProvider(
            this,
            PostViewModel.Factory(PostRepositoryInMemoryImpl())
        ).get(PostViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        subscribe()
        createListeners()
    }

    private fun subscribe() {
        viewModel.data.observe(this) { post ->
            binding.apply {
                author.text = post.author
                published.text = post.published
                content.text = post.content
                heartIcon.setImageResource(if (post.likedByMe) R.drawable.ic_baseline_favorite_red_24 else R.drawable.ic_baseline_favorite_border_24)
                likes.text = formatNumber(post.likes)
                shares.text = formatNumber(post.shares)
            }
        }
    }

    private fun createListeners() {
        binding.apply {
            heartIcon.setOnClickListener {
                viewModel.like()
            }

            shareButton.setOnClickListener {
                viewModel.share()
            }
        }
    }
}