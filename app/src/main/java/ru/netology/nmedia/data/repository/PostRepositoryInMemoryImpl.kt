package ru.netology.nmedia.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.domain.repository.PostRepository
import ru.netology.nmedia.dto.Post

class PostRepositoryInMemoryImpl : PostRepository {
    private var post = Post(
        id = 1,
        author = "Нетология. Университет интернет-профессий будущего",
        content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → https://is.gd/xqtzIE",
        published = "21 мая в 15:28",
        likedByMe = false,
        likes = 999,
        shares = 15999
    )

    private val data = MutableLiveData(post)

    override fun get(): LiveData<Post> {
        return data
    }

    override fun like() {
        post = post.copy(
            likedByMe = !post.likedByMe,
            likes = post.likes + if (post.likedByMe) -1 else 1
        )
        data.value = post
    }

    override fun share() {
        post = post.copy(shares = post.shares + 10)
        data.value = post
    }

}
