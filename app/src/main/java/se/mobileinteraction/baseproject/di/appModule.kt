package se.mobileinteraction.baseproject.di

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import se.mobileinteraction.baseproject.BuildConfig
import se.mobileinteraction.baseproject.R
import se.mobileinteraction.baseproject.config.Dispatchers
import se.mobileinteraction.baseproject.data.api.CatsApi
import se.mobileinteraction.baseproject.data.cache.CatCache
import se.mobileinteraction.baseproject.data.cache.InMemoryCatCache
import se.mobileinteraction.baseproject.data.repository.CatsRepository
import se.mobileinteraction.baseproject.ui.detail.CatDetailViewModel
import se.mobileinteraction.baseproject.ui.favourites.FavouriteCatsViewModel
import se.mobileinteraction.baseproject.ui.list.CatListViewModel

val appModule = module {
    factory<CallAdapter.Factory> { RxJava2CallAdapterFactory.create() }
    factory<Converter.Factory> { GsonConverterFactory.create() }
    single {
        val clientBuilder = OkHttpClient.Builder()

        if (BuildConfig.DEBUG) clientBuilder.addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })

        Retrofit.Builder()
            .addCallAdapterFactory(get())
            .addConverterFactory(get())
            .baseUrl(androidApplication().getString(R.string.cats_api_base))
            .client(clientBuilder.build())
            .build()
    }

    single {
        val retrofit: Retrofit = get()
        retrofit.create(CatsApi::class.java)
    }
    single<CatCache> { InMemoryCatCache() }
    single { Dispatchers(AndroidSchedulers.mainThread(), Schedulers.io(), Schedulers.computation()) }
    single { CatsRepository(androidApplication(), get(), get()) }

    viewModel { CatListViewModel(get(), get()) }
    viewModel { CatDetailViewModel(get(), get()) }
    viewModel { FavouriteCatsViewModel(get(), get()) }
}
