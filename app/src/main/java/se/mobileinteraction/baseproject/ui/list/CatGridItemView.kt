package se.mobileinteraction.baseproject.ui.list

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_cat.view.*
import se.mobileinteraction.baseproject.R
import se.mobileinteraction.baseproject.utils.load


class CatGridItemView(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attributeSet, defStyleAttr) {

    var favourite = false
        set(value) {
            field = value
            markFavouriteState()
        }


    var imageUrl: String? = null
        set(value) {
            if (value != field) {
                field = value
                loadImage()
            }
        }

    var currentlySelected = false
        set(value) {
            field = value
            markSelectedState()
        }

    init {
        parseAttributes(context, attributeSet, defStyleAttr)
        inflate(context)
    }

    private fun parseAttributes(
        context: Context,
        attributeSet: AttributeSet?,
        defStyleAttr: Int
    ) {
        val ta = context.obtainStyledAttributes(
            attributeSet,
            R.styleable.CatGridItemView,
            defStyleAttr,
            0
        )

        try {
            imageUrl = ta.getString(R.styleable.CatGridItemView_imageUrl)
            currentlySelected = ta.getBoolean(R.styleable.CatGridItemView_currentlySelected, false)
            favourite = ta.getBoolean(R.styleable.CatGridItemView_favourite, false)
        } finally {
            ta.recycle()
        }
    }

    private fun inflate(context: Context) {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.item_cat, this, true)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        loadImage()
        markSelectedState()
        markFavouriteState()
    }

    private fun markSelectedState() {
        selectedMarker?.visibility = if (currentlySelected) View.VISIBLE else View.INVISIBLE
    }

    private fun markFavouriteState() {
        favouriteMarker?.visibility = if (favourite) View.VISIBLE else View.INVISIBLE
    }

    private fun loadImage() {
        imageUrl?.let {
            imageView.load(it, measuredWidth, measuredHeight)
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        val savedState = state as SavedState
        super.onRestoreInstanceState(savedState.superState)
        imageUrl = savedState.imageUrl
        currentlySelected = savedState.currentlySelected

        (0 until childCount).forEach {
            @Suppress("UNCHECKED_CAST")
            getChildAt(it).restoreHierarchyState(savedState.childrenStates as SparseArray<Parcelable>)
        }
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val savedState = SavedState(superState!!)

        savedState.currentlySelected = currentlySelected
        savedState.childrenStates = SparseArray()
        savedState.imageUrl = imageUrl

        (0 until childCount).forEach {
            @Suppress("UNCHECKED_CAST")
            getChildAt(it).saveHierarchyState(savedState.childrenStates as SparseArray<Parcelable>)
        }

        return savedState
    }

    class SavedState : BaseSavedState {
        var favourite: Boolean = false
        var currentlySelected: Boolean = false
        var imageUrl: String? = null
        var childrenStates: SparseArray<Any>? = null

        constructor(superState: Parcelable) : super(superState)

        constructor(parcel: Parcel, classLoader: ClassLoader?) : super(parcel) {
            favourite = parcel.readByte().toInt() != 0
            currentlySelected = parcel.readByte().toInt() != 0
            imageUrl = parcel.readString()
            childrenStates = parcel.readSparseArray(classLoader)
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeByte((if (favourite) 1 else 0))
            out.writeByte((if (currentlySelected) 1 else 0).toByte())
            out.writeString(imageUrl)
            out.writeSparseArray(childrenStates)
        }

        companion object {

            @JvmField
            val CREATOR: Parcelable.ClassLoaderCreator<SavedState> =
                object : Parcelable.ClassLoaderCreator<SavedState> {
                    override fun createFromParcel(
                        source: Parcel,
                        loader: ClassLoader?
                    ): SavedState {
                        return SavedState(source, loader)
                    }

                    override fun createFromParcel(source: Parcel): SavedState {
                        return createFromParcel(source, null)
                    }

                    override fun newArray(size: Int): Array<SavedState?> {
                        return arrayOfNulls(size)
                    }
                }
        }
    }
}
