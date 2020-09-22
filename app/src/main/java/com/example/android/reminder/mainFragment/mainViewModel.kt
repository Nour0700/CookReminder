package com.example.android.reminder.mainFragment

import android.app.Application
import androidx.lifecycle.*
import com.example.android.reminder.database.Cook
import com.example.android.reminder.database.CookDatabaseDao
import kotlinx.coroutines.*


val DESCING_ORDER = 1
val ASCENDING_ORDER = 2

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
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    var cookListOrder: Int = ASCENDING_ORDER


    // this value is updated each time the data changes
    val noDataTextVisible = Transformations.map(cooks){
        it.isEmpty()
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

    fun updateCook(cook:Cook){
        uiScope.launch {
            withContext(Dispatchers.IO){
                cook.lastTimeCooked = System.currentTimeMillis()
                databaseDao.update(cook)
            }
        }
    }

    fun deleteCook(cook:Cook){
        uiScope.launch {
            withContext(Dispatchers.IO){
                databaseDao.delete(cook)
            }
        }
    }

    //=========================================

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}