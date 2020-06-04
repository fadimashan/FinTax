package se.mobileinteraction.baseproject.ui.detail

import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.transition.TransitionInflater
import kotlinx.android.synthetic.main.fragment_cat_detail.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import se.mobileinteraction.baseproject.R
import se.mobileinteraction.baseproject.utils.load
import timber.log.Timber

class CatDetailFragment : Fragment(R.layout.fragment_cat_detail) {
    private val viewModel by viewModel<CatDetailViewModel>()

    private val id: String
        get() = CatDetailFragmentArgs.fromBundle(arguments!!).catId

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.getCat(id).observe(viewLifecycleOwner, Observer { cat ->
            if (cat != null) {
                imageView.load(cat.url) { startPostponedEnterTransition() }
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val transition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        sharedElementEnterTransition = transition
        sharedElementReturnTransition = transition
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()

        ViewCompat.setTransitionName(imageView, "imageTransition$id")
        Timber.e("${ViewCompat.getTransitionName(imageView)}")
    }
}
