package com.fakhry.transjakarta.feature.vehicles.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fakhry.transjakarta.feature.vehicles.presentation.model.RateLimitUiState
import com.fakhry.transjakarta.feature.vehicles.presentation.retryButtonLabel

@Composable
fun PaginationErrorItem(
    message: String,
    rateLimitState: RateLimitUiState?,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val errorMessage = rateLimitState?.message ?: message
    val retryEnabled = rateLimitState?.retryEnabled ?: true
    val buttonLabel = retryButtonLabel(rateLimitState)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = errorMessage,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onRetry,
            enabled = retryEnabled,
        ) {
            Text(buttonLabel)
        }
    }
}
