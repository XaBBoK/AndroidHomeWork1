package ru.netology.nmedia.presentation.fragments

import android.annotation.SuppressLint
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.internal.findRootView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.netology.nmedia.R

@SuppressLint("CustomSplashScreen")
class SplashScreenFragment : Fragment(R.layout.fragment_splash_screen) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        CoroutineScope(Dispatchers.Main).launch {
            val animationDrawable: AnimatedVectorDrawable  = resources.getDrawable(R.drawable.avd_anim) as AnimatedVectorDrawable

            val img = findRootView(requireActivity()).findViewById<AppCompatImageView>(R.id.animatedImageView)
            img.setImageDrawable(animationDrawable)
            animationDrawable.start()
            // Ждем несколько секунд
            delay(1000)
            // Переходим на основной экран

            findNavController().navigate(R.id.action_splashScreenFragment_to_feedFragment)
        }
    }
}