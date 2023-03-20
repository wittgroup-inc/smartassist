package com.gowittgroup.smartassist.models

sealed class BackPress {
    object Idle : BackPress()
    object InitialTouch : BackPress()
}
