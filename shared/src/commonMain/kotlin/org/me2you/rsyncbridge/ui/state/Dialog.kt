package org.me2you.rsyncbridge.ui.state

sealed class IosDialog {
    object None : IosDialog()
    object FileSelect : IosDialog()
    object PathSelect : IosDialog()
}