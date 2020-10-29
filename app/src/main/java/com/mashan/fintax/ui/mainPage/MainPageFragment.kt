package com.mashan.fintax.ui.mainPage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.firebase.ui.auth.AuthUI
import com.mashan.fintax.R
import kotlinx.android.synthetic.main.fragment_login.sign_out
import kotlinx.android.synthetic.main.home_page_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainPageFragment : Fragment(R.layout.home_page_fragment) {

    private val viewModel by viewModel<MainPageViewModel>()

    private val adapter = IconAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        house_btn.setOnClickListener{
            findNavController().navigate(MainPageFragmentDirections.actionNavHostFragmentToHouseFragment())
        }

        car_btn.setOnClickListener {
            findNavController().navigate(MainPageFragmentDirections.actionNavHostFragmentToCarFragment())
        }

        salary_btn.setOnClickListener {
            findNavController().navigate(MainPageFragmentDirections.actionNavHostFragmentToSalaryFragment())
        }

        percentage_btn.setOnClickListener {
            findNavController().navigate(MainPageFragmentDirections.actionNavHostFragmentToPercentageFragment())
        }

        summary_btn.setOnClickListener {
            findNavController().navigate(MainPageFragmentDirections.actionNavHostFragmentToSummaryFragment())
        }

        info_btn.setOnClickListener {
            findNavController().navigate(MainPageFragmentDirections.actionNavHostFragmentToInfoFragment())
        }
    }
}
