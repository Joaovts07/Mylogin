package com.example.mylogin.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LoadingButton(
    onClick: () -> Unit,
    isLoading: Boolean = false,
    text: String = "Button",
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.buttonColors()
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(48.dp),
        shape = RoundedCornerShape(8.dp),
        enabled = enabled,
        colors = colors
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(ButtonDefaults.IconSize),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp
            )
        } else {
            Text(text)
        }
    }
}