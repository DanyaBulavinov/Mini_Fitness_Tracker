package com.projects.minifitnesstracker.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.projects.minifitnesstracker.databinding.FragmentMainBinding
import com.projects.minifitnesstracker.viewmodel.MainViewModel
import java.text.DecimalFormat

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding: FragmentMainBinding
        get() = _binding ?: throw RuntimeException("FragmentMainBinding == null")

    private val viewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.init {
            binding.viewModel = viewModel
        }

//        viewModel.calculateAmountOfSleep()
        observeViewModel()
        setupOnClickEvents()
    }

    private fun setupOnClickEvents() {
        binding.stepsCV.setOnClickListener {
            findNavController().navigate(
                MainFragmentDirections.actionMainFragmentToStepsFragment(viewModel.trackingLiveData.value)
            )
        }
        binding.glassesCV.setOnClickListener {
            findNavController().navigate(
                MainFragmentDirections.actionMainFragmentToWaterFragment(viewModel.trackingLiveData.value)
            )
        }
        binding.sleepCV.setOnClickListener {
            findNavController().navigate(
                MainFragmentDirections.actionMainFragmentToSleepFragment(
                    viewModel.trackingLiveData.value?.amountOfSleep ?: 0,
                    viewModel.trackingLiveData.value
                )
            )
        }
        binding.bmiCV.setOnClickListener {
            findNavController().navigate(
                MainFragmentDirections.actionMainFragmentToBMI()
            )
        }
    }

    private fun observeViewModel() {
        viewModel.trackingLiveData.observe(viewLifecycleOwner) {
            it?.let {
                binding.glassesOfWater = it.glassesOfWater ?: 0
                binding.stepsWalked = breakNumberIntoDigits(it.amountOfSteps ?: 0)
                binding.amountOfSleep = viewModel.convertToHoursAndMinutes(it.amountOfSleep ?: 0)
            }
        }
        viewModel.stepsGoal.observe(viewLifecycleOwner) {
            it?.let {
                binding.stepsGoal = breakNumberIntoDigits(it)
            }
        }
        viewModel.percentageOfGoal.observe(viewLifecycleOwner) {
            binding.percentageOfGoal = it
            binding.progressBar.setProgress(it)
        }
//        viewModel.amountOfSleep.observe(viewLifecycleOwner){
//            binding.amountOfSleep = it
//        }
    }

    private fun breakNumberIntoDigits(steps: Int): String {
        val formatter = DecimalFormat("#,###")
        val formatted = formatter.format(steps)
        return formatted.toString()
    }

}