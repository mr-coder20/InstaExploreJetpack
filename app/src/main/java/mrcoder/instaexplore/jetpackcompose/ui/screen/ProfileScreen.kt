package mrcoder.instaexplore.jetpackcompose.ui.screen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
@Composable
fun ProfileScreen() {
    Box(Modifier.fillMaxSize()
        .background(MaterialTheme.colorScheme.primary ), contentAlignment = Alignment.Center) {

    Text("Profile Page")
    }
}