package com.example.weather_app.designsystem.components

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import com.example.weather_app.designsystem.theme.Theme

@Composable
fun WTButton(
    text: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = Theme.colors.buttonColor,
    textStyle: TextStyle = Theme.typography.bodyMedium,
    textColor: Color = Theme.colors.textColors.bodyColor,
    shape: Shape = Theme.shapes.medium,
    elevation: ButtonElevation = ButtonDefaults.elevatedButtonElevation(),
    content: (@Composable () -> Unit)? = null
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = color
        ),
        shape = shape,
        elevation = elevation
    ) {
        if (content != null) {
            content()
        }
        if (text!= null)
        Text(
            text = text,
            style = textStyle,
            color = textColor,
        )
    }
}
@Preview(showBackground = true)
@Composable
fun WTButtonPreview() {
    WTButton(
        text = "Click Me",
        onClick = { /*TODO*/ },
        color = Theme.colors.buttonColor,
        textStyle = Theme.typography.bodyMedium,
        textColor = Theme.colors.textColors.bodyColor,
        shape = Theme.shapes.medium
    )
}