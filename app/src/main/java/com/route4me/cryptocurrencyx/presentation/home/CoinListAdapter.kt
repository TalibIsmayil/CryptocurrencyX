package com.route4me.cryptocurrencyx.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.route4me.cryptocurrencyx.common.Constants
import com.route4me.cryptocurrencyx.databinding.CoinListItemBinding
import com.route4me.cryptocurrencyx.domain.model.Data

class CoinListAdapter : RecyclerView.Adapter<CoinListAdapter.CoinListVH>() {

    private val dataList: ArrayList<Data> = arrayListOf()

    private var clickListener: ((Data) -> Unit)? = null

    fun setOnItemClickListener(listener: (Data) -> Unit) {
        clickListener = listener
    }

    fun setList(list: List<Data>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

    inner class CoinListVH(private val binding: CoinListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(model: Data) {
            binding.apply {
                "${model.cmcRank}. ${model.name} (${model.symbol})".also { coinName.text = it }
                "$${
                    String.format(
                        Constants.PRICE_FORMAT,
                        model.quote?.usd?.price
                    )
                }".also { exchangeRate.text = it }
                itemView.setOnClickListener {
                    clickListener?.invoke(model)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoinListVH {
        val binding =
            CoinListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CoinListVH(binding)
    }

    override fun onBindViewHolder(holder: CoinListVH, position: Int) {
        dataList.getOrNull(position)?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int = dataList.size
}