package com.roquebuarque.architecturecomponentssample.feature.country.presentation

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou
import com.roquebuarque.architecturecomponentssample.R
import com.roquebuarque.architecturecomponentssample.feature.country.data.entities.CountryDto
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_country_detail.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview


@FlowPreview
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class CountryDetailFragment : Fragment(R.layout.fragment_country_detail) {

    private val viewModel: CountryDetailViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getArgumentsFromPrevFragment()
    }

    private fun getArgumentsFromPrevFragment() {
        arguments?.getString(COUNTRY_NAME_EXTRA)?.let {
            viewModel.start(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupObserver()
    }

    private fun setupObserver() {
        viewModel.state.observe(this) { country ->
            bindCountry(country)
        }
    }

    private fun bindCountry(country: CountryDto) {
        txtCountryNameDetail.text = country.name
        txtCapitalDetail.text = getString(R.string.capital, country.capital)
        txtRegionDetail.text = getString(R.string.region, country.region)
        txtSubRegionDetail.text = getString(R.string.sub_region, country.subRegion)
        txtPopulationDetail.text = getString(R.string.population, "%,d".format(country.population))

        GlideToVectorYou
            .init()
            .with(activity)
            .load(Uri.parse(country.flag), imgCountryDetail)

    }


    companion object {
        const val COUNTRY_NAME_EXTRA = "COUNTRY_NAME_EXTRA"
    }

}