package mrcoder.instaexplore.jetpackcompose.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class AppStateViewModelFactory(
    private val photoViewModel: PhotoViewModel // نیاز به ارسال PhotoViewModel به AppStateViewModel
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST") // جلوگیری از هشدار کست نامعتبر
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppStateViewModel::class.java)) {
            return AppStateViewModel(photoViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
