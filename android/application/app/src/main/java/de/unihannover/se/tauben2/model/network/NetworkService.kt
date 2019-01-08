package de.unihannover.se.tauben2.model.network

import de.unihannover.se.tauben2.LiveDataRes
import de.unihannover.se.tauben2.model.Auth
import de.unihannover.se.tauben2.model.CounterValue
import de.unihannover.se.tauben2.model.Token
import de.unihannover.se.tauben2.model.database.entity.Case
import de.unihannover.se.tauben2.model.database.entity.News
import de.unihannover.se.tauben2.model.database.entity.PopulationMarker
import de.unihannover.se.tauben2.model.database.entity.User
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface NetworkService {

    @GET("case")
    fun getCases(@Header("Authorization") token: String): LiveDataRes<List<Case>>

    @GET("case/{id}")
    fun getCase(@Header("Authorization") token: String, @Path("id") id: Int)
            : LiveDataRes<Case>

    @POST("case")
    fun sendCase(@Header("Authorization") token: String, @Body case: Case): LiveDataRes<Case>

    @PUT("case/{id}")
    fun updateCase(@Header("Authorization") token: String, @Path("id") id: Int,
                   @Body case: Case): LiveDataRes<Case>

    @DELETE("case/{id}")
    fun deleteCase(@Header("Authorization") token: String,
                   @Path("id") id: Int): Call<Void>

    @GET("stats")
    fun getStats(@Header("Authorization") token: String, @Body timeStamp: Long): LiveDataRes<List<Case>>

    @PUT
    fun uploadCasePicture(@Url uploadUrl: String, @Body media: RequestBody): Call<Void>


    @GET("feed")
    fun getNews(@Header("Authorization") token: String): LiveDataRes<List<News>>

    @POST("feed")
    fun sendNews(@Header("Authorization") token: String, @Body news: News): LiveDataRes<News>

    @PUT("feed/{id}")
    fun updateNews(@Header("Authorization") token: String, @Path("id") id: Int,
                   @Body news: News): LiveDataRes<News>

    @GET("feed/{feedID}")
    fun getNewsPost(@Header("Authorization") token: String, @Path("feedID") feedID: Int): LiveDataRes<News>

    @DELETE("feed/{feedID}")
    fun deleteNews(@Header("Authorization") token: String, @Path("feedID") feedID: Int): Call<Void>

    @GET("user")
    fun getUsers(@Header("Authorization") token: String): LiveDataRes<List<User>>

    @GET("user/{username}")
    fun getUser(@Header("Authorization") token: String, @Path("username") username: String): LiveDataRes<User>

    @POST("user")
    fun register(@Header("Authorization") token: String, @Body user: User): LiveDataRes<User>

    @PUT("user/{username}")
    fun updatePermissions(@Header("Authorization") token: String, @Body auth: Auth,
                          @Path("username") username: String): LiveDataRes<User>


    @POST("auth/login")
    fun login(@Body user: User): Call<Token>

    @DELETE("auth/logout")
    fun logout(@Header("Authorization") token: String): Call<Void>


    @GET("population")
    fun getPigeonCounters(@Header("Authorization") token: String)
            : LiveDataRes<List<PopulationMarker>>

    @POST("population/{markerId}")
    fun postCounterValue(@Header("Authorization") token: String, @Body value: CounterValue,
                         @Path("markerId") markerId: Int): LiveDataRes<CounterValue>

    @POST("population")
    fun postNewMarker(@Header("Authorization") token: String, @Body marker: PopulationMarker): LiveDataRes<PopulationMarker>

    @DELETE("population/{id}")
    fun deleteMarker(@Header("Authorization") token: String, @Path("id") id: Int): Call<Void>
}