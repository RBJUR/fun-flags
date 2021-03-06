package com.roquebuarque.architecturecomponentssample.feature.country.presentation

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.roquebuarque.architecturecomponentssample.R
import com.roquebuarque.architecturecomponentssample.feature.country.data.entities.CountryDto
import com.roquebuarque.architecturecomponentssample.utils.doAfterTextChangedFlow
import com.roquebuarque.architecturecomponentssample.utils.hideKeyboard
import com.roquebuarque.architecturecomponentssample.utils.setOnClickListenerFlow
import com.roquebuarque.architecturecomponentssample.utils.setOnRefreshListenerFlow
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_country_list.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

@ExperimentalCoroutinesApi
@FlowPreview
@AndroidEntryPoint
class CountryListFragment : Fragment(R.layout.fragment_country_list) {

    private val viewModel: CountryListViewModel by viewModels()
    private val adapter: CountryListAdapter by lazy { CountryListAdapter(::countryListClicked) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupList()
        setupIntentBinding()
    }

    private fun setupIntentBinding() {
        intent(
            edtSearchCountry
                .doAfterTextChangedFlow()
                .map { CountryListIntent.Search(it) },
            imgCleanSearchCountryList
                .setOnClickListenerFlow()
                .map { CountryListIntent.CleanSearch },
            countryListSwipeRefresh
                .setOnRefreshListenerFlow()
                .map { CountryListIntent.Refresh }
        )
    }

    private fun intent(vararg intents: Flow<CountryListIntent>) {
        viewModel.intent(intents.asIterable().merge())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupObserver()
    }

    private fun setupObserver() {
        viewModel.state.observe(this) { state ->
            when (state) {
                is CountryListState.Loading -> load(state.isLoading)
                CountryListState.Empty -> showEmptyState()
                is CountryListState.Message -> showMessage(state)
                is CountryListState.Success -> displayList(state)
                CountryListState.ClearSearch -> clearSearch()
                is CountryListState.SearchMode -> enableSearchMode()
            }
        }
    }

    private fun enableSearchMode() {
        imgCleanSearchCountryList.isVisible = true
    }

    private fun displayList(state: CountryListState.Success) {
        rvCountries.isVisible = true
        txtEmptyCountryList.isVisible = false
        adapter.submitList(state.data)
    }

    private fun clearSearch() {
        hideKeyboard()
        edtSearchCountry.clearFocus()
        edtSearchCountry.setText("")
        imgCleanSearchCountryList.isVisible = false
    }

    private fun showMessage(message: CountryListState.Message){
        Toast.makeText(activity, message.text, Toast.LENGTH_LONG).show()
    }

    private fun showEmptyState() {
        rvCountries.isVisible = false
        txtEmptyCountryList.isVisible = true
    }

    private fun load(isRefresh: Boolean) {
        countryListSwipeRefresh.isRefreshing = isRefresh
    }

    private fun setupList() {
        rvCountries.adapter = adapter
    }

    private fun countryListClicked(countryDto: CountryDto) {
        findNavController().navigate(
            R.id.presentCountryDetail,
            bundleOf(CountryDetailFragment.COUNTRY_NAME_EXTRA to countryDto.name)
        )
    }
}