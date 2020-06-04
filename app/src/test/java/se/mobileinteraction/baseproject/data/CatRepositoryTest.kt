package se.mobileinteraction.baseproject.data

import android.app.Application
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import se.mobileinteraction.baseproject.R
import se.mobileinteraction.baseproject.data.api.CatsApi
import se.mobileinteraction.baseproject.data.cache.CatCache
import se.mobileinteraction.baseproject.data.repository.CatsRepository
import se.mobileinteraction.baseproject.entities.Cat

class CatRepositoryTest {
    private val application = mockk<Application>()
    private val api = mockk<CatsApi>()
    private val cats = listOf(Cat("id1", "url1"), Cat("id2", "url2"))
    private val cache = mockk<CatCache>()

    @Before
    fun setUp() {
        every { application.getString(R.string.api_key) }.returns("123")
        every { cache.putCats(any()) }.answers {}
        every { cache.putCat(any()) }.answers {}
    }

    @Test
    fun `should fetch cats`() {
        // Given
        every { cache.getCats() }.returns(listOf())
        every { api.getCats(apiKey = "123", limit = any(), page = any(), order = any()) }.returns(Single.just(cats))
        val repo = CatsRepository(application, api, cache)

        // When
        val observable = repo.getCats()

        // Then
        observable.test()
            .assertValueSequence(listOf(Loading(), Success(cats)))
            .assertComplete()
    }

    @Test
    fun `should fetch cached first`() {
        // Given
        val cachedCats = cats.subList(0, 1)
        every { cache.getCats() }.returns(cachedCats)
        every { api.getCats(apiKey = "123", limit = any(), page = any(), order = any()) }.returns(Single.just(cats))
        val repo = CatsRepository(application, api, cache)

        // When
        val observable = repo.getCats()

        // Then
        observable.test()
            .assertValueSequence(listOf(Loading(), Success(cachedCats), Success(cats)))
            .assertComplete()
    }

    @Test
    fun `should only emmit uniques`() {
        // Given
        every { cache.getCats() }.returns(cats)
        every { api.getCats(apiKey = "123", limit = any(), page = any(), order = any()) }.returns(Single.just(cats))
        val repo = CatsRepository(application, api, cache)

        // When
        val observable = repo.getCats()

        // Then
        observable.test()
            .assertValueSequence(listOf(Loading(), Success(cats)))
            .assertComplete()
    }

    @Test
    fun `should fetch single cat`() {
        // Given
        val cat = Cat("id", "url")
        every { cache.getCat(any()) }.returns(null)
        every { api.getCat(apiKey = "123", id = cat.id) }.returns(Single.just(cat))
        val repo = CatsRepository(application, api, cache)

        // When
        val observable = repo.getCat(cat.id)

        // Then
        observable.test()
            .assertValueSequence(listOf(Loading(), Success(cat)))
            .assertComplete()
    }

    @Test
    fun `should fetch a single cat from cache`() {
        // Given
        val cachedCat = Cat("id", "url")
        val cat = Cat("id", "url2")
        every { cache.getCat(any()) }.returns(cachedCat)
        every { api.getCat(apiKey = "123", id = cat.id) }.returns(Single.just(cat))
        val repo = CatsRepository(application, api, cache)

        // When
        val observable = repo.getCat(cat.id)

        // Then
        observable.test()
            .assertValueSequence(listOf(Loading(), Success(cachedCat), Success(cat)))
            .assertComplete()
    }

    @Test
    fun `should handle errors when fetching`() {
        // Given
        val error = Exception("Error")
        every { api.getCats(any(), any(), any(), any()) }.returns(Single.error<List<Cat>>(error))
        every { cache.getCats() }.returns(listOf())
        val repo = CatsRepository(application, api, cache)

        // When
        val observable = repo.getCats()

        // Then
        observable.test()
            .assertValueSequence(listOf(Loading(), Failed(error)))
            .assertComplete()
    }

    @Test
    fun `should be able to favourite cat`() {
        // Given
        every { api.getCats(any(), any(), any(), any()) }.returns(Single.just(cats))
        every { cache.getCats() }.returns(listOf())
        val repo = CatsRepository(application, api, cache)

        // When
        val observable = repo.getFavouriteCats().test()
        repo.toggleCatIsFavourite(cats[0])

        // Then
        observable.assertValueSequence(mutableListOf(listOf(), listOf(cats[0])))
    }
}
