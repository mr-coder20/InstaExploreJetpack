package mrcoder.instaexplore.jetpackcompose.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import mrcoder.instaexplore.jetpackcompose.UiState

class AppStateViewModel(
    private val photoViewModel: PhotoViewModel
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<Unit>>(UiState.Loading)
    val uiState: StateFlow<UiState<Unit>> = _uiState

    init {
        preloadApp()
    }

    private fun preloadApp() {
        viewModelScope.launch {
            if (photoViewModel.uiState.value !is UiState.Success) {
                try {
                    photoViewModel.fetchPhotos()
                    _uiState.value = UiState.Success(Unit)
                } catch (e: Exception) {
                    _uiState.value = UiState.Error("خطا در بارگذاری داده‌ها: ${e.message}")
                }
            } else {
                _uiState.value = UiState.Success(Unit)
            }
        }
    }
}
