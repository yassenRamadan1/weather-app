package com.example.weather_app.designsystem.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.weather_app.designsystem.theme.Theme

@Composable
fun WTFloatingButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = Theme.colors.buttonColor,
    shape: Shape = Theme.shapes.circle,
    elevation: Dp = 6.dp,
    content: (@Composable () -> Unit)? = {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add",
            tint = Theme.colors.onBodyColor,
            modifier = Modifier.size(42.dp)
        )
    }
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        containerColor = color,
        shape = shape,
        elevation = FloatingActionButtonDefaults.elevation(elevation),
        content = content ?: {}
    )
}

@Preview(showBackground = true)
@Composable
fun WTFloatingButtonPreview() {
    WTFloatingButton(
        onClick = { /*TODO*/ },
        color = Theme.colors.buttonColor,
        shape = Theme.shapes.circle
    )
}