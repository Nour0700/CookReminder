package com.example.android.reminder.mainFragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.DiffUtil
import com.example.android.reminder.R
import com.example.android.reminder.database.Cook
import com.example.android.reminder.database.CookDatabase
import com.example.android.reminder.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    //========================================= Inits

    lateinit var viewModel: MainViewModel
    lateinit var binding : FragmentMainBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)
        val application = requireNotNull(this.activity).application
        val databaseDao = CookDatabase.getInstance(application).cookDatabaseDao
        val viewModelFactory = MainViewModelFactory(databaseDao, application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
        binding.viewModel = viewModel

        //========================================= Observers

        // passing viewLifecycleOwner to be aware of the liveCycle of the fragment and not
        // notify this observer when the data changes and the fragment is not on screen.
        viewModel.shouldNavigateToAddFragment.observe(viewLifecycleOwner, Observer<Boolean> { shouldNavigate ->
            if (shouldNavigate) {
                findNavController().navigate(
                    MainFragmentDirections.actionMainFragmentToAddFragment()
                )
                // we call this because we want the code above get executed just once
                // if any configuration this observer will get the recent data which want allow to
                // execute the code
                viewModel.endNavigationToAddFragment()
                //GlobalScope.launch {
                //    withContext(Dispatchers.IO){
                //        databaseDao.deleteAllData()
                //    }
                //}
            }
        })

        // to display the no data text
        viewModel.noDataTextVisible.observe(viewLifecycleOwner, Observer {
            if (it){
                binding.noDataText.visibility = View.VISIBLE
            }else{
                binding.noDataText.visibility = View.GONE
            }
        })

        //=========================================

        //for menu navigation
        setHasOptionsMenu(true)

        // this call allow to update the layout from the viewModel using liveData.
        binding.setLifecycleOwner(this)
        // Inflate the cook_item for this fragment

        //========================================= RecyclerView
        val adapter = CookAdapter(databaseDao)
        binding.cookList.adapter = adapter

        viewModel.cooks.observe(viewLifecycleOwner, Observer {
            it?.let {
                // this is used to tell the listAdapter which list to keep track off.
                adapter.submitList(it)
            }
        })



        return binding.root
    }
    //========================================= for menu navigation
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.overflow_menu, menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item, findNavController())
                ||super.onOptionsItemSelected(item)
    }
}

