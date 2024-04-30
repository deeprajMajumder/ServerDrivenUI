package com.deepraj.serverdrivenui

import androidx.lifecycle.ViewModel
import com.deepraj.serverdrivenui.interfaces.LayoutType
import com.deepraj.serverdrivenui.model.firebaseModels.Meta
import com.deepraj.serverdrivenui.model.firebaseModels.NewsItems
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class MainActivityViewModel : ViewModel() {
    private val realTimeDatabase = Firebase.database
    private val dataNode = realTimeDatabase.getReference("ui/data")
    private val layoutNode = realTimeDatabase.getReference("ui/layout")
    private val metaNode = realTimeDatabase.getReference("ui/meta")

    //FireBase flows
    private val _dataFlow : Flow<List<NewsItems>> = callbackFlow {

    }
    private val _layoutTypeMapFlow : Flow<Map<String, LayoutType>> = callbackFlow {

    }
    private  var _metaFlow : Flow<Meta> = callbackFlow {

    }

    //UI flow
    

}