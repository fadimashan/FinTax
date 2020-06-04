package se.mobileinteraction.baseproject.ui.favourites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.fragment_favourite_cats.*
import kotlinx.android.synthetic.main.item_cat.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import se.mobileinteraction.baseproject.R
import se.mobileinteraction.baseproject.entities.Cat
import se.mobileinteraction.baseproject.ui.list.CatAdapter
import se.mobileinteraction.baseproject.utils.afterMeasure

private const val SPAN_COUNT = 3

class FavouriteCatsFragment : Fragment(R.layout.fragment_favourite_cats) {

    private val viewModel by viewModel<FavouriteCatsViewModel>()
    private val catAdapter by lazy(LazyThreadSafetyMode.NONE) { CatAdapter() }

    private val goToCatDetail = { itemView: View, cat: Cat ->
        with(findNavController()) {
            val id = cat.id
            val imageView = itemView.imageView
            val extras = FragmentNavigatorExtras(imageView to "imageTransition$id")
            val arguments = Bundle().apply { putString("catId", id) }

            navigate(R.id.catDetailFragment, arguments, null, extras)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        postponeEnterTransition()
        super.onViewCreated(view, savedInstanceState)

        recyclerView.apply {
            this.adapter = catAdapter
            layoutManager = GridLayoutManager(view.context, SPAN_COUNT)

            afterMeasure { startPostponedEnterTransition() }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.cats.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                catAdapter.cats = it
            }
        })
    }

    override fun onStart() {
        super.onStart()
        catAdapter.onClickListener = goToCatDetail
    }

    override fun onStop() {
        super.onStop()
        catAdapter.onClickListener = null
    }
}
