package se.mobileinteraction.baseproject.ui.list

import androidx.lifecycle.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import se.mobileinteraction.baseproject.config.Dispatchers
import se.mobileinteraction.baseproject.data.Failed
import se.mobileinteraction.baseproject.data.Loading
import se.mobileinteraction.baseproject.data.Success
import se.mobileinteraction.baseproject.data.repository.CatsRepository
import se.mobileinteraction.baseproject.entities.Cat
import se.mobileinteraction.baseproject.utils.SingleLiveEvent

sealed class CatListEvents
data class ShowSnackbar(val message: String) : CatListEvents()

data class CatListItem(val cat: Cat, val isSelected: Boolean, val isFavourited: Boolean)

class CatListViewModel(
    private val repo: CatsRepository,
    private val dispatchers: Dispatchers
) : ViewModel() {

    private val disposable = CompositeDisposable()
    private val _loading = MutableLiveData<Boolean>()
    private val selectedCats = MutableLiveData<Set<Cat>>()
    private val allCats = MutableLiveData<List<Cat>>()
    private val favouriteCats = MutableLiveData<List<Cat>>()

    private val _cats = MediatorLiveData<List<CatListItem>>().apply {
        val allCatsList = mutableListOf<Cat>()
        val selectedCatsList = mutableListOf<Cat>()
        val favouriteCatsList = mutableListOf<Cat>()

        addSource(allCats) {
            allCatsList.addAll(it)
            value = allCatsList.map { cat ->
                CatListItem(cat, selectedCatsList.contains(cat), favouriteCatsList.contains(cat))
            }
        }

        addSource(favouriteCats) {
            favouriteCatsList.clear()
            favouriteCatsList.addAll(it)
            value = allCatsList.map { cat ->
                CatListItem(cat, selectedCatsList.contains(cat), favouriteCatsList.contains(cat))
            }
        }

        addSource(selectedCats) {
            selectedCatsList.clear()
            selectedCatsList.addAll(it)
            value = allCatsList.map { cat ->
                CatListItem(cat, selectedCatsList.contains(cat), favouriteCatsList.contains(cat))
            }
        }
    }

    val hasSelectedCats: LiveData<Boolean> = Transformations.map(selectedCats) { !it.isEmpty() }

    init {
        disposable += repo.getFavouriteCats()
            .subscribeBy { favouriteCats.value = it }
    }

    val cats: LiveData<List<CatListItem>> by lazy {
        disposable += repo
            .getCats()
            .subscribeOn(dispatchers.io)
            .observeOn(dispatchers.main)
            .subscribeBy {
                when (it) {
                    is Success -> allCats.value = it.data
                    is Failed -> it.throwable.message?.let { message ->
                        _events.value = ShowSnackbar(message)
                    }
                }

                _loading.value = it is Loading
            }

        _cats
    }

    private val _events = SingleLiveEvent<CatListEvents>()

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

    fun toggleCatSelected(cat: Cat) {
        if (selectedCats.value?.contains(cat) == true) {
            selectedCats.value = selectedCats.value?.minus(cat)
        } else {
            selectedCats.value = selectedCats.value?.plus(cat) ?: setOf(cat)
        }
    }

    fun toggleSelectedIsFavourite() {
        selectedCats.value?.forEach { repo.toggleCatIsFavourite(it) }
        selectedCats.value = setOf()
    }
}

