package com.example.baicuoiki.api

import com.example.baicuoiki.data.Deck
import com.example.baicuoiki.data.Flashcard
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface SyncApiService {
    @GET("sync/decks")
    suspend fun getDecks(): List<Deck>

    @POST("sync/decks")
    suspend fun uploadDecks(@Body decks: List<Deck>)

    @GET("sync/cards")
    suspend fun getCards(): List<Flashcard>

    @POST("sync/cards")
    suspend fun uploadCards(@Body cards: List<Flashcard>)
}
