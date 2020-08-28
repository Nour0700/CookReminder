package com.example.android.reminder.mainFragment

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.android.reminder.network.Cook
import com.example.android.reminder.network.FirebaseDatabase
import com.example.android.reminder.network.FirebaseUserLiveData
import androidx.lifecycle.map
import com.example.android.reminder.addFragment.TAG
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.*


val DESCING_ORDER = 1
val ASCENDING_ORDER = 2



class MainViewModel: ViewModel(){

    val cooks = FirebaseDatabase.allCooks
    val result = FirebaseDatabase.result
    lateinit var userId : String

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
                FirebaseDatabase.updateCook(cook, userId)
            }
        }
    }

    fun deleteCook(cook:Cook){
        uiScope.launch {
            withContext(Dispatchers.IO){
                FirebaseDatabase.deleteCook(cook, userId)
            }
        }
    }

    //=========================================

    val authenticationState = FirebaseUserLiveData().map { user ->
        if (user != null) {
            AuthenticationState.AUTHENTICATED
        } else {
            AuthenticationState.UNAUTHENTICATED
        }
    }

    //=========================================

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
        FirebaseDatabase.clearListeners()
    }

}

enum class AuthenticationState {
    AUTHENTICATED, UNAUTHENTICATED, INVALID_AUTHENTICATION
}