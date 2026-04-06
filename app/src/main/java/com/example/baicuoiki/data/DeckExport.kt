package com.example.baicuoiki.data

data class DeckExport(
    val deckName: String,
    val description: String,
    val cards: List<CardExport>
)

data class CardExport(
    val front: String,
    val back: String
)
