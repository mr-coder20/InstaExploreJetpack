package mrcoder.instaexplore.jetpackcompose.data

import mrcoder.instaexplore.jetpackcompose.model.Photo

class PhotoRepository() {
    suspend fun fetchPhotos(offset: Int, limit: Int): List<Photo> {
        return RetrofitInstance.api.getPhotos(offset, limit).photos
    }
}
