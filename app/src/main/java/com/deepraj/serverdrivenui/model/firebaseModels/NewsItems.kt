package com.deepraj.serverdrivenui.model.firebaseModels

data class NewsItems(
    val id : Int = 0,
    val title : String = "",
    val description : String = "",
    val isFavorite : Boolean = false
)
