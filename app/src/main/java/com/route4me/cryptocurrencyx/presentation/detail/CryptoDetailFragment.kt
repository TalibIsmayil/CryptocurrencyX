package com.route4me.cryptocurrencyx.presentation.detail

import android.graphics.Color
import android.graphics.DashPathEffect
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.Legend.LegendForm
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.LimitLine.LimitLabelPosition
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.Utils
import com.route4me.cryptocurrencyx.R
import com.route4me.cryptocurrencyx.common.Constants
import com.route4me.cryptocurrencyx.common.Resource
import com.route4me.cryptocurrencyx.databinding.FragmentCryptoDetailBinding
import com.route4me.cryptocurrencyx.domain.model.Data
import dagger.hilt.android.AndroidEntryPoint
import java.util.ArrayList

@AndroidEntryPoint
class CryptoDetailFragment : Fragment() {

    private lateinit var cryptoDetailBinding: FragmentCryptoDetailBinding

    val detailViewModel: DetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        cryptoDetailBinding = FragmentCryptoDetailBinding.inflate(inflater, container, false)
        return cryptoDetailBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observeChanges()

    }

    private fun initView() {
        cryptoDetailBinding.apply {
            detailToolbar.setNavigationOnClickListener {
                activity?.onBackPressed()
            }
            chart.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.grayBackground
                )
            )
            chart.description.isEnabled = false

            // enable touch gestures
            chart.setTouchEnabled(true)

            // enable scaling and dragging
            chart.isDragEnabled = true
            chart.setScaleEnabled(true)

            // force pinch zoom along both axis
            chart.setPinchZoom(true)
        }
        arguments?.getInt(Constants.CRYPTO_COIN_ID)?.let {
            detailViewModel.getLatestCoins(it)
        }
    }

    private fun observeChanges() {
        detailViewModel.state.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Loading -> {
                    cryptoDetailBinding.detailLoading.visibility = View.VISIBLE
                }
                is Resource.Error -> {
                    cryptoDetailBinding.apply {
                        detailLoading.visibility = View.GONE
                        detailErrorText.visibility = View.VISIBLE
                        detailErrorText.text = it.message
                    }
                }
                is Resource.Success -> {
                    setSuccesPage(it)
                }
            }
        })
    }

    private fun setSuccesPage(it: Resource<Data>) {
        cryptoDetailBinding.card.visibility = View.VISIBLE
        cryptoDetailBinding.detailLoading.visibility = View.GONE
        "${it.data?.cmcRank}. ${it.data?.name} (${it.data?.symbol})".also {
            cryptoDetailBinding.coinName.text = it
        }
        cryptoDetailBinding.coinPrice.text =
            "$${String.format(Constants.PRICE_FORMAT, it.data?.quote?.usd?.price)}"
        cryptoDetailBinding.change.text =
            "${
                String.format(
                    Constants.PRICE_FORMAT,
                    (it.data?.quote?.usd?.percentChange24h ?: 0.0).unaryPlus()
                )
            }%"
        val isLow = it.data?.quote?.usd?.percentChange24h ?: -1.0 < 0.0
        if (isLow) {
            cryptoDetailBinding.change.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.red_rounded_bg
                )
            )
            cryptoDetailBinding.change.setCompoundDrawablesWithIntrinsicBounds(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_round_arrow_drop_down_24
                ), null, null, null
            )
        } else {
            cryptoDetailBinding.change.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.green_rounded_bg
                )
            )
            cryptoDetailBinding.change.setCompoundDrawablesWithIntrinsicBounds(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_round_arrow_drop_up_24
                ), null, null, null
            )
        }


        var xAxis = cryptoDetailBinding.chart.xAxis
        xAxis.apply {
            xAxis.mAxisMaximum = 7f
            xAxis.axisMinimum = 1f
        }

        var yAxis = cryptoDetailBinding.chart.axisLeft

        // disable dual axis (only use LEFT axis)
        cryptoDetailBinding.chart.axisRight.isEnabled = false

        // horizontal grid lines
        yAxis.enableGridDashedLine(10f, 10f, 0f)

        // axis range

        yAxis.setDrawLimitLinesBehindData(true)
        xAxis.setDrawLimitLinesBehindData(true)
        setData(it.data, isLow)


        val l: Legend = cryptoDetailBinding.chart.legend

        l.form = LegendForm.LINE
    }


    private fun setData(datas: Data?, isLow: Boolean) {
        val currentPrice = datas?.quote?.usd?.price ?: 1.0
        val change7d = datas?.quote?.usd?.percentChange7d ?: 1.0
        val change24h = datas?.quote?.usd?.percentChange24h ?: 1.0

        val values = ArrayList<Entry>()
        values.add(Entry(1f, currentPrice.plus(change7d).toFloat()))
        values.add(Entry(6f, currentPrice.plus(change24h).toFloat()))
        values.add(Entry(7f, currentPrice.toFloat()))

        var set1: LineDataSet? = null
        if (cryptoDetailBinding.chart.data != null &&
            cryptoDetailBinding.chart.data.dataSetCount > 0
        ) {
            set1 = cryptoDetailBinding.chart.data.getDataSetByIndex(0) as LineDataSet
            set1.values = values
            set1.notifyDataSetChanged()
            cryptoDetailBinding.chart.data.notifyDataChanged()
            cryptoDetailBinding.chart.notifyDataSetChanged()
        } else {
            // create a dataset and give it a type
            set1 = LineDataSet(values, datas?.symbol)
            set1.setDrawIcons(true)


            // black lines and points
            set1.color = Color.BLACK
            set1.setCircleColor(Color.BLACK)

            // line thickness and point size
            set1.lineWidth = 1f
            set1.circleRadius = 3f

            // draw points as solid circles
            set1.setDrawCircleHole(false)

            // customize legend entry
            set1.formLineWidth = 1f
            set1.formSize = 15f

            // text size of values
            set1.valueTextSize = 9f

            // set the filled area
            set1.setDrawFilled(true)
            set1.fillFormatter =
                IFillFormatter { dataSet, dataProvider ->
                    cryptoDetailBinding.chart.getAxisLeft().getAxisMinimum()
                }

            // set color of filled area
            if (Utils.getSDKInt() >= 18) {
                // drawables only supported on api level 18 and above
                if (isLow) {
                    val drawable =
                        ContextCompat.getDrawable(requireContext(), R.drawable.red_rounded_bg)
                    set1.fillDrawable = drawable
                } else {
                    val drawable =
                        ContextCompat.getDrawable(requireContext(), R.drawable.green_rounded_bg)
                    set1.fillDrawable = drawable
                }

            } else {
                set1.fillColor = Color.BLACK
            }
            val dataSets = ArrayList<ILineDataSet>()
            dataSets.add(set1) // add the data sets

            // create a data object with the data sets
            val data = LineData(dataSets)

            // set data
            cryptoDetailBinding.chart.data = data
        }
    }
}