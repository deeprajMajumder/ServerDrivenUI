package com.deepraj.serverdrivenui.model

import com.deepraj.serverdrivenui.model.firebaseModels.NewsItems

data class LayoutInformation(
    val layoutMeta: LayoutMeta,
    val layoutData : List<NewsItems>
)
