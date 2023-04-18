package ru.netology.nmedia.presentation.fragments

import android.content.res.Resources
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentImageViewerBinding
import ru.netology.nmedia.utils.load

class ImageViewerFullscreen : Fragment(R.layout.fragment_image_viewer) {
    private val binding: FragmentImageViewerBinding by viewBinding(
        FragmentImageViewerBinding::bind
    )

    private var image: String? = null
    private var dummyButton: Button? = null

    override fun onStart() {
        //установка черного цвета заголовка
        (activity as? AppCompatActivity)?.apply {
            val color = getThemeAttributeValue(
                ContextThemeWrapper(this, R.style.Theme_NMedia_Black).theme,
                androidx.appcompat.R.attr.colorPrimaryDark
            )
            window.statusBarColor = color
            supportActionBar?.setBackgroundDrawable(color.toDrawable())
        }
        super.onStart()
    }

    override fun onStop() {
        //восстановление обычного цвета заголовка из темы
        (activity as? AppCompatActivity)?.apply {
            val color = getThemeAttributeValue(theme, androidx.appcompat.R.attr.colorPrimaryDark)
            window.statusBarColor = color
            supportActionBar?.setBackgroundDrawable(color.toDrawable())
        }

        super.onStop()
    }

    private fun getThemeAttributeValue(
        themeValue: Resources.Theme,
        attribute: Int
    ): Int {
        val typedVal = TypedValue()

        themeValue.resolveAttribute(
            attribute, typedVal, true
        )

        return typedVal.data
    }

    //val navHostController by lazy { (requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController }
    //val appBarConf by lazy { AppBarConfiguration(navHostController.graph) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //добавляем верхнее меню с кнопкой назад
        /*(activity as? AppCompatActivity)?.apply {
            setSupportActionBar(binding.toolbar)
            setupActionBarWithNavControllerDefault()
            //supportActionBar?.setDisplayHomeAsUpEnabled(true)
            //supportActionBar?.title = navHostController.currentDestination?.label.toString()
        }*/

        image = arguments?.getString(
            INTENT_EXTRA_IMAGE_URI
        )

        binding.imageViewerFullscreen.load(
            url = image.toString(), placeholder = R.drawable.ic_loading_placeholder
        )

        dummyButton = binding.dummyButton
    }

}