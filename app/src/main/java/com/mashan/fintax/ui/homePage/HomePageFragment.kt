package com.mashan.fintax.ui.homePage

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mashan.fintax.R

class HomePageFragment : Fragment() {

    companion object {
        fun newInstance() = HomePageFragment()
    }

    private lateinit var viewModel: HomePageViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.home_page_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(HomePageViewModel::class.java)
        // TODO: Use the ViewModel
    }

}