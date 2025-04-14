package mrcoder.instaexplore.jetpackcompose.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import mrcoder.instaexplore.jetpackcompose.data.PhotoRepository

class PhotoViewModelFactory(private val photoRepository: PhotoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return PhotoViewModel(photoRepository) as T
    }
}