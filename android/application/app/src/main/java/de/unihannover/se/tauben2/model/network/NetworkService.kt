package de.unihannover.se.tauben2.model.network

import de.unihannover.se.tauben2.LiveDataRes
import de.unihannover.se.tauben2.model.entity.Case
import de.unihannover.se.tauben2.model.entity.PigeonCounter
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface NetworkService {

    @GET("case")
    fun getCases(): LiveDataRes<List<Case>>

    @GET("case/{id}")
    fun getCase(@Path("id") id: Int): LiveDataRes<Case>

    @POST("case")
    fun sendCase(@Body case: Case): LiveDataRes<Case>

    @POST("population")
    fun sendPigeonCounter(@Body pigeonCounter: PigeonCounter): LiveDataRes<Unit>
}