package com.route4me.cryptocurrencyx.di

import android.content.Context
import com.route4me.cryptocurrencyx.BuildConfig
import com.route4me.cryptocurrencyx.data.local.CryptoDao
import com.route4me.cryptocurrencyx.data.local.CryptoDatabase
import com.route4me.cryptocurrencyx.data.remote.CryptoAPI
import com.route4me.cryptocurrencyx.data.repository.CryptoRepositoryImpl
import com.route4me.cryptocurrencyx.domain.repository.CryptoRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): CryptoDatabase {
        return CryptoDatabase.getDatabase(context)
    }

    @Singleton
    @Provides
    fun provideDao(database: CryptoDatabase): CryptoDao {
        return database.cryptoDao()
    }

    @Provides
    @Singleton
    fun provideCryptoAPI(): CryptoAPI {
        val logging: HttpLoggingInterceptor = HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)

        val okHttpClient = OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .addInterceptor { chain: Interceptor.Chain ->
                val original = chain.request()
                val request = original.newBuilder()
                    .header("User-Agent", "Android")
                    .header("Accept", "application/json")
                    .method(original.method, original.body)
                    .build()
                chain.proceed(request)
            }

        return Retrofit.Builder()
            .client(okHttpClient.build())
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CryptoAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideCoinRepository(api: CryptoAPI, dao: CryptoDao): CryptoRepository {
        return CryptoRepositoryImpl(api, dao)
    }

}