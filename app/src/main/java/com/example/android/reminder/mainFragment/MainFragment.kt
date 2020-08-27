package com.example.android.reminder.mainFragment

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.android.reminder.R
import com.example.android.reminder.addFragment.AddFragment
import com.example.android.reminder.addFragment.TAG
import com.example.android.reminder.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    //========================================= Inits

    lateinit var viewModel: MainViewModel
    lateinit var binding: FragmentMainBinding
    lateinit var adapter: CookAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)
        val application = requireNotNull(this.activity).application
        val viewModelFactory = MainViewModelFactory(application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
        binding.viewModel = viewModel

        //for menu navigation
        setHasOptionsMenu(true)

        // this call allow to update the layout from the viewModel using liveData.
        binding.setLifecycleOwner(this)
        return binding.root

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // passing viewLifecycleOwner to be aware of the liveCycle of the fragment and not
        // notify this observer when the data changes and the fragment is not on screen.
        viewModel.shouldNavigateToAddFragment.observe(viewLifecycleOwner, Observer{ shouldNavigate ->
            if (shouldNavigate) {
                AddFragment().show(childFragmentManager, "")
                viewModel.endNavigationToAddFragment()
            }
        })

        // to display the no data text
        viewModel.noDataTextVisible.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.noDataText.visibility = View.VISIBLE
            } else {
                binding.noDataText.visibility = View.GONE
            }
        })


        viewModel.result.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                Toast.makeText(requireContext(), "Unable to do changes", Toast.LENGTH_SHORT).show()
            }
        })

        //========================================= RecyclerView

        // here we implement the click listener that will ask view model what to do
        val updateCookLastCookDateListener = UpdateCookLastCookDateListener {
            viewModel.updateCook(it)
        }
        val deleteItemListener = DeleteItemListener {
            viewModel.deleteCook(it)
        }

        adapter = CookAdapter(updateCookLastCookDateListener, deleteItemListener)
        binding.cookList.adapter = adapter

        viewModel.cooks.observe(viewLifecycleOwner, Observer {
            it?.let {
                // this is used to tell the listAdapter which list to keep track off.
                adapter.addHeaderAndSubmitList(it, viewModel.cookListOrder)
            }
        })

    }

    //========================================= for menu navigation
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.overflow_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.asc_order_menu_item -> {
                adapter.addHeaderAndSubmitList(viewModel.cooks.value, ASCENDING_ORDER)
                super.onOptionsItemSelected(item)
            }
            R.id.desc_order_menu_item -> {
                adapter.addHeaderAndSubmitList(viewModel.cooks.value, DESCING_ORDER)
                super.onOptionsItemSelected(item)
            }
            else -> return NavigationUI.onNavDestinationSelected(item, findNavController())
                    || super.onOptionsItemSelected(item)
        }
    }


}

