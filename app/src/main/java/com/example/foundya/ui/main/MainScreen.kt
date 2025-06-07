package com.example.foundya.ui.main

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.foundya.R
import com.example.foundya.ui.Composables.ItemCard.ClaimState
import com.example.foundya.ui.Composables.ItemCard.ItemCard
import com.example.foundya.ui.Composables.ItemCard.PostListViewModel
import com.example.foundya.ui.theme.FoundYaTheme

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: PostListViewModel = hiltViewModel()
) {
    val posts = viewModel.posts.value
    val placeholderUrl = viewModel.placeholderUrl.value
    val claimStates = viewModel.claimStates.value

    FoundYaTheme {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.app_name)) }
                )
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(posts) { post ->
                    val claimState = claimStates[post.id] ?: ClaimState()
                    ItemCard(
                        post = post,
                        placeholderUrl = placeholderUrl,
                        onClaimClick = { viewModel.onClaimClick(post.id) },
                        isClaiming = claimState.isClaiming,
                        isClaimed = claimState.isClaimed,
                        errorMessage = claimState.errorMessage,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}