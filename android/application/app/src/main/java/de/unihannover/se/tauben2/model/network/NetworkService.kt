package de.unihannover.se.tauben2.model.network

import de.unihannover.se.tauben2.LiveDataRes
import de.unihannover.se.tauben2.model.Auth
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
    fun getCase(@Header("Authorization") token: String, @Path("id") id: Int): LiveDataRes<Case>

    @POST("case")
    fun sendCase(@Header("Authorization") token: String, @Body case: Case): LiveDataRes<Case>

    @PUT("case/{id}")
    fun updateCase(@Header("Authorization") token: String, @Path("id") id: Int, @Body case: Case): LiveDataRes<Case>

    @DELETE("case/{id}")
    fun deleteCase(@Header("Authorization") token: String, @Path("id") id: Int): Call<Void>

    @PUT
    fun uploadCasePicture(@Url uploadUrl: String, @Body media: RequestBody): Call<Void>

    @GET("news")
    fun getNews(@Header("Authorization") token: String): LiveDataRes<List<News>>

    @GET("user")
    fun getUsers(@Header("Authorization") token: String): LiveDataRes<List<User>>

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
    fun getPigeonCounters(@Header("Authorization") token: String): LiveDataRes<List<PopulationMarker>>

    @POST("population")
    fun sendPigeonCounter(@Header("Authorization") token: String, @Body populationMarker: PopulationMarker): LiveDataRes<Unit>
}