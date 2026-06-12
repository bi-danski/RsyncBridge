package org.me2you.rsyncbridge.ui.components.configscreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.me2you.rsyncbridge.ui.theme.ActionCyan
import org.me2you.rsyncbridge.ui.theme.SurfaceVariant
import org.me2you.rsyncbridge.ui.theme.TextPrimary
import org.me2you.rsyncbridge.ui.theme.TextSecondary

@Composable
fun ConfigField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onTogglePassword: (() -> Unit)? = null
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = TextSecondary,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 0.5.sp,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    fontSize = 13.sp,
                    color = Color(0xFF555560),
                    fontFamily = FontFamily.Monospace
                )
            },
            singleLine = true,
            visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation()
            else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ActionCyan,
                unfocusedBorderColor = Color(0xFF3D3D45),
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                cursorColor = ActionCyan,
                focusedContainerColor = SurfaceVariant,
                unfocusedContainerColor = SurfaceVariant
            ),
            textStyle = LocalTextStyle.current.copy(
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace
            ),
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = if (isPassword && onTogglePassword != null) {
                {
                    Text(
                        text = if (passwordVisible) "hide" else "show",
                        fontSize = 10.sp, color = TextSecondary,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.clickable {
                            onTogglePassword()
                        }.padding(end = 8.dp)
                    )
                }
            } else null
        )
    }
}