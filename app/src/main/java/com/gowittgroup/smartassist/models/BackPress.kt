package com.gowittgroup.smartassist.models

sealed class BackPress {
    data object Idle : BackPress()
    data object InitialTouch : BackPress()
}
