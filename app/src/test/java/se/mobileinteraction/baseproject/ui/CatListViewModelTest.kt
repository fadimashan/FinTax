package se.mobileinteraction.baseproject.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.jraska.livedata.test
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import org.junit.Rule
import org.junit.Test
import se.mobileinteraction.baseproject.config.Dispatchers
import se.mobileinteraction.baseproject.data.Loading
import se.mobileinteraction.baseproject.data.Success
import se.mobileinteraction.baseproject.data.repository.CatsRepository
import se.mobileinteraction.baseproject.entities.Cat
import se.mobileinteraction.baseproject.ui.list.CatListItem
import se.mobileinteraction.baseproject.ui.list.CatListViewModel

class CatListViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val cats = listOf(Cat("id", "url"))
    private val repo = mockk<CatsRepository>()
    private val dispatchers = Dispatchers(Schedulers.trampoline(), Schedulers.trampoline(), Schedulers.trampoline())

    @Test
    fun `should fetch cats`() {
        // Given
        every { repo.getCats() }.returns(Observable.just(Loading(), Success(cats)))
        every { repo.getFavouriteCats() } returns BehaviorSubject.create<List<Cat>>().apply { onNext(listOf()) }
        val viewModel = CatListViewModel(repo, dispatchers)

        // When
        val cats = viewModel.cats

        // Then
        cats.test()
            .awaitValue()
            .assertValue(this.cats.map { CatListItem(it, isSelected = false, isFavourited = false) })
    }

    @Test
    fun `should update selected and unselect cats`() {
        // Given
        every { repo.getCats() }.returns(Observable.just(Loading(), Success(cats)))
        every { repo.getFavouriteCats() } returns BehaviorSubject.create<List<Cat>>().apply { onNext(listOf()) }
        val viewModel = CatListViewModel(repo, dispatchers)

        // When
        val cats = viewModel.cats.test()

        // Then
        viewModel.toggleCatSelected(this.cats[0])
        cats.awaitValue()
        cats.assertValue(listOf(CatListItem(this.cats[0], isSelected = true, isFavourited = false)))
        viewModel.toggleCatSelected(this.cats[0])
        cats.awaitValue()
        cats.assertValue(listOf(CatListItem(this.cats[0], isSelected = false, isFavourited = false)))
    }

    @Test
    fun `should ba able to favoutie a cat cat`() {
        // Given
        val behaviourSubject = BehaviorSubject.create<List<Cat>>().apply { onNext(listOf()) }
        every { repo.getCats() } returns Observable.just(Loading(), Success(cats))
        every { repo.getFavouriteCats() } returns behaviourSubject
        every { repo.toggleCatIsFavourite(any()) } answers {}
        val viewModel = CatListViewModel(repo, dispatchers)

        // When
        val liveData = viewModel.cats.test()

        // Then
        liveData.awaitValue().assertValue(cats.map { CatListItem(it, isSelected = false, isFavourited = false) })
        viewModel.toggleCatSelected(cats[0])
        viewModel.toggleSelectedIsFavourite()
        behaviourSubject.onNext(listOf(cats[0]))
        liveData.awaitValue().assertValue(cats.map { CatListItem(it, isSelected = false, isFavourited = it == cats[0]) })
    }
}
