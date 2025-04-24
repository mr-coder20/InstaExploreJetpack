package mrcoder.instaexplore.jetpackcompose.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mrcoder.instaexplore.jetpackcompose.model.Photo

@Composable
fun TripleColumnLayout(
    photos: List<Photo>,
    uniquePhoto: Photo,
    isLeftAligned: Boolean,
    index: Int
) {
    val distinctPhotos = photos.distinctBy { it.url }

    if (distinctPhotos.size < 8) return

    val photos5To7 = distinctPhotos.slice(4..6)
    val photos0To4 = distinctPhotos.take(5)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(1.dp)
            .background(MaterialTheme.colorScheme.primary)
    ) {
        if (index >= 2) {
            Row(modifier = Modifier.fillMaxWidth()) {
                photos5To7.forEach { photo ->
                    ImageCard(photo = photo, height = 150.dp, modifier = Modifier.weight(1f))
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            if (isLeftAligned) {
                ImageCard(
                    photo = uniquePhoto,
                    height = 240.dp,
                    modifier = Modifier.weight(1f)
                )
            }

            Column(modifier = Modifier.weight(1.5f)) {
                listOf(
                    photos0To4[0] to photos0To4[1],
                    photos0To4[2] to photos0To4[3]
                ).forEach { (photo1, photo2) ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        ImageCard(photo = photo1, height = 120.dp, modifier = Modifier.weight(1f))
                        ImageCard(photo = photo2, height = 120.dp, modifier = Modifier.weight(1f))
                    }
                }
            }

            if (!isLeftAligned) {
                ImageCard(
                    photo = uniquePhoto,
                    height = 240.dp,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
