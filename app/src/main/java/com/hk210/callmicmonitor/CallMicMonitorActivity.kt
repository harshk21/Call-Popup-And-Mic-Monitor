package com.hk210.callmicmonitor

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.hk210.callmicmonitor.databinding.CallMicMonitorActivityBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CallMicMonitorActivity : AppCompatActivity() {

    private var _binding: CallMicMonitorActivityBinding? = null
    private val binding: CallMicMonitorActivityBinding
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = CallMicMonitorActivityBinding.inflate(layoutInflater)
        binding.root.apply {
            setContentView(this)
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupBottomNavigation()
    }

    fun setToolbarTitle(title: String) {
        binding.toolbarTitle.text = title
    }

    private fun setupBottomNavigation() {
        val navHostFragment =
            binding.fragmentContainerView.getFragment<NavHostFragment>()
        binding.bottomNavView.setupWithNavController(navHostFragment.navController)
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}
