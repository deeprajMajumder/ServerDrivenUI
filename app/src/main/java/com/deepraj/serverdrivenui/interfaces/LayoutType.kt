package com.deepraj.serverdrivenui.interfaces

sealed interface LayoutType {
    data object List : LayoutType
    data class Grid(val columns: Int) : LayoutType
}