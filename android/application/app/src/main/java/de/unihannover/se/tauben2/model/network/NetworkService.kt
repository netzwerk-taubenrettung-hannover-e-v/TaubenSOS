package de.unihannover.se.tauben2.model.network

import de.unihannover.se.tauben2.LiveDataRes
import de.unihannover.se.tauben2.model.entity.Case
import de.unihannover.se.tauben2.model.entity.PigeonCounter
import de.unihannover.se.tauben2.model.entity.User
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface NetworkService {

    @GET("case")
    fun getCases(): LiveDataRes<List<Case>>

    @GET("case/{id}")
    fun getCase(@Path("id") id: Int): LiveDataRes<Case>

    @POST("case")
    fun sendCase(@Body case: Case): LiveDataRes<Case>

    @PUT
    fun uploadCasePicture(@Url uploadUrl: String, @Body media: RequestBody): Call<Void>

    @GET("user")
    fun getUsers(): LiveDataRes<List<User>>

    @GET("population")
    fun getPigeonCounters(): LiveDataRes<List<PigeonCounter>>

    @POST("population")
    fun sendPigeonCounter(@Body pigeonCounter: PigeonCounter): LiveDataRes<Unit>
}