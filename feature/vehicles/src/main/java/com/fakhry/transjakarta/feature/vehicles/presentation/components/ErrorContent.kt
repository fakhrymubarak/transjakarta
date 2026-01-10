package com.fakhry.transjakarta.feature.vehicles.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fakhry.transjakarta.core.designsystem.theme.TransjakartaTheme
import com.fakhry.transjakarta.feature.vehicles.presentation.model.RateLimitUiState
import com.fakhry.transjakarta.feature.vehicles.presentation.retryButtonLabel

@Composable
fun ErrorContent(
    message: String,
    rateLimitState: RateLimitUiState?,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val errorMessage = rateLimitState?.message ?: message
    val retryEnabled = rateLimitState?.retryEnabled ?: true
    val buttonLabel = retryButtonLabel(rateLimitState)
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp),
        ) {
            Text(
                text = "⚠️",
                style = MaterialTheme.typography.displayMedium,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Something went wrong",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onRetry,
                enabled = retryEnabled,
            ) {
                Text(buttonLabel)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorContentPreview() {
    TransjakartaTheme {
        ErrorContent(
            message = "Network error: Unable to connect",
            rateLimitState = null,
            onRetry = {},
        )
    }
}
