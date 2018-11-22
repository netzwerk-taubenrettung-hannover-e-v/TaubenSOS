package de.unihannover.se.tauben2.model.network

import de.unihannover.se.tauben2.LiveDataRes
import de.unihannover.se.tauben2.model.entity.Case
import retrofit2.http.GET
import retrofit2.http.Path

interface NetworkService {

    @GET("case")
    fun getCases(): LiveDataRes<List<Case>>

    @GET("case/{id}")
    fun getCase(@Path("id") id: Int): LiveDataRes<Case>

}