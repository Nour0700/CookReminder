package com.example.android.reminder.Network

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.lang.Exception

data class NetworkCook(
    @get:Exclude
    var id: String? = null, // this want be stored in the object but will be the key of the object.
    val name: String? = null,
    val lastTimeCooked: Long = System.currentTimeMillis(),
    @get:Exclude
    var isDeleted : Boolean = false
)

class FirebaseDatabase(){
    companion object{

        //=========================================

        private val dbCooks = Firebase.database.getReference("cooks")

        private val _result = MutableLiveData<Exception?>()
        val addNewCookResult: LiveData<Exception?>
            get() = _result
        
        private val _allNetworkCooks = MutableLiveData<List<NetworkCook>>()
        val allNetworkCooks: LiveData<List<NetworkCook>>
            get() = _allNetworkCooks

        private val _newNetworkCook = MutableLiveData<NetworkCook>()
        val newNetworkCook: LiveData<NetworkCook>
            get() = _newNetworkCook

        //=========================================

        fun addNewCook(networkCook: NetworkCook){
            networkCook.id = dbCooks.push().key // this should be the key form the
            dbCooks.child(networkCook.id!!).setValue(networkCook) // as saying
                .addOnCompleteListener{
                    if(it.isSuccessful){
                        _result.value = null
                    }else{
                        _result.value = it.exception
                    }
                }
        }

        //=========================================

        fun updateNetworkCook(networkCook: NetworkCook){
            dbCooks.child(networkCook.id!!).setValue(networkCook) // as saying
                .addOnCompleteListener{
                    if(it.isSuccessful){
                        _result.value = null
                    }else{
                        _result.value = it.exception
                    }
                }
        }

        //=========================================

        fun deleteNetworkCook(networkCook: NetworkCook){
            dbCooks.child(networkCook.id!!).setValue(networkCook) // as saying
                .addOnCompleteListener{
                    if(it.isSuccessful){
                        _result.value = null
                    }else{
                        _result.value = it.exception
                    }
                }
        }

        //=========================================

        private val childEventListenerAllData = object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val networkCook = snapshot.getValue(NetworkCook::class.java)
                networkCook?.id = snapshot.key
                _newNetworkCook.value = networkCook
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val networkCook = snapshot.getValue(NetworkCook::class.java)
                networkCook?.id = snapshot.key
                _newNetworkCook.value = networkCook
            }
            override fun onChildRemoved(snapshot: DataSnapshot) {
                val networkCook = snapshot.getValue(NetworkCook::class.java)
                networkCook?.id = snapshot.key
                networkCook?.isDeleted = true
                _newNetworkCook.value = networkCook

            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }
            override fun onCancelled(error: DatabaseError) {
            }
        }

        fun getRealtimeUpdate(){
            dbCooks.addChildEventListener(childEventListenerAllData)
        }

        //=========================================

        fun clearListeners(){
            dbCooks.removeEventListener(childEventListenerAllData)
        }

    }
}