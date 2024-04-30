package com.deepraj.serverdrivenui.interfaces

interface LayoutType {
    object List : LayoutType
    data class Grid(val columns: Int) : LayoutType
}