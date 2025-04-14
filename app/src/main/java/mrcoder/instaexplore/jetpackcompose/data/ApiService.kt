import mrcoder.instaexplore.jetpackcompose.model.PhotoResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("v1/sample-data/photos/?offset=5&limit=500")
    suspend fun getPhotos(@Query("offset") offset: Int, @Query("limit") limit: Int): PhotoResponse
}
