package ru.netology.nmedia

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val post = Post(
            id = 1,
            author = "Нетология. Университет интернет-профессий будущего",
            content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → https://is.gd/xqtzIE",
            published = "21 мая в 15:28",
            likedByMe = false,
            likes = 1399,
            shares = 15999
        )

        binding.apply {
            author.text = post.author
            published.text = post.published
            content.text = post.content
            heartIcon.setImageResource(if (post.likedByMe) R.drawable.ic_baseline_favorite_red_24 else R.drawable.ic_baseline_favorite_border_24)
            likes.text = formatNumber(post.likes)
            shares.text = formatNumber(post.shares)

            heartIcon.setOnClickListener {
                post.likedByMe = !post.likedByMe
                heartIcon.setImageResource(if (post.likedByMe) R.drawable.ic_baseline_favorite_red_24 else R.drawable.ic_baseline_favorite_border_24)
                post.likes += if (post.likedByMe) 1 else -1
                likes.text = formatNumber(post.likes)
            }

            shareButton.setOnClickListener {
                post.shares += 10
                shares.text = formatNumber(post.shares)
            }
        }
    }
}