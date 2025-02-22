package com.gowittgroup.smartassist.ui.summary

import com.gowittgroup.smartassist.core.SideEffect

sealed class SummarySideEffect : SideEffect {
    data object ProfileUpdateSuccess : SummarySideEffect()
}