package com.deepraj.serverdrivenui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.deepraj.serverdrivenui.interfaces.LayoutType
import com.deepraj.serverdrivenui.model.LayoutInformation
import com.deepraj.serverdrivenui.model.firebaseModels.NewsItems
import com.deepraj.serverdrivenui.ui.theme.ServerDrivenUITheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ServerDrivenUITheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainComposable(viewModel = MainActivityViewModel())
                }
            }
        }
    }
}
@Composable
fun MainComposable(viewModel : MainActivityViewModel) {
    val layoutInformation by viewModel.layoutInformationFlow.collectAsStateWithLifeCycle()
    when(layoutInformation){
        null -> LoadingComponent()
        else -> NewsFeedScreen(layoutInformation)
    }
}
@Composable
fun NewsFeedScreen(layoutInformation: LayoutInformation){
    when(layoutInformation.layoutMeta.layoutType){
        is LayoutType.List -> {
            LazyColumn(contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(items = layoutInformation.layoutData, key = { newsItem -> newsItem.id}){
                    NewItemComponent(
                        it,
                        layoutInformation.layoutMeta.favoriteEnabled
                    )
                }
            }
        }
        is LayoutType.Grid ->{
            LazyVerticalGrid(columns = GridCells.Fixed(layoutInformation.layoutMeta.layoutType.columns),
                contentPadding =  PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items( items = layoutInformation.layoutData, key = { newItem -> newItem.id}){
                    NewItemComponent(
                        it,
                        layoutInformation.layoutMeta.favoriteEnabled
                    )
                }

            }
        }
    }
}
@Composable
fun NewItemComponent(newsItem : NewsItems, favoriteEnabled : Boolean){
    Column(modifier = Modifier
        .background(
            color = Color.LightGray,
            shape = RoundedCornerShape(16.dp)
        )
        .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = newsItem.title)
            Spacer(modifier = Modifier.weight(1f))
            if (favoriteEnabled){
                val icon = if (newsItem.isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder
                Icon(imageVector = icon,
                    contentDescription = "Favourite",
                    modifier = Modifier.clickable {
                        Log.d("FAVORITE", "Handle onClick for ${newsItem.id}")
                    })
            }
        }
        Spacer(modifier = Modifier
            .height(1.dp)
            .fillMaxWidth()
            .background(color = Color.DarkGray, shape = RoundedCornerShape(50))
        )
        Spacer(modifier = Modifier.height(15.dp))
        Text( text =  newsItem.description)
    }
}
@Composable
fun LoadingComponent(){
    Box(modifier = Modifier.fillMaxSize()){
        CircularProgressIndicator(
            modifier = Modifier
                .size(50.dp)
                .align(Alignment.Center),
            color =  MaterialTheme.colorScheme.tertiary
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ServerDrivenUITheme {
        MainComposable(MainActivityViewModel())
    }
}