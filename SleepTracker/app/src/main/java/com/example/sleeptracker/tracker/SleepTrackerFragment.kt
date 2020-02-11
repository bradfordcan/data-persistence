package com.example.sleeptracker.tracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.sleeptracker.R
import com.example.sleeptracker.database.SleepDatabase
import com.example.sleeptracker.databinding.FragmentSleepTrackerBinding
import com.google.android.material.snackbar.Snackbar

class SleepTrackerFragment : Fragment() {

    private lateinit var binding:  FragmentSleepTrackerBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Get a reference to the binding object and inflate the fragment views.
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sleep_tracker, container, false)

        val application = requireNotNull(this.activity).application

        val dataSource = SleepDatabase.getInstance(application).sleepDatabaseDao

        val viewModelFactory = SleepTrackerViewModelFactory(dataSource, application)

        val sleepTrackerViewModel = ViewModelProviders.of(this, viewModelFactory).get(SleepTrackerViewModel::class.java)

        binding.sleepTrackerViewModel = sleepTrackerViewModel

        // TODO:  add GridLayout
        val manager = GridLayoutManager(activity, 3)
        binding.sleepList.layoutManager = manager

        val adapter = SleepNightAdapter(SleepNightAdapter.SleepNightListener {
            nightId -> Toast.makeText(context, "${nightId}", Toast.LENGTH_SHORT).show()
        })
        binding.sleepList.adapter = adapter
        sleepTrackerViewModel.nights.observe(this, Observer {
            it?.let {
                // adapter.data = it
                adapter.submitList(it)
            }
        })

        binding.setLifecycleOwner (this)

        sleepTrackerViewModel.navigateToSleepQuality.observe(this, Observer {
            night->
            night?.let{
                this.findNavController().navigate(SleepTrackerFragmentDirections.actionSleepTrackerFragmentToSleepQualityFragment(night.nightId))
                sleepTrackerViewModel.doneNavigating()
            }
        })

        sleepTrackerViewModel.showSnackbarEvent.observe(this, Observer {
            if(it == true) {
                Snackbar.make(activity!!.findViewById(android.R.id.content), getString(R.string.cleared_message), Snackbar.LENGTH_SHORT).show()
                sleepTrackerViewModel.doneShowingSnackbar()
            }
        })
        return binding.root
    }

}
