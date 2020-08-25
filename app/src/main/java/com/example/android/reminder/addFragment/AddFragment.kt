package com.example.android.reminder.addFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import com.example.android.reminder.R
import com.example.android.reminder.database.CookDatabase
import com.example.android.reminder.databinding.FragmentAddBinding
import com.google.android.material.snackbar.Snackbar

val TAG = "Nour"

class AddFragment : DialogFragment() {

    private lateinit var binding:FragmentAddBinding
    private lateinit var viewModel: AddViewModel
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //========================================= Init
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add, container, false)
        val application = requireNotNull(this.activity).application
        val databaseDao = CookDatabase.getInstance(application).cookDatabaseDao
        val viewModelFactory = AddViewModelFactory(databaseDao, application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(AddViewModel::class.java)
        binding.viewModel = viewModel


        //========================================= Observers
        viewModel.hide.observe(viewLifecycleOwner, Observer {
            if (it) {
                getSystemService(application, InputMethodManager::class.java)
                    ?.hideSoftInputFromWindow(binding.addButton.windowToken, 0)
                binding.addTextField.setText("")
                Toast.makeText(application, getString(R.string.sucessfully_added_cook_message), Toast.LENGTH_SHORT).show()
                viewModel.hideEnd()
                dismiss()
            }
        })

        viewModel.displayEmptyFieldMessage.observe(viewLifecycleOwner, Observer {
            if(it){
                getSystemService(application, InputMethodManager::class.java)
                    ?.hideSoftInputFromWindow(binding.addButton.windowToken, 0)
                Snackbar.make(
                    requireActivity().findViewById(android.R.id.content),
                    getString(R.string.empty_field_message),
                    Snackbar.LENGTH_SHORT // How long to display the message.
                ).show()
                viewModel.displayEmptyFieldMessageEnd()
                dismiss()
            }
        })

        
        binding.lifecycleOwner = this
        // Inflate the layout for this fragment
        return binding.root
    }
}