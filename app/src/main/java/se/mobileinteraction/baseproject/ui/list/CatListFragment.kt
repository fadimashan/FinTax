package se.mobileinteraction.baseproject.ui.list

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.fragment_cat_list.*
import kotlinx.android.synthetic.main.fragment_cat_list.view.*
import kotlinx.android.synthetic.main.item_cat.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import se.mobileinteraction.baseproject.R
import se.mobileinteraction.baseproject.entities.Cat
import se.mobileinteraction.baseproject.utils.Reselectable
import se.mobileinteraction.baseproject.utils.afterMeasure

private const val COLUMNS = 3

class CatListFragment : Fragment(R.layout.fragment_cat_list), Reselectable {

    private val viewModel by viewModel<CatListViewModel>()
    private val adapter: CatAdapter by lazy { CatAdapter() }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        postponeEnterTransition()

        super.onViewCreated(view, savedInstanceState)
        recyclerView.apply {
            adapter = this@CatListFragment.adapter
            layoutManager = GridLayoutManager(view.context, COLUMNS)

            afterMeasure { startPostponedEnterTransition() }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.cats.observe(viewLifecycleOwner, Observer { cats ->
            if (cats != null) {
                adapter.cats = cats
            }
        })

        viewModel.hasSelectedCats.observe(viewLifecycleOwner, Observer { hasSelected ->
            if (hasSelected == true) {
                adapter.onClickListener = toggleCatSelectedClick
                favouriteButton.animate()
                    .translationY(0f)
                    .start()
            } else {
                adapter.onClickListener = goToDetailsOnClickListener
                favouriteButton.post {
                    favouriteButton.animate()
                        .translationY(favouriteButton.height.toFloat())
                        .start()
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()

        adapter.onClickListener = goToDetailsOnClickListener
        adapter.onLongPressListener = toggleCatSelectedClick
        favouriteButton.setOnClickListener { viewModel.toggleSelectedIsFavourite() }
    }

    private val goToDetailsOnClickListener = { itemView: View, cat: Cat ->
        with(findNavController()) {
            val id = cat.id
            val imageView = itemView.imageView
            val extras = FragmentNavigatorExtras(imageView to "imageTransition$id")
            val arguments = Bundle().apply { putString("catId", id) }

            navigate(R.id.catDetailFragment, arguments, null, extras)
        }
    }

    private val toggleCatSelectedClick = { _: View, cat: Cat ->
        viewModel.toggleCatSelected(cat)
    }

    override fun onStop() {
        super.onStop()
        adapter.onClickListener = null
        adapter.onLongPressListener = null
    }

    override fun onReselected() {
        recyclerView.smoothScrollToPosition(0)
    }
}
