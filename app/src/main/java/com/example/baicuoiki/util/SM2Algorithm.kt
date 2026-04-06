package com.example.baicuoiki.util

import com.example.baicuoiki.data.Flashcard
import kotlin.math.max

data class SM2Result(
    val interval: Int,
    val repetition: Int,
    val easeFactor: Float,
    val nextReview: Long
)

object SM2Algorithm {
    fun calculate(card: Flashcard, quality: Int): SM2Result {
        var newRepetition: Int
        var newInterval: Int
        var newEaseFactor: Float

        if (quality >= 3) {
            // Đáp án đúng
            if (card.repetition == 0) {
                newInterval = 1
                newRepetition = 1
            } else if (card.repetition == 1) {
                newInterval = 6
                newRepetition = 2
            } else {
                newInterval = (card.interval * card.easeFactor).toInt()
                newRepetition = card.repetition + 1
            }
            // Cập nhật Ease Factor
            newEaseFactor = card.easeFactor + (0.1f - (5 - quality) * (0.08f + (5 - quality) * 0.02f))
        } else {
            // Đáp án sai
            newRepetition = 0
            newInterval = 1
            newEaseFactor = card.easeFactor
        }

        if (newEaseFactor < 1.3f) newEaseFactor = 1.3f

        val nextReviewMillis = System.currentTimeMillis() + (newInterval * 24 * 60 * 60 * 1000L)

        return SM2Result(newInterval, newRepetition, newEaseFactor, nextReviewMillis)
    }
}
