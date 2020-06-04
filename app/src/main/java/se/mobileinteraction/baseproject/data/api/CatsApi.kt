package se.mobileinteraction.baseproject.data.api

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query
import se.mobileinteraction.baseproject.entities.Cat

interface CatsApi {
    @GET("images/search")
    fun getCats(
        @Header("x-api-key") apiKey: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("order") order: String
    ): Single<List<Cat>>

    @GET("images/{imageId}")
    fun getCat(@Header("x-api-key") apiKey: String, @Path("imageId") id: String): Single<Cat>
}

