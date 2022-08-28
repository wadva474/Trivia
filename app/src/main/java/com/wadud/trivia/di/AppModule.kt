package com.wadud.trivia.di

import com.squareup.moshi.Moshi
import com.wadud.trivia.BuildConfig
import com.wadud.trivia.network.QuestionApi
import com.wadud.trivia.repository.QuestionRepository
import com.wadud.trivia.util.Constants
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Singleton
    @Provides
    fun provideQuestionApi(moshi: Moshi, client: Lazy<OkHttpClient>): QuestionApi {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(client.get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(QuestionApi::class.java)
    }

    @Singleton
    @Provides
    fun provideMoshi() = Moshi.Builder().build()

    @Singleton
    @Provides
    fun provideQuestionRepository(api: QuestionApi): QuestionRepository{
        return QuestionRepository(api)
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

    @Provides
    @Singleton
    fun provideGenericOkHttpClient(
        interceptor: HttpLoggingInterceptor
    ): OkHttpClient = OkHttpClient().newBuilder()
        .addInterceptor(interceptor).build()

}