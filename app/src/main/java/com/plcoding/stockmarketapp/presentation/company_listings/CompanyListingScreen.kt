package com.plcoding.stockmarketapp.presentation.company_listings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.plcoding.stockmarketapp.presentation.company_listings.widgets.CompanyItem
import com.plcoding.stockmarketapp.presentation.destinations.CompanyInfoScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
@Destination(start = true)
fun CompanyListingScreen(
    navigation:DestinationsNavigator,
    viewModels: CompanyListingsViewModels= hiltViewModel()
) {
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = viewModels.state.isRefreshing)

    val state = viewModels.state

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // виджет строки поиска
        OutlinedTextField(
            value = state.searchQuery, onValueChange = {
                viewModels.onEvent(
                    CompanyListingsEvent.OnSearchQueryChange(it)
                )
            },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            placeholder = {
                Text(text = "Search..")
            },
            maxLines = 1,
            singleLine = true
        )

        // виджет обновления
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = {
                viewModels.onEvent(CompanyListingsEvent.Refresh)
            }
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ){
                items(state.companies.size){i->
                    val company = state.companies[i]
                    CompanyItem(company = company, modifier = Modifier.fillMaxWidth().clickable {
                        navigation.navigate(CompanyInfoScreenDestination(company.symbol))
                    }.padding(16.dp))
                    if(i<state.companies.size){
                        Divider(modifier = Modifier.padding(horizontal = 16.dp))
                    }
                }
            }
        }
    }
}