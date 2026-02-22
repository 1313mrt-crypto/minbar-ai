package com.sokhanara.app.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sokhanara.app.R

/**
 * Home Screen با ViewModel
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToCreate: () -> Unit = {},
    onNavigateToLibrary: () -> Unit = {},
    onNavigateToProjects: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(R.string.settings)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        when (uiState) {
            is HomeUiState.Loading -> {
                LoadingContent(modifier = Modifier.padding(paddingValues))
            }
            
            is HomeUiState.Empty -> {
                EmptyContent(
                    modifier = Modifier.padding(paddingValues),
                    onNavigateToCreate = onNavigateToCreate,
                    onNavigateToLibrary = onNavigateToLibrary,
                    onNavigateToProjects = onNavigateToProjects
                )
            }
            
            is HomeUiState.Success -> {
                SuccessContent(
                    modifier = Modifier.padding(paddingValues),
                    speeches = (uiState as HomeUiState.Success).speeches,
                    onNavigateToCreate = onNavigateToCreate,
                    onNavigateToLibrary = onNavigateToLibrary,
                    onNavigateToProjects = onNavigateToProjects
                )
            }
            
            is HomeUiState.Error -> {
                ErrorContent(
                    modifier = Modifier.padding(paddingValues),
                    message = (uiState as HomeUiState.Error).message,
                    onRetry = { viewModel.refresh() }
                )
            }
        }
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyContent(
    modifier: Modifier = Modifier,
    onNavigateToCreate: () -> Unit,
    onNavigateToLibrary: () -> Unit,
    onNavigateToProjects: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "به سخنرانی هوشمند خوش آمدید! 🎤",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "با هوش مصنوعی، سخنرانی‌های حرفه‌ای بسازید",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Button(
            onClick = onNavigateToCreate,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.create_new),
                style = MaterialTheme.typography.titleMedium
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onNavigateToLibrary,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.LibraryBooks,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.library),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
            
            OutlinedButton(
                onClick = onNavigateToProjects,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Folder,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.projects),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun SuccessContent(
    modifier: Modifier = Modifier,
    speeches: List<com.sokhanara.app.domain.model.Speech>,
    onNavigateToCreate: () -> Unit,
    onNavigateToLibrary: () -> Unit,
    onNavigateToProjects: () -> Unit
) {
    // TODO: نمایش لیست سخنرانی‌ها
    EmptyContent(modifier, onNavigateToCreate, onNavigateToLibrary, onNavigateToProjects)
}

@Composable
private fun ErrorContent(
    modifier: Modifier = Modifier,
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.error
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(onClick = onRetry) {
            Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("تلاش مجدد")
        }
    }
}
```

---

## ✅ ساختار فایل‌های ساخته‌شده
```
app/src/main/java/com/sokhanara/app/ui/screens/
├── home/
│   ├── HomeScreen.kt (بروزرسانی شد)    ✅
│   └── HomeViewModel.kt                  ✅
├── create/
│   └── CreateViewModel.kt                ✅
└── preview/
    └── PreviewViewModel.kt               ✅
