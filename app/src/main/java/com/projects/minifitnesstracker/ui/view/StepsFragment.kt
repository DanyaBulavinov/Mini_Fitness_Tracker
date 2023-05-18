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
import com.projects.minifitnesstracker.databinding.FragmentStepsBinding
import com.projects.minifitnesstracker.model.DateStepsTuple
import com.projects.minifitnesstracker.viewmodel.StepsViewModel
import kotlin.time.Duration.Companion.days


class StepsFragment : Fragment() {

    private lateinit var barChart: BarChart

    // variable for our bar data.
    private lateinit var barData: BarData

    // variable for our bar data set.
    private lateinit var barDataSet: BarDataSet

    // array list for storing entries.
    private var barEntriesList = mutableListOf<BarEntry>()

    private var _binding: FragmentStepsBinding? = null
    private val binding: FragmentStepsBinding
        get() = _binding ?: throw RuntimeException("FragmentStepsBinding == null")

    private val args by navArgs<StepsFragmentArgs>()

    private val viewModel by lazy {
        ViewModelProvider(this)[StepsViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStepsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getData()
        barChartInit()
        initCurrentSteps()
        addTextChangeListeners()
        initOnClickListeners()
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.dateStepsLiveData.observe(viewLifecycleOwner) {
            setBarEntries(it)
        }
        viewModel.errorSteps.observe(viewLifecycleOwner) {
            val message = if (it) {
                getString(R.string.error_input_steps)
            } else {
                null
            }
            binding.textInputEditText.error = message
        }
        viewModel.currentSteps.observe(viewLifecycleOwner) {
            binding.currentStepsData = it
        }
        viewModel.canGetData.observe(viewLifecycleOwner) {
            barDataSet.removeLast()
            viewModel.getData()
        }
    }

    private fun initOnClickListeners() {
        binding.setStepsButton.setOnClickListener {
            viewModel.setSteps(
                binding.textInputEditText.text.toString(),
                args.currentDateTrackingData!!
            )
        }
    }


    private fun initCurrentSteps() {
        val currentDataTrackingDate = args.currentDateTrackingData
        viewModel.setCurrentSteps(currentDataTrackingDate?.amountOfSteps!!)
    }

    private fun addTextChangeListeners() {
        binding.textInputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.resetErrorSteps()
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

    private fun setBarEntries(entries: List<DateStepsTuple>) {
        for (entry in entries) {
            barDataSet.addEntry(
                BarEntry(
                    entry.date.date.days.inWholeDays.toFloat(),
                    entry.amountOfSteps.toFloat()
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

        val markerView = BarChartMarkerView(requireContext(), R.layout.custom_marker_layout)
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



























