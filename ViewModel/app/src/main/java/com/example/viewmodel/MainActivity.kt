package com.example.viewmodel

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.viewmodel.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

//        var counter = 1
//        binding.textView.text = counter.toString()
//
//        binding.button.setOnClickListener {
//            counter += 1
//            binding.textView.text = counter.toString()
//        }

//        val mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
//        mainViewModel.counter = 1
//        binding.textView.text = mainViewModel.counter.toString()
//
//        binding.button.setOnClickListener {
//            mainViewModel.counter += 1
//            binding.textView.text = mainViewModel.counter.toString()
//        }

        val factory = MainViewModelFactory(1)
//        val mainViewModel = ViewModelProvider(this, factory).get(MainViewModel::class.java)
        val mainViewModel by viewModels<MainViewModel> { factory }
        binding.textView.text = mainViewModel.counter.toString()

        binding.button.setOnClickListener {
            mainViewModel.counter += 1
            binding.textView.text = mainViewModel.counter.toString()
        }
    }

}