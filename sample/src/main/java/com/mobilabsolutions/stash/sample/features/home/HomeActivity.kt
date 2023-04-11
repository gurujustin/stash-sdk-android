package com.mobilabsolutions.stash.sample.features.home

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.airbnb.mvrx.viewModel
import com.airbnb.mvrx.withState
import com.mobilabsolutions.stash.sample.R
import com.mobilabsolutions.stash.sample.databinding.ActivityHomeBinding
import com.mobilabsolutions.stash.sample.shared.SampleActivity
import javax.inject.Inject

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 16-08-2019.
 */
class HomeActivity : SampleActivity() {
    @Inject
    lateinit var homeNavigationViewModelFactory: HomeActivityViewModel.Factory

    private val viewModel: HomeActivityViewModel by viewModel()
    private lateinit var binding: ActivityHomeBinding

    private val navController: NavController
        get() = (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)

        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (navController.currentDestination?.id != destination.id) {
                navController.navigate(destination.id)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.subscribe(this) { postInvalidate() }
    }

    override fun invalidate() {
        withState(viewModel) {
            binding.state = it
        }
    }
}