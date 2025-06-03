package com.mahmutgunduz.togemi.view

import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.animation.AnticipateOvershootInterpolator
import android.view.animation.OvershootInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.mahmutgunduz.togemi.R
import com.mahmutgunduz.togemi.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var pulseAnimator: ValueAnimator? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation()
    }
    
    private fun setupBottomNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(binding.fragmentContainerView.id) as NavHostFragment
        val navController = navHostFragment.navController

        // Connect BottomNavigationView with NavController
        binding.bottomNavigationView.setupWithNavController(navController)
        
        // Apply entrance animation for the container
        binding.bottomNavContainer.alpha = 0f
        binding.bottomNavContainer.translationY = 100f
        
        ViewCompat.animate(binding.bottomNavContainer)
            .translationY(0f)
            .alpha(1f)
            .setDuration(1000)
            .setStartDelay(300)
            .setInterpolator(AnticipateOvershootInterpolator(1.0f))
            .start()
            
        // Apply a subtle scale animation to the container
        binding.bottomNavContainer.scaleX = 0.9f
        binding.bottomNavContainer.scaleY = 0.9f
        
        ViewCompat.animate(binding.bottomNavContainer)
            .scaleX(1.0f)
            .scaleY(1.0f)
            .setDuration(800)
            .setStartDelay(500)
            .setInterpolator(OvershootInterpolator(0.8f))
            .start()
            
        // Add subtle shadow elevation animation
        startElevationAnimation()
            
        // Handle destination changes
        navController.addOnDestinationChangedListener { _, destination, _ ->
            // Apply a subtle bounce effect when changing destinations
            binding.bottomNavContainer.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(100)
                .withEndAction {
                    binding.bottomNavContainer.animate()
                        .scaleX(1.0f)
                        .scaleY(1.0f)
                        .setDuration(300)
                        .setInterpolator(OvershootInterpolator(0.8f))
                        .start()
                }
                .start()
        }
    }
    
    private fun startElevationAnimation() {
        pulseAnimator = ValueAnimator.ofFloat(8f, 16f, 8f).apply {
            duration = 3000
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener {
                binding.bottomNavContainer.cardElevation = it.animatedValue as Float
            }
            start()
        }
    }
    
    override fun onDestroy() {
        pulseAnimator?.cancel()
        super.onDestroy()
    }
}