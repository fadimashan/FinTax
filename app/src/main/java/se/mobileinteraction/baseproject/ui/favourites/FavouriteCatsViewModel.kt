package se.mobileinteraction.baseproject.ui.favourites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import se.mobileinteraction.baseproject.config.Dispatchers
import se.mobileinteraction.baseproject.data.repository.CatsRepository
import se.mobileinteraction.baseproject.ui.list.CatListItem

class FavouriteCatsViewModel(private val repo: CatsRepository, private val dispatchers: Dispatchers) : ViewModel() {
    private val disposables = CompositeDisposable()

    private val _cats = MutableLiveData<List<CatListItem>>()
    val cats: LiveData<List<CatListItem>>
        get() {
            disposables += repo.getFavouriteCats()
                .subscribeOn(dispatchers.computation)
                .observeOn(dispatchers.main)
                .map { cats ->
                    cats.map { cat ->
                        CatListItem(cat, isSelected = false, isFavourited = true)
                    }
                }
                .subscribeBy {
                    if (it != null) {
                        _cats.value = it
                    }
                }

            return _cats
        }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}
