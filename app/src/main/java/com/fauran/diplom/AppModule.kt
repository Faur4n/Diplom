package com.fauran.diplom

import android.content.Context
import com.fauran.diplom.network.SpotifyApi
import com.fauran.diplom.network.SpotifyApiService
import com.fauran.diplom.network.SpotifyAuthInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.readystatesoftware.chuck.ChuckInterceptor
import com.skydoves.sandwich.coroutines.CoroutinesResponseCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    private const val SPOTIFY_BASE_URL = "https://api.spotify.com/v1/"


    @Singleton
    @Provides
    fun provideRetrofit(
        gson: Gson,
        authInterceptor: SpotifyAuthInterceptor,
        @ApplicationContext context: Context
    ): Retrofit {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val clientBuilder = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)

        if (BuildConfig.DEBUG)
            clientBuilder
                .addInterceptor(ChuckInterceptor(context))

        val builder = Retrofit.Builder()
            .baseUrl(SPOTIFY_BASE_URL)
            .addConverterFactory(
                GsonConverterFactory.create(gson)
            )
            .addCallAdapterFactory(CoroutinesResponseCallAdapterFactory())
            .client(clientBuilder.build())

        return builder.build()
    }

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder().setLenient().disableHtmlEscaping()
        .create()


    @Provides
    @Singleton
    fun provideSpotifyApiService(retrofit: Retrofit): SpotifyApiService =
        retrofit.create(SpotifyApiService::class.java)

    @Provides
    @Singleton
    fun provideSpotifyApi(api : SpotifyApiService) = SpotifyApi(api)

    @Provides
    @Singleton
    fun provideSpotifyAuthInterceptor(@ApplicationContext context: Context) : SpotifyAuthInterceptor =
        SpotifyAuthInterceptor(context)
}
