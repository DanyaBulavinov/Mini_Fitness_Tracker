package com.projects.minifitnesstracker.ui.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.projects.minifitnesstracker.R
import com.projects.minifitnesstracker.databinding.FragmentBmiBinding
import com.projects.minifitnesstracker.databinding.FragmentMainBinding
import com.projects.minifitnesstracker.viewmodel.BmiViewModel

class BmiFragment : Fragment() {

    private var _binding: FragmentBmiBinding? = null
    private val binding: FragmentBmiBinding
        get() = _binding ?: throw RuntimeException("FragmentBmiBinding == null")

    private val viewModel by lazy {
        ViewModelProvider(this)[BmiViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBmiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initOnClickListeners()
        addTextChangeListeners()
        observeViewModel()
    }

    private fun observeViewModel(){
        viewModel.errorHeight.observe(viewLifecycleOwner) {
            val message = if (it) {
                getString(R.string.error_input_steps)
            } else {
                null
            }
            binding.textInputEditTextHeight.error = message
        }
        viewModel.errorWeight.observe(viewLifecycleOwner) {
            val message = if (it) {
                getString(R.string.error_input_steps)
            } else {
                null
            }
            binding.textInputEditTextWeight.error = message
        }
        viewModel.bmiScore.observe(viewLifecycleOwner){
            binding.bmiScore = it.toString()
        }
        viewModel.bmiStatus.observe(viewLifecycleOwner){
            binding.weightStatus = it
        }
    }

    private fun addTextChangeListeners() {
        binding.textInputEditTextWeight.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.resetErrorWeight()
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })
        binding.textInputEditTextHeight.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.resetErrorHeight()
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }

    private fun initOnClickListeners(){
        binding.calculateButton.setOnClickListener{
            viewModel.calculateBMI(binding.textInputEditTextHeight.text?.toString(), binding.textInputEditTextWeight.text?.toString())
        }
    }

}