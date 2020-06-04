package se.mobileinteraction.baseproject.data.repository

import android.app.Application
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import se.mobileinteraction.baseproject.R
import se.mobileinteraction.baseproject.data.Failed
import se.mobileinteraction.baseproject.data.Loading
import se.mobileinteraction.baseproject.data.Resource
import se.mobileinteraction.baseproject.data.Success
import se.mobileinteraction.baseproject.data.api.CatsApi
import se.mobileinteraction.baseproject.data.cache.CatCache
import se.mobileinteraction.baseproject.entities.Cat

class CatsRepository(application: Application, private val api: CatsApi, private val cache: CatCache) {
    private val apiKey = application.getString(R.string.api_key)
    private val favouriteCats = BehaviorSubject.create<List<Cat>>().apply { onNext(listOf()) }

    fun getCats(): Observable<Resource<List<Cat>>> {
        val network = api.getCats(apiKey = apiKey, page = 100, limit = 36, order = "asc")
            .toObservable()
            .doOnNext { cache.putCats(it) }
            .map { Success(it) as Resource<List<Cat>> }
            .onErrorReturn { Failed(it) }

        val cachedCats = cache.getCats()
        val cached = if (!cachedCats.isEmpty()) Observable.just(Success(cachedCats)) else Observable.empty()

        return Observable.merge(cached, network)
            .startWith(Loading())
            .distinctUntilChanged()
    }

    fun getCat(id: String): Observable<Resource<Cat>> {
        val network: Observable<Resource<Cat>> = api.getCat(apiKey, id)
            .toObservable()
            .doOnNext { cache.putCat(it) }
            .map { Success(it) as Resource<Cat> }
            .onErrorReturn { Failed(it) }

        val cat = cache.getCat(id)
        val cached = if (cat != null) Observable.just(Success(cat)) else Observable.empty()

        return Observable.merge(cached, network)
            .startWith(Loading())
    }

    fun toggleCatIsFavourite(cat: Cat) {
        val currentFavourites = (favouriteCats.value ?: listOf())
        if (currentFavourites.contains(cat)) {
            favouriteCats.onNext(currentFavourites - cat)
        } else {
            favouriteCats.onNext(currentFavourites + cat)
        }
    }

    fun getFavouriteCats(): Observable<List<Cat>> = favouriteCats
}

