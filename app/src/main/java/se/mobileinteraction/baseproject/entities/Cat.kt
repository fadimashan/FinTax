package se.mobileinteraction.baseproject.entities

import com.google.gson.annotations.Expose

data class Cat(
    @Expose
    val id: String,
    @Expose
    val url: String
) : Comparable<Cat> {
    override fun compareTo(other: Cat): Int = id.compareTo(other.id)
}
