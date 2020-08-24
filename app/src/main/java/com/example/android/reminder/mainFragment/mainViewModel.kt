package com.example.android.reminder.mainFragment

import android.app.Application
import androidx.lifecycle.*
import com.example.android.reminder.database.CookDatabaseDao
import java.util.*


class MainViewModelFactory(private val dataSource: CookDatabaseDao, private val application: Application) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(dataSource, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


class MainViewModel(val databaseDao: CookDatabaseDao, application: Application): AndroidViewModel(application){

    val cooks = databaseDao.getAllCooks()

    // this value is updated each time the data changes
    val noDataTextVisible = Transformations.map(cooks){
        it.isEmpty()
    }

    //=========================================
    // this code will be used to change the date when we are going to display it
    val _date = MutableLiveData<Date>()
    val date:LiveData<String> = Transformations.map(_date){
        it.toString()
    }

    //=========================================

    private val _shouldNavigateToAddFragment = MutableLiveData<Boolean>()
    val shouldNavigateToAddFragment: LiveData<Boolean>
        get() = _shouldNavigateToAddFragment

    fun navigateToAddFragment(){
        _shouldNavigateToAddFragment.value = true
    }
    fun endNavigationToAddFragment(){
        _shouldNavigateToAddFragment.value = false
    }


    //=========================================

    init{
        _shouldNavigateToAddFragment.value =false
    }

    //=========================================
    override fun onCleared() {
        super.onCleared()
    }
}