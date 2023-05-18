package com.projects.minifitnesstracker.ui.view

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.renderer.BarChartRenderer
import com.projects.minifitnesstracker.R
import com.projects.minifitnesstracker.chartutils.BarChartMarkerView
import com.projects.minifitnesstracker.chartutils.SleepBarChartMakerView
import com.projects.minifitnesstracker.databinding.FragmentSleepBinding
import com.projects.minifitnesstracker.databinding.FragmentStepsBinding
import com.projects.minifitnesstracker.model.DateSleepTuple
import com.projects.minifitnesstracker.model.DateStepsTuple
import com.projects.minifitnesstracker.viewmodel.SleepViewModel
import com.projects.minifitnesstracker.viewmodel.StepsViewModel
import kotlin.time.Duration.Companion.days

class SleepFragment : Fragment() {

    private lateinit var barChart: BarChart

    // variable for our bar data.
    private lateinit var barData: BarData

    // variable for our bar data set.
    private lateinit var barDataSet: BarDataSet

    // array list for storing entries.
    private var barEntriesList = mutableListOf<BarEntry>()

    private var _binding: FragmentSleepBinding? = null
    private val binding: FragmentSleepBinding
        get() = _binding ?: throw RuntimeException("FragmentSleepBinding == null")

    private val args by navArgs<SleepFragmentArgs>()

    private val viewModel by lazy {
        ViewModelProvider(this)[SleepViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSleepBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getData()
        barChartInit()
        initCurrentAmountOfSleep()
        addTextChangeListeners()
        initOnClickListeners()
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.dateSleepLiveData.observe(viewLifecycleOwner) {
            setBarEntries(it)
        }
        viewModel.errorStartHours.observe(viewLifecycleOwner) {
            val message = if (it) {
                getString(R.string.error_input_steps)
            } else {
                null
            }
            binding.textInputEditTextStartHours.error = message
        }
        viewModel.errorStartMinutes.observe(viewLifecycleOwner) {
            val message = if (it) {
                getString(R.string.error_input_steps)
            } else {
                null
            }
            binding.textInputEditTextStartMinutes.error = message
        }
        viewModel.errorEndHours.observe(viewLifecycleOwner) {
            val message = if (it) {
                getString(R.string.error_input_steps)
            } else {
                null
            }
            binding.textInputEditTextEndHours.error = message
        }
        viewModel.errorEndMinutes.observe(viewLifecycleOwner) {
            val message = if (it) {
                getString(R.string.error_input_steps)
            } else {
                null
            }
            binding.textInputEditTextEndMinutes.error = message
        }
        viewModel.currentSleep.observe(viewLifecycleOwner) {
            binding.currentSleep.text = it
        }
        viewModel.canGetData.observe(viewLifecycleOwner) {
            barDataSet.removeLast()
            viewModel.getData()
        }
    }

    private fun initOnClickListeners() {
        binding.setSleepButton.setOnClickListener {
            viewModel.setSleep(
                binding.textInputEditTextStartHours.text.toString(),
                binding.textInputEditTextStartMinutes.text.toString(),
                binding.textInputEditTextEndHours.text.toString(),
                binding.textInputEditTextEndMinutes.text.toString(),
                args.currentDateTrackingData!!
            )
        }
    }


    private fun initCurrentAmountOfSleep() {
        val currentAmountOfSleep = args.amountOfSleep
        viewModel.setCurrentSleep(currentAmountOfSleep)
    }

    private fun addTextChangeListeners() {
        binding.textInputEditTextStartHours.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.resetErrorStartHours()
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })
        binding.textInputEditTextStartMinutes.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.resetErrorStartMinutes()
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })
        binding.textInputEditTextEndHours.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.resetErrorEndHours()
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })
        binding.textInputEditTextEndMinutes.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.resetErrorEndMinutes()
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })
    }

    private fun notifyBarChartDataSetChanged() {
        barData.notifyDataChanged()
        barChart.notifyDataSetChanged()
        barChart.setVisibleXRangeMaximum(5f)
        barChart.invalidate()
    }

    private fun setBarEntries(entries: List<DateSleepTuple>) {
        for (entry in entries) {
            barDataSet.addEntry(
                BarEntry(
                    entry.date.date.days.inWholeDays.toFloat(),
                    entry.amountOfSleep.toFloat()
                )
            )
        }
        notifyBarChartDataSetChanged()
    }

    private fun barChartInit() {
        barChart = binding.barChart

        barDataSet = BarDataSet(barEntriesList, "")

        barDataSet.addEntry(BarEntry(1f, 0f))

        barData = BarData(barDataSet)

        barChart.data = barData

        barDataSet.removeFirst()

        barDataSet.setDrawValues(false)
        barDataSet.valueTextColor = Color.BLACK

        val barChartRenderer =
            BarChartRenderer(barChart, barChart.animator, barChart.viewPortHandler, true, 50f)
        barChart.renderer = barChartRenderer

        val markerView = SleepBarChartMakerView(requireContext(), R.layout.custom_marker_layout)
        barChart.marker = markerView

        barChart.axisLeft.axisMinimum = 0f
        barChart.axisRight.axisMinimum = 0f

        barChart.xAxis.yOffset = -10f

        barChart.xAxis.labelCount = 31

        barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM_INSIDE
        barChart.xAxis.granularity = 1f

        barChart.xAxis.setDrawLabels(true)
        barChart.xAxis.setDrawAxisLine(false)
        barChart.xAxis.setDrawGridLines(false)

        barChart.axisRight.setDrawGridLines(false)
        barChart.axisRight.setDrawLabels(false)
        barChart.axisRight.setDrawAxisLine(false)

        barChart.axisLeft.setDrawLabels(false)
        barChart.axisLeft.setDrawAxisLine(false)
        barChart.axisLeft.setDrawGridLines(false)

        barChart.animateY(800)

        barChart.setScaleEnabled(false)

        barChart.centerViewTo(40f, 0f, YAxis.AxisDependency.RIGHT)

        barChart.legend.isEnabled = false;
        barChart.description.isEnabled = false
    }
}