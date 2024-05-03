package com.deepraj.serverdrivenui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deepraj.serverdrivenui.interfaces.LayoutType
import com.deepraj.serverdrivenui.model.LayoutInformation
import com.deepraj.serverdrivenui.model.LayoutMeta
import com.deepraj.serverdrivenui.model.firebaseModels.Meta
import com.deepraj.serverdrivenui.model.firebaseModels.NewsItems
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn


class MainActivityViewModel : ViewModel() {
    private val TAG = MainActivityViewModel::class.java.name
    private val realTimeDatabase = Firebase.database
    private val dataNode = realTimeDatabase.getReference("ui/data")
    private val layoutNode = realTimeDatabase.getReference("ui/layout")
    private val metaNode = realTimeDatabase.getReference("ui/meta")

    //FireBase flows
    private val _dataFlow : Flow<List<NewsItems>> = callbackFlow {
        val  listener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val newsItems = snapshot.children.map {
                    it.getValue<NewsItems>()!!.copy(isFavorite =  it.children.find {
                        it.key == "isFavorite"
                    }!!.getValue<Boolean>()!!.run {
                        return@run this
                    })
                }
                trySend(newsItems)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Error fetching data from realtime DB")
            }
        }
        dataNode.addValueEventListener(listener)
        awaitClose { dataNode.removeEventListener(listener) }
    }
    private val _layoutTypeMapFlow : Flow<Map<String, LayoutType>> = callbackFlow {
        fun parse(snapshot: DataSnapshot) : LayoutType{
            val type = snapshot.children.find {
                it.key == "type"
            }!!.getValue<String>()!!
            return when (type){
                "list" -> LayoutType.List
                "grid" -> LayoutType.Grid(
                    columns = snapshot.children.find { it.key == "columns" }!!.getValue<Int>()!!
                )
                 else -> {
                     Log.e(TAG,"Unknown Type: $type");
                     LayoutType.List //return list by default
                 }
            }
        }

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val map = snapshot.children.associate {
                    it.key!! to parse(it)
                }
                trySend(map)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Error fetching data from realtime DB")
            }
        }
        layoutNode.addValueEventListener(listener)
        awaitClose { layoutNode.removeEventListener(listener) } //await and then remove event listener
    }
    private var _metaFlow : Flow<Meta> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.getValue<Meta>()!!)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Error fetching data from realtime DB")
            }
        }
        metaNode.addValueEventListener(listener)
        awaitClose { metaNode.removeEventListener(listener) } //await and then remove event listener
    }

    //UI flow
    val layoutInformationFlow : StateFlow<LayoutInformation?> = combine(_dataFlow, _layoutTypeMapFlow, _metaFlow){
        newsItems, layoutTypeMap, meta ->
        if (newsItems.isEmpty()) {
            return@combine null
        }
        val layoutInformation = LayoutMeta(
            layoutTypeMap[meta.mode] ?: LayoutType.List, //default layout is List
            meta.canFavorite
        )
        return@combine LayoutInformation(
            layoutInformation,
            newsItems
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

}