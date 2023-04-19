package ru.netology.nmedia.presentation.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging
import ru.netology.nmedia.R
import ru.netology.nmedia.di.DependencyContainer
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.presentation.fragments.INTENT_EXTRA_POST
import ru.netology.nmedia.utils.setupActionBarWithNavControllerDefault

class ActivityApp : AppCompatActivity(R.layout.activity_app) {
    private val dependencyContainer = DependencyContainer.getInstance()
    private val navHostController by lazy { (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController }
    private val appBarConf by lazy { AppBarConfiguration(navHostController.graph) }
    override fun onSupportNavigateUp(): Boolean {
        return navHostController.navigateUp(appBarConf) || super.onSupportNavigateUp()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_app)

        checkGoogleApiAvailability()
        handleIntent()

        setupActionBarWithNavControllerDefault()

        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            try {
                if (it.isComplete) {
                    val firebaseToken = it.result.toString()
                    this.getPreferences(Context.MODE_PRIVATE).edit().putString("fbt", firebaseToken)
                        .apply()
                    println(firebaseToken)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    private fun checkGoogleApiAvailability() {
        with(GoogleApiAvailability.getInstance()) {
            val code = isGooglePlayServicesAvailable(this@ActivityApp)
            if (code == ConnectionResult.SUCCESS) {
                return@with
            }
            if (isUserResolvableError(code)) {
                getErrorDialog(this@ActivityApp, code, 9000)?.show()
                return
            }
            Toast.makeText(this@ActivityApp, "Google Api Unavailable", Toast.LENGTH_LONG).show()
        }

        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            println(it)
        }
    }

    private fun handleIntent() {
        intent?.let {
            val text = it.getStringExtra(Intent.EXTRA_TEXT)
            intent.removeExtra(Intent.EXTRA_TEXT)

            if (it.action != Intent.ACTION_SEND)
                return@let

            if (!text.isNullOrBlank()) {
                findNavController(R.id.nav_host_fragment).navigate(
                    R.id.feedFragmentToEditPostFragment,
                    bundleOf(Pair(INTENT_EXTRA_POST, Post().copy(content = text)))
                )
            } else {
                Snackbar.make(
                    findViewById(android.R.id.content),
                    "Ошибка",
                    BaseTransientBottomBar.LENGTH_INDEFINITE
                )
                    .setAction("OK") {
                        finish()
                    }
                    .show()
                return
            }
        }
    }
}