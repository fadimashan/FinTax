package se.mobileinteraction.baseproject.ui.list

import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_cat.view.*
import se.mobileinteraction.baseproject.R
import se.mobileinteraction.baseproject.entities.Cat

typealias CatItemOnClickListener = (View, Cat) -> Unit
typealias OnCatItemLongPressListener = (View, Cat) -> Unit

class CatAdapter : RecyclerView.Adapter<CatAdapter.CatHolder>() {

    var onClickListener: CatItemOnClickListener? = null
    var onLongPressListener: OnCatItemLongPressListener? = null

    private val _cats = mutableListOf<CatListItem>()
    var cats: List<CatListItem>
        get() = _cats
        set(value) {
            val diffResult = DiffUtil.calculateDiff(
                CatDiffCallback(
                    _cats,
                    value
                )
            )
            _cats.clear()
            _cats.addAll(value)
            diffResult.dispatchUpdatesTo(this)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatHolder {
        return CatHolder(CatGridItemView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        })
    }

    override fun getItemCount(): Int = cats.size

    override fun onBindViewHolder(holder: CatHolder, position: Int) {
        with(cats[position]) {
            holder.view.also {
                it.imageUrl = cat.url
                it.currentlySelected = isSelected
                it.contentDescription = holder.itemView.context.getString(
                    if (isSelected) R.string.cat_item_selected else R.string.cat_item_unselected
                )
                it.favourite = isFavourited
                it.setOnClickListener { onClickListener?.invoke(holder.itemView.imageView, cat) }
                it.setOnLongClickListener {
                    onLongPressListener?.invoke(holder.itemView, cat)
                    false
                }
            }

            ViewCompat.setTransitionName(holder.view.imageView, "imageTransition${cat.id}")
        }
    }

    class CatHolder(val view: CatGridItemView) : RecyclerView.ViewHolder(view)

    class CatDiffCallback(
        private val oldItems: List<CatListItem>,
        private val newItems: List<CatListItem>
    ) : DiffUtil.Callback() {
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldItems[oldItemPosition].cat.id == newItems[newItemPosition].cat.id

        override fun getOldListSize(): Int = oldItems.size

        override fun getNewListSize(): Int = newItems.size

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldItems[oldItemPosition] == newItems[newItemPosition]

    }
}

