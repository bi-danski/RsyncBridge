package org.me2you.rsyncbridge.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.defaultScrollbarStyle
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.me2you.rsyncbridge.data.ProxyConfig
import org.me2you.rsyncbridge.data.SshConfig
import org.me2you.rsyncbridge.ui.components.configscreen.ArgTag
import org.me2you.rsyncbridge.ui.components.configscreen.ConfigField
import org.me2you.rsyncbridge.ui.components.configscreen.ConfigSection
import org.me2you.rsyncbridge.ui.theme.ActionCyan
import org.me2you.rsyncbridge.ui.theme.ActionGreen
import org.me2you.rsyncbridge.ui.theme.C2
import org.me2you.rsyncbridge.ui.theme.C9
import org.me2you.rsyncbridge.ui.theme.DeepCharcoal
import org.me2you.rsyncbridge.ui.theme.K6
import org.me2you.rsyncbridge.ui.theme.SurfaceVariant
import org.me2you.rsyncbridge.ui.theme.TextPrimary
import org.me2you.rsyncbridge.ui.theme.TextSecondary
import org.me2you.rsyncbridge.vm.ConfViewModel
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun ConfigScreen(confViewModel: ConfViewModel) {
    var sshPort by confViewModel.sshPort
    var sshHost by confViewModel.sshHost
    var sshPass by confViewModel.sshPass
    var sshUser by confViewModel.sshUser
    var proxyRemotePort by confViewModel.proxyRemotePort
    var proxyLocalPort by confViewModel.proxyLocalPort
    var args by confViewModel.proxyArgs
    var argInput by confViewModel.argInput
    var passVisible by confViewModel.passVisible
    var savedVisible by confViewModel.savedVisible
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()


    Scaffold {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DeepCharcoal)
        ) {
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(24.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(end = 12.dp)
            ) {
                item {
                    Column(modifier = Modifier.padding(bottom = 24.dp)) {
                        Text(
                            text = "SSH & iProxy Configurations",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                item {
                    ConfigSection(
                        label = "SSH Configuration",
                        dotColor = ActionCyan
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            ConfigField(
                                label = "HOST",
                                value = sshHost,
                                onValueChange = { sshHost = it },
                                placeholder = sshHost,
                                modifier = Modifier.weight(1f)
                            )
                            ConfigField(
                                label = "PORT",
                                value = sshPort.toString(),
                                onValueChange = { sshPort = it.toInt() },
                                placeholder = sshPort.toString(),
                                keyboardType = KeyboardType.Number,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            ConfigField(
                                label = "USER",
                                value = sshUser,
                                onValueChange = { sshUser = it },
                                placeholder = sshUser,
                                modifier = Modifier.weight(1f)
                            )
                            ConfigField(
                                label = "PASSWORD",
                                value = sshPass,
                                onValueChange = { sshPass = it },
                                placeholder = "••••••••",
                                isPassword = true,
                                passwordVisible = passVisible,
                                onTogglePassword = { passVisible = !passVisible },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    ConfigSection(
                        label = "iProxy Configuration",
                        dotColor = ActionGreen
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            ConfigField(
                                label = "REMOTE PORT",
                                value = proxyRemotePort.toString(),
                                onValueChange = { proxyRemotePort = it.toInt() },
                                placeholder = proxyRemotePort.toString(),
                                keyboardType = KeyboardType.Number,
                                modifier = Modifier.weight(1f)
                            )
                            ConfigField(
                                label = "LOCAL PORT",
                                value = proxyLocalPort.toString(),
                                onValueChange = { proxyLocalPort = it.toInt() },
                                placeholder = proxyLocalPort.toString(),
                                keyboardType = KeyboardType.Number,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        Column(modifier = Modifier.height(150.dp)) {
                            Text(
                                text = "ARGS",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 0.5.sp,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .border(
                                        width = 0.5.dp,
                                        color = K6,
                                        shape = RoundedCornerShape(6.dp)
                                    )
                                    .background(
                                        color = SurfaceVariant,
                                        shape = RoundedCornerShape(6.dp)
                                    )
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Column {
                                    if (args.isNotEmpty()) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            args.forEach { arg ->
                                                ArgTag(
                                                    value = arg,
                                                    onRemove = {
                                                        args = args.toMutableList().also {
                                                            it.remove(arg)
                                                        }
                                                    }
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))

                                        OutlinedTextField(
                                            value = argInput,
                                            onValueChange = { argInput = it },
                                            placeholder = {
                                                Text(
                                                    "add arg...",
                                                    fontSize = 12.sp,
                                                    color = C9,
                                                    fontFamily = FontFamily.Monospace
                                                )
                                            },
                                            singleLine = true,
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = Color.Transparent,
                                                unfocusedBorderColor = Color.Transparent,
                                                focusedTextColor = TextPrimary,
                                                unfocusedTextColor = TextPrimary,
                                                cursorColor = ActionCyan
                                            ),
                                            textStyle = LocalTextStyle.current.copy(
                                                fontSize = 12.sp,
                                                fontFamily = FontFamily.Monospace
                                            ),
                                            modifier = Modifier.fillMaxSize(),
                                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                            keyboardActions = KeyboardActions(onDone = {
                                                val trimmed = argInput.trim()
                                                if (trimmed.isNotEmpty()) {
                                                    args = args.toMutableList()
                                                        .also { it.add(trimmed) }
                                                    argInput = ""
                                                }
                                            })
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = {
                                scope.launch(Dispatchers.Main) {
                                    confViewModel.onSaveConfigs(
                                        SshConfig(
                                            host = sshHost,
                                            port = sshPort,
                                            user = sshUser,
                                            pass = sshPass
                                        ),
                                        ProxyConfig(
                                            remotePort = proxyRemotePort,
                                            localPort = proxyLocalPort,
                                            args = args
                                        )
                                    )
                                }
                                scope.launch {
                                    savedVisible = true
                                    delay(3000.milliseconds)
                                    savedVisible = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ActionGreen),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 9.dp)
                        ) {
                            Text(
                                text = "Save Config",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Black,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        OutlinedButton(
                            onClick = { scope.launch(Dispatchers.IO) { confViewModel.onResetConfigs() } },
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, C2),
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 9.dp)
                        ) {
                            Text(
                                text = "Reset",
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                item {
                    AnimatedVisibility(
                        visible = savedVisible,
                        enter = fadeIn() + slideInVertically(),
                        exit = fadeOut() + slideOutVertically()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 12.dp, bottom = 12.dp)
                        ) {
                            Text(
                                text = "✓",
                                fontSize = 14.sp,
                                color = ActionGreen
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Configuration saved",
                                fontSize = 12.sp,
                                color = ActionGreen,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                    Spacer(Modifier.height(20.dp))
                }
            }

            VerticalScrollbar(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxHeight()
                    .padding(vertical = 4.dp, horizontal = 2.dp),
                adapter = rememberScrollbarAdapter(scrollState = listState),
                style = defaultScrollbarStyle().copy(
                    unhoverColor = TextSecondary.copy(alpha = 0.3f),
                    hoverColor = ActionGreen.copy(alpha = 0.4f),
                    thickness = 8.dp,
                    shape = MaterialTheme.shapes.small
                )
            )
        }
    }
}