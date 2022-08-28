package com.wadud.trivia.repository

import android.util.Log
import com.wadud.trivia.data.DataOrException
import com.wadud.trivia.model.QuestionItem
import com.wadud.trivia.network.QuestionApi
import javax.inject.Inject

class QuestionRepository @Inject constructor(private val api: QuestionApi) {

    private val dataOrException = DataOrException<ArrayList<QuestionItem>, Boolean, Exception>()

    suspend fun getAllQuestions(): DataOrException<ArrayList<QuestionItem>, Boolean, Exception> {
        try {
            dataOrException.loading = true
            dataOrException.data = api.getAllQuestions()
            if (dataOrException.data.toString().isNotEmpty()) dataOrException.loading = false
        } catch (exception: Exception) {
            dataOrException.exception = exception
            dataOrException.loading = false
            Log.e("wadud", exception.toString())
        }
        return dataOrException
    }
}