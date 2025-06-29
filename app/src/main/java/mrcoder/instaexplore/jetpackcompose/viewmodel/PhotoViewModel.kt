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

    // وضعیت برای نگهداری داده‌ها و وضعیت بارگذاری
    private val _uiState = MutableStateFlow<UiState<List<Photo>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Photo>>> = _uiState

    // وضعیت اسکرول (برای نگهداری موقعیت اسکرول)
    private val _scrollIndex = MutableStateFlow(0)
    val scrollIndex: StateFlow<Int> = _scrollIndex

    private val _scrollOffset = MutableStateFlow(0)
    val scrollOffset: StateFlow<Int> = _scrollOffset

    // متغیرهای مرتبط با وضعیت بارگذاری
    private var currentOffset = 0
    private val limit = 50 // تعداد تصاویر هر بار بارگذاری
    private val allPhotos = mutableListOf<Photo>()

    // متد برای بارگذاری مجدد عکس‌ها
    fun onRefresh() {
        currentOffset = 0 // وضعیت اسکرول را از ابتدا قرار می‌دهیم
        allPhotos.clear() // پاک کردن لیست تصاویر برای بارگذاری جدید
        fetchPhotos() // بارگذاری مجدد تصاویر
    }

    // متد برای بارگذاری عکس‌ها
    fun fetchPhotos() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading // تنظیم وضعیت به در حال بارگذاری
            try {
                // درخواست برای بارگذاری تصاویر جدید
                val newPhotos = photoRepository.fetchPhotos(currentOffset, limit)
                    .filterNot { it in allPhotos } // جلوگیری از اضافه کردن عکس‌های تکراری

                if (newPhotos.isNotEmpty()) {
                    allPhotos.addAll(newPhotos) // افزودن عکس‌های جدید به لیست
                    currentOffset += newPhotos.size // به‌روزرسانی موقعیت اسکرول
                }

                _uiState.value = UiState.Success(allPhotos) // وضعیت به موفقیت تغییر می‌کند
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.localizedMessage ?: "Unknown Error") // مدیریت خطا
            }
        }
    }

    // متدی برای ذخیره وضعیت اسکرول (index و offset)
    fun saveScrollState(index: Int, offset: Int) {
        _scrollIndex.value = index
        _scrollOffset.value = offset
    }
}
