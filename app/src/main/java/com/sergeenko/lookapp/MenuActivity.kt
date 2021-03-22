package com.sergeenko.lookapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDestination
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.sergeenko.lookapp.databinding.PostTapeFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MenuActivity : AppCompatActivity() {

    private lateinit var binding: PostTapeFragmentBinding
    @Inject
    lateinit var repository: Repository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PostTapeFragmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.newLook.setOnClickListener {
            navigateTo(R.id.action_global_newLookFragment)
            binding.newPostLook.visibility = View.GONE
        }

        binding.newPost.setOnClickListener {
            navigateTo(R.id.action_global_newPostFragment)
            binding.newPostLook.visibility = View.GONE
        }

        Navigation
                .findNavController(this, R.id.fragment2)
                .addOnDestinationChangedListener { controller, destination, arguments ->
                    manageDestination(destination)
                }


        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.person -> logOut()
                R.id.scrolling -> navigateTo(R.id.action_global_lookScrollingFragment)
                R.id.add -> {
                    if(binding.newPostLook.visibility == View.VISIBLE){
                        binding.newPostLook.visibility = View.GONE
                    }else{
                        binding.newPostLook.visibility = View.VISIBLE
                        binding.newPostLook.bringToFront()
                    }
                }
            }

            return@setOnNavigationItemSelectedListener true
        }
    }

    private fun navigateTo(action: Int) {
        Navigation
            .findNavController(this, R.id.fragment2)
            .navigate(action)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        binding.newPostLook.visibility = View.GONE
        return super.onTouchEvent(event)

    }

    private fun logOut() {
        lifecycleScope.launch {
            repository.logout()
                    .catch {
                        Toast.makeText(applicationContext, it.message, Toast.LENGTH_SHORT).show()
                    }
                    .collect {
                        startActivity(Intent(applicationContext, MainNavigation::class.java))
                        finish()
                    }

        }
    }

    private fun manageDestination(destination: NavDestination) {
        when (destination.id) {
            R.id.commentsFragment, R.id.newPostFragment -> {
                binding.bottomNavigationView.visibility = View.GONE
            }
            else -> {
                binding.bottomNavigationView.visibility = View.VISIBLE
            }
        }
    }
}