package com.projects.minifitnesstracker.ui.view

import android.graphics.Color
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.projects.minifitnesstracker.databinding.FragmentWaterBinding
import com.projects.minifitnesstracker.model.DateGlassesTuple
import com.projects.minifitnesstracker.model.DateStepsTuple
import com.projects.minifitnesstracker.viewmodel.StepsViewModel
import com.projects.minifitnesstracker.viewmodel.WaterViewModel
import kotlin.time.Duration.Companion.days

class WaterFragment : Fragment() {

    private lateinit var barChart: BarChart

    // variable for our bar data.
    private lateinit var barData: BarData

    // variable for our bar data set.
    private lateinit var barDataSet: BarDataSet

    // array list for storing entries.
    private var barEntriesList = mutableListOf<BarEntry>()

    private var _binding: FragmentWaterBinding? = null
    private val binding: FragmentWaterBinding
        get() = _binding ?: throw RuntimeException("FragmentWaterBinding == null")

    private val args by navArgs<WaterFragmentArgs>()

    private val viewModel by lazy {
        ViewModelProvider(this)[WaterViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWaterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getData()
        barChartInit()
        initCurrentGlasses()
        addTextChangeListeners()
        initOnClickListeners()
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.dateGlassesLiveData.observe(viewLifecycleOwner) {
            setBarEntries(it)
        }
        viewModel.errorGlasses.observe(viewLifecycleOwner) {
            val message = if (it) {
                getString(R.string.error_input_steps)
            } else {
                null
            }
            binding.textInputEditText.error = message
        }
        viewModel.currentGlasses.observe(viewLifecycleOwner) {
            binding.currentGlassesData = it
        }
        viewModel.canGetData.observe(viewLifecycleOwner) {
            barDataSet.removeLast()
            viewModel.getData()
        }
    }

    private fun initOnClickListeners() {
        binding.setGlassesButton.setOnClickListener {
            viewModel.setGlasses(
                binding.textInputEditText.text.toString(),
                args.currentDateTrackingData!!
            )
        }
    }


    private fun initCurrentGlasses() {
        val currentDataTrackingDate = args.currentDateTrackingData
        viewModel.setCurrentGlasses(currentDataTrackingDate?.glassesOfWater!!)
    }

    private fun addTextChangeListeners() {
        binding.textInputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.resetErrorGlasses()
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

    private fun setBarEntries(entries: List<DateGlassesTuple>) {
        for (entry in entries) {
            barDataSet.addEntry(
                BarEntry(
                    entry.date.date.days.inWholeDays.toFloat(),
                    entry.amountOfGlasses.toFloat()
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