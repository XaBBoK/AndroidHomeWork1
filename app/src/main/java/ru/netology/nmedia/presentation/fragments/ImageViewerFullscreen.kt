package ru.netology.nmedia.presentation.fragments

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentImageViewerBinding
import ru.netology.nmedia.utils.load
import ru.netology.nmedia.utils.setupActionBarWithNavControllerDefault

class ImageViewerFullscreen : Fragment(R.layout.fragment_image_viewer) {
    private val binding: FragmentImageViewerBinding by viewBinding(
        FragmentImageViewerBinding::bind
    )

    private var image: String? = null
    private var dummyButton: Button? = null

    override fun onStart() {
        //установка черного цвета заголовка
        (activity as? AppCompatActivity)?.apply {
            window.statusBarColor = resources.getColor(R.color.black)
        }
        super.onStart()
    }

    override fun onStop() {
        //восстановление обычного цвета заголовка из темы
        (activity as? AppCompatActivity)?.apply {
            val typedVal = TypedValue()

            theme.resolveAttribute(
                androidx.appcompat.R.attr.colorPrimaryDark, typedVal, true
            )

            val color = typedVal.data
            window.statusBarColor = color
        }

        super.onStop()
    }

    //val navHostController by lazy { (requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController }
    //val appBarConf by lazy { AppBarConfiguration(navHostController.graph) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //добавляем верхнее меню с кнопкой назад
        (activity as? AppCompatActivity)?.apply {
            setSupportActionBar(binding.toolbar)
            setupActionBarWithNavControllerDefault()
            //supportActionBar?.setDisplayHomeAsUpEnabled(true)
            //supportActionBar?.title = navHostController.currentDestination?.label.toString()
        }

        image = arguments?.getString(
            INTENT_EXTRA_IMAGE_URI
        )

        binding.imageViewerFullscreen.load(
            url = image.toString(), placeholder = R.drawable.ic_loading_placeholder
        )

        dummyButton = binding.dummyButton
    }

}