package com.example.foundya.ui.main

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.compose.FoundYaTheme
import com.example.foundya.R
import com.example.foundya.ui.Composables.AddPost.AddPostDialog
import com.example.foundya.ui.Composables.ItemCard.ItemCard
import com.example.foundya.ui.Composables.ItemCard.PostListViewModel
import kotlinx.coroutines.launch

enum class PostTab(@StringRes val titleRes: Int) {
    ALL(R.string.all_posts),
    MY(R.string.my_posts)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: PostListViewModel = hiltViewModel()
) {
    val posts by viewModel.posts.collectAsState()
    val placeholderUrl by viewModel.placeholderUrl.collectAsState()
    val claimStates by viewModel.claimStates.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    val pagerState = rememberPagerState(pageCount = { PostTab.entries.size })
    val coroutineScope = rememberCoroutineScope()

    val myPosts = remember(posts) { posts.filter { viewModel.isPostOwnedByUser(it) } }
    val otherPosts = remember(posts) { posts.filterNot { viewModel.isPostOwnedByUser(it) } }

    FoundYaTheme {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            topBar = {
                Column {
                    TopAppBar(
                        title = { Text(stringResource(R.string.app_name)) }
                    )
                    TabRow(
                        selectedTabIndex = pagerState.currentPage,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        PostTab.entries.forEachIndexed { index, tab ->
                            Tab(
                                text = { Text(stringResource(tab.titleRes)) },
                                selected = pagerState.currentPage == index,
                                onClick = {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(index)
                                    }
                                }
                            )
                        }
                    }
                }
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showDialog = true },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.AddPost))
                }
            }
        ) { innerPadding ->
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) { page ->
                val currentPosts = when (PostTab.entries[page]) {
                    PostTab.ALL -> posts
                    PostTab.MY -> myPosts
                }

                if (currentPosts.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when (PostTab.entries[page]) {
                                PostTab.ALL -> stringResource(R.string.NoPosts)
                                PostTab.MY -> stringResource(R.string.NoYourPosts)
                            }
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(currentPosts) { post ->
                            val claimState = claimStates[post.id] ?: PostListViewModel.ClaimState()
                            ItemCard(
                                post = post,
                                placeholderUrl = placeholderUrl,
                                onClaimClick = { viewModel.onClaimClick(post.id) },
                                onDeleteClick = { viewModel.deletePost(post.id) },
                                isClaiming = claimState.isClaiming,
                                isClaimed = claimState.isClaimed,
                                isOwnedByUser = viewModel.isPostOwnedByUser(post),
                                errorMessage = claimState.errorMessage,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            if (showDialog) {
                AddPostDialog(
                    onDismiss = { showDialog = false },
                    onConfirm = { newPost ->
                        viewModel.addPost(newPost)
                        showDialog = false
                    }
                )
            }
        }
    }
}