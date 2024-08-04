package com.plcoding.stockmarketapp.presentation.company_info

import com.plcoding.stockmarketapp.domain.model.CompanyInfo
import com.plcoding.stockmarketapp.domain.model.IntroDayInfo

data class CompanyInfoState(
    val stockInfo:List<IntroDayInfo> = emptyList(),
    val company: CompanyInfo?=null,
    val isLoading:Boolean=false,
    val error: String? = null
)
