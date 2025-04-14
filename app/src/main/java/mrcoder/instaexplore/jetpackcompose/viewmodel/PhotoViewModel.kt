package mrcoder.instaexplore.jetpackcompose.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import mrcoder.instaexplore.jetpackcompose.UiState
import mrcoder.instaexplore.jetpackcompose.data.PhotoRepository
import mrcoder.instaexplore.jetpackcompose.model.Photo

class PhotoViewModel(private val photoRepository: PhotoRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<List<Photo>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Photo>>> = _uiState

    private var currentOffset = 0
    private val limit = 500
    private val allPhotos = mutableListOf<Photo>()

    fun fetchPhotos() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val newPhotos = photoRepository.fetchPhotos(currentOffset, limit)
                    .filterNot { it in allPhotos }

                if (newPhotos.isNotEmpty()) {
                    allPhotos.addAll(newPhotos)
                    currentOffset += newPhotos.size
                }

                _uiState.value = UiState.Success(allPhotos)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.localizedMessage ?: "Unknown Error")
            }
        }
    }
}