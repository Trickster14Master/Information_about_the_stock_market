package com.plcoding.stockmarketapp.presentation.company_listings

import com.plcoding.stockmarketapp.domain.model.CompanyListing

// класс в котором описываются состояния экрана
data class CompanyListingsState(
    val companies: List<CompanyListing> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val searchQuery: String = ""
)
