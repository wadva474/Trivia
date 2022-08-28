package com.wadud.trivia.screen

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.wadud.trivia.component.Questions

@Composable
fun QuestionScreen(viewModel: QuestionsViewModel = hiltViewModel()) {
    Questions(viewModel)
}