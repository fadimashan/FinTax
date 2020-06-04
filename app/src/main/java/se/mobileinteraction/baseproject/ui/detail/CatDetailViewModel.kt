package se.mobileinteraction.baseproject.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import se.mobileinteraction.baseproject.config.Dispatchers
import se.mobileinteraction.baseproject.data.Failed
import se.mobileinteraction.baseproject.data.Success
import se.mobileinteraction.baseproject.data.repository.CatsRepository
import se.mobileinteraction.baseproject.entities.Cat
import timber.log.Timber

class CatDetailViewModel(private val repo: CatsRepository, private val dispatchers: Dispatchers) : ViewModel() {
    private val disposable = CompositeDisposable()

    fun getCat(catId: String): LiveData<Cat> {
        val data = MutableLiveData<Cat>()

        disposable += repo.getCat(catId)
            .subscribeOn(dispatchers.io)
            .observeOn(dispatchers.main, true)
            .subscribeBy(
                onNext = {
                    when (it) {
                        is Success -> data.postValue(it.data)
                        is Failed -> Timber.e(it.throwable)
                    }
                },
                onError = Timber::e
        )

        return data
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}
