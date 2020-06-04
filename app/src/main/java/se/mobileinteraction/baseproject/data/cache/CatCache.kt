package se.mobileinteraction.baseproject.data.cache

import se.mobileinteraction.baseproject.entities.Cat
import java.util.concurrent.ConcurrentHashMap

interface CatCache {
    fun putCat(cat: Cat)
    fun putCats(cats: List<Cat>)
    fun getCat(id: String): Cat?
    fun getCats(): List<Cat>
}

class InMemoryCatCache : CatCache {

    private val cats = ConcurrentHashMap<String, Cat>()
    private val catsSorted = sortedSetOf<Cat>()

    override fun putCat(cat: Cat) {
        cats[cat.id] = cat
        catsSorted += cat
    }

    override fun getCat(id: String): Cat? = cats[id]

    override fun putCats(cats: List<Cat>) = cats.forEach { putCat(it) }

    override fun getCats(): List<Cat> = catsSorted.toList()
}
