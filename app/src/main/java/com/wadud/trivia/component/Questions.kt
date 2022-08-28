package com.wadud.trivia.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wadud.trivia.model.QuestionItem
import com.wadud.trivia.screen.QuestionsViewModel
import com.wadud.trivia.util.AppColors

@Composable
fun Questions(viewModel: QuestionsViewModel) {
    val questions = viewModel.data.value.data?.toMutableList()

    val questionIndex = remember {
        mutableStateOf(0)
    }

    if (viewModel.data.value.loading == true) {
        CircularProgressIndicator()
    } else {
        val question = try {
            questions?.get(questionIndex.value)
        } catch (ex: Exception) {
            null
        }
        if (question != null) {
            QuestionDisplay(question, questionIndex, viewModel) {
                questionIndex.value = questionIndex.value + 1
            }
        }
    }


}

@Composable
fun QuestionDisplay(
    questionItem: QuestionItem,
    questionIndex: MutableState<Int>,
    viewModel: QuestionsViewModel,
    onNextClicked: (Int) -> Unit

) {
    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
    val choicesState = remember(questionItem) {
        questionItem.choices.toMutableList()
    }
    val answerState = remember(questionItem) {
        mutableStateOf<Int?>(null)
    }

    val correctAnswerState = remember(questionItem) {
        mutableStateOf<Boolean?>(null)
    }

    val updateAnswer: (Int) -> Unit = remember(questionItem) {
        {
            answerState.value = it
            correctAnswerState.value = choicesState[it] == questionItem.answer
        }
    }
    Surface(
        modifier = Modifier.fillMaxWidth().fillMaxHeight(),
        color = AppColors.mDarkPurple
    ) {

        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            if (questionIndex.value >= 3) ShowProgress(questionIndex.value)
            QuestionTracker(questionIndex.value, viewModel.getTotalItemCount())
            DrawDottedLine(pathEffect)

            Column {
                Text(
                    text = questionItem.question,
                    modifier = Modifier.padding(6.dp).align(Alignment.Start).fillMaxHeight(0.3F),
                    fontSize = 17.sp,
                    color = AppColors.mOffWhite,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 22.sp
                )
            }

            //Choices
            choicesState.forEachIndexed { index, answerText ->
                Row(
                    modifier = Modifier.padding(3.dp).fillMaxWidth().height(45.dp)
                        .border(
                            width = 4.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    AppColors.mOffDarkPurple,
                                    AppColors.mOffDarkPurple
                                )
                            ),
                            shape = RoundedCornerShape(15.dp)
                        )
                        .clip(
                            RoundedCornerShape(
                                topStartPercent = 50,
                                topEndPercent = 50,
                                bottomEndPercent = 50,
                                bottomStartPercent = 50
                            )
                        )
                        .background(Color.Transparent),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (answerState.value == index),
                        onClick = {
                            updateAnswer(index)
                        },
                        modifier = Modifier.padding(start = 16.dp),
                        colors = RadioButtonDefaults.colors(
                            selectedColor =
                            if (correctAnswerState.value == true && index == answerState.value) {
                                Color.Green.copy(alpha = 0.2F)
                            } else Color.Red.copy(alpha = 0.2F)
                        )
                    )

                    val annotatedString = buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                fontWeight = FontWeight.Light,
                                color = if (correctAnswerState.value == true && index == answerState.value) {
                                    Color.Green
                                } else if (correctAnswerState.value == false && index == answerState.value) {
                                    Color.Red
                                } else {
                                    AppColors.mOffWhite
                                },
                                fontSize = 17.sp
                            )
                        ) {
                            append(answerText)
                        }
                    }
                    Text(annotatedString)
                }
            }

            Button(
                onClick = { onNextClicked(questionIndex.value) },
                modifier = Modifier.padding(3.dp).align(Alignment.CenterHorizontally),
                shape = RoundedCornerShape(34.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = AppColors.mLightBlue)
            ) {
                Text(
                    text = "Next",
                    modifier = Modifier.padding(4.dp),
                    color = AppColors.mOffWhite,
                    fontSize = 17.sp
                )
            }
        }
    }
}

@Composable
fun QuestionTracker(counter: Int = 10, outOf: Int = 100) {
    Text(
        text = buildAnnotatedString {
            withStyle(ParagraphStyle(textIndent = TextIndent.None)) {
                withStyle(
                    SpanStyle(
                        color = AppColors.mLightGray,
                        fontWeight = FontWeight.Bold,
                        fontSize = 27.sp
                    )
                ) {
                    append("Question $counter /")

                    withStyle(
                        SpanStyle(
                            color = AppColors.mLightGray,
                            fontWeight = FontWeight.Light,
                            fontSize = 14.sp
                        )
                    ) {
                        append("$outOf")
                    }
                }
            }
        }, modifier = Modifier.padding(20.dp)
    )
}

@Composable
fun DrawDottedLine(pathEffect: PathEffect) {
    Canvas(modifier = Modifier.fillMaxWidth().height(1.dp)) {
        drawLine(
            color = AppColors.mLightGray,
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            pathEffect = pathEffect
        )
    }
}

@Composable
@Preview
fun ShowProgress(score: Int = 240) {
    val gradient = Brush.linearGradient(listOf(Color(0XFFF95075), Color(0XFFBE6BE5)))

    val progressFactor by remember(score) {
        mutableStateOf(score * 0.005F)
    }
    Row(
        modifier = Modifier.padding(3.dp).fillMaxWidth().height(45.dp).border(
            width = 4.dp, brush = Brush.linearGradient(
                listOf(AppColors.mLightPurple, AppColors.mLightPurple)
            ), shape = RoundedCornerShape(34.dp)
        ).clip(
            RoundedCornerShape(
                topEndPercent = 50,
                topStartPercent = 50,
                bottomStartPercent = 50,
                bottomEndPercent = 50
            )
        ).background(
            Color.Transparent
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Button(
            onClick = {}, contentPadding = PaddingValues(1.dp),
            modifier = Modifier.fillMaxWidth(progressFactor).background(gradient),
            enabled = false, elevation = null,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Transparent,
                disabledBackgroundColor = Color.Transparent
            )
        ) {
            Text(
                text = (score * 10).toString(),
                modifier = Modifier.clip(shape = RoundedCornerShape(23.dp))
                    .fillMaxHeight(0.87F).fillMaxWidth().padding(6.dp),
                color = AppColors.mOffWhite,
                textAlign = TextAlign.Center
            )

        }
    }
}


