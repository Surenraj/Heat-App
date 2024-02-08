package com.thinkgas.heatapp.di.module

import com.thinkgas.heatapp.data.remote.api.TpiApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule
{
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit
    {
        val client = OkHttpClient().newBuilder()
            .connectTimeout(180, TimeUnit.SECONDS)
            .writeTimeout(180, TimeUnit.SECONDS)
            .readTimeout(180, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("http://164.52.205.22/heat_app_api_prod/")
//            .baseUrl("https://heat.think-gas.com/heat_app_api_prod/")
            //.baseUrl("https://heat.think-gas.com/heat_app_api_build_dev/")
            .client(client)
            .build()
    }

    @Provides
    @Singleton
    fun provideTpiApiService(retrofit: Retrofit): TpiApiService {
        return retrofit.create(TpiApiService::class.java)
    }

}