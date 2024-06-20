package com.dicoding.areunemia.data.remote.response

import com.google.gson.annotations.SerializedName

data class QuestionnaireAnswers(

    @field:SerializedName("question7")
    val question7: String? = null,

    @field:SerializedName("question6")
    val question6: String? = null,

    @field:SerializedName("question9")
    val question9: String? = null,

    @field:SerializedName("question8")
    val question8: String? = null,

    @field:SerializedName("question10")
    val question10: String? = null,

    @field:SerializedName("question3")
    val question3: String? = null,

    @field:SerializedName("question2")
    val question2: String? = null,

    @field:SerializedName("question5")
    val question5: String? = null,

    @field:SerializedName("question4")
    val question4: String? = null,

    @field:SerializedName("question1")
    val question1: String? = null

)


data class AnswerItem(
    val no: Int,
    val answer: String?
)

fun QuestionnaireAnswers.toQuestionList(): List<AnswerItem> {
    return listOf(
        AnswerItem(1, question1),
        AnswerItem(2, question2),
        AnswerItem(3, question3),
        AnswerItem(4, question4),
        AnswerItem(5, question5),
        AnswerItem(6, question6),
        AnswerItem(7, question7),
        AnswerItem(8, question8),
        AnswerItem(9, question9),
        AnswerItem(10, question10)
    )
}
