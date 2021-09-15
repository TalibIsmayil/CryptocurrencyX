package com.route4me.cryptocurrencyx.presentation.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.route4me.cryptocurrencyx.R
import com.route4me.cryptocurrencyx.common.Constants
import com.route4me.cryptocurrencyx.common.Resource
import com.route4me.cryptocurrencyx.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {


    private val homeViewModel: HomeViewModel by viewModels()

    private lateinit var homeBinding: FragmentHomeBinding

    private lateinit var mAdapter: CoinListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeBinding = FragmentHomeBinding.inflate(inflater, container, false)
        return homeBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        observeChanges()
    }

    private fun initView() {
        mAdapter = CoinListAdapter()
        homeBinding.coinListRV.adapter = mAdapter
        mAdapter.setOnItemClickListener {
            findNavController().navigate(
                R.id.to_cryptoDetailFragment, bundleOf(
                    Constants.CRYPTO_COIN_ID to it.id
                )
            )
        }
    }

    private fun observeChanges() {
        homeViewModel.state.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Loading -> {
                    homeBinding.homeLoading.visibility = View.VISIBLE
                }
                is Resource.Error -> {
                    homeBinding.apply {
                        homeLoading.visibility = View.GONE
                        errorText.visibility = View.VISIBLE
                        errorText.text = it.message
                    }
                }
                is Resource.Success -> {
                    homeBinding.homeLoading.visibility = View.GONE
                    it.data?.let { data -> mAdapter.setList(data) }
                }
            }
        })
    }


}