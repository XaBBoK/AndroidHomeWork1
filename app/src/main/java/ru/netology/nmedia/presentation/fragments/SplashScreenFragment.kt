package ru.netology.nmedia.presentation.fragments

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.internal.findRootView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.netology.nmedia.R
import ru.netology.nmedia.utils.setupActionBarWithNavControllerDefault

@SuppressLint("CustomSplashScreen")
class SplashScreenFragment : Fragment(R.layout.fragment_splash_screen) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as? AppCompatActivity)?.apply {
            supportActionBar?.hide()
        }

        (activity as? AppCompatActivity)?.apply {
            val color = getThemeAttributeValue(theme, androidx.appcompat.R.attr.background)
            window.statusBarColor = color
        }


        lifecycleScope.launch {
            val animationDrawable: AnimatedVectorDrawable =
                resources.getDrawable(R.drawable.avd_anim) as AnimatedVectorDrawable

            val img =
                findRootView(requireActivity()).findViewById<AppCompatImageView>(R.id.animatedImageView)
            img.setImageDrawable(animationDrawable)
            animationDrawable.start()
            // Ждем несколько секунд
            delay(1000)
            // Переходим на основной экран

            findNavController().apply {
                navigate(R.id.action_splashScreenFragment_to_feedFragment)
                graph = navInflater.inflate(R.navigation.nav_main)
                    .also { it.setStartDestination(R.id.feedFragment) }
                (requireActivity() as? AppCompatActivity)?.apply {
                    setupActionBarWithNavControllerDefault()
                }
            }
        }
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
}