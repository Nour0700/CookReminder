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
){
    override fun equals(other: Any?): Boolean {
        return if(other is NetworkCook){
            this.id == other.id
        }else {
            false
        }
    }
    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + lastTimeCooked.hashCode()
        return result
    }
}

class FirebaseDatabase(){
    companion object{

        private lateinit var userId: String
        fun mySetUserId(id: String){
            userId = id
        }
        //=========================================

        private val dbCooks = Firebase.database.getReference("users/$userId/cooks")

        private val _result = MutableLiveData<Exception?>()
        val addNewCookResult: LiveData<Exception?>
            get() = _result
        
        private val _allNetworkCooks = MutableLiveData<MutableList<NetworkCook>>()
        val allNetworkCooks: LiveData<MutableList<NetworkCook>>
            get() = _allNetworkCooks

        //=========================================

        fun addNewCook(networkCook: NetworkCook){
            networkCook.id = dbCooks.push().key
            dbCooks.child(networkCook.id!!).setValue(networkCook)
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
            dbCooks.child(networkCook.id!!).setValue(networkCook)
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
            dbCooks.child(networkCook.id!!).setValue(null)
                .addOnCompleteListener{
                    if(it.isSuccessful){
                        _result.value = null
                    }else{
                        _result.value = it.exception
                    }
                }
        }

        //=========================================

        fun getAllNetworkCooks(){
            dbCooks.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        val netWorkCooks = mutableListOf<NetworkCook>()
                        for(item in snapshot.children){
                            val networkCook = item.getValue(NetworkCook::class.java)
                            networkCook?.id = item.key
                            networkCook?.let { netWorkCooks.add(it) }
                        }
                        _allNetworkCooks.value = netWorkCooks
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }

            })
        }

        //=========================================

        private val childEventListenerAllData = object : ChildEventListener{
            //=========================================
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val networkCook = snapshot.getValue(NetworkCook::class.java)
                networkCook?.id = snapshot.key
                networkCook?.let {
                    val allNetworkCooks = _allNetworkCooks.value
                    if(allNetworkCooks != null){
                        allNetworkCooks.add(networkCook)
                        _allNetworkCooks.value = allNetworkCooks
                    }else{
                        val newAllNetworkCooks = mutableListOf(networkCook)
                        _allNetworkCooks.value = newAllNetworkCooks
                    }
                }
            }
            //=========================================
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val networkCook = snapshot.getValue(NetworkCook::class.java)
                networkCook?.id = snapshot.key
                networkCook?.let {
                    val allNetworkCooks = _allNetworkCooks.value
                    if(allNetworkCooks != null){
                        val index = allNetworkCooks.indexOf(networkCook)
                        allNetworkCooks[index] = networkCook
                        _allNetworkCooks.value = allNetworkCooks
                    }
                }
            }
            //=========================================
            override fun onChildRemoved(snapshot: DataSnapshot) {
                val networkCook = snapshot.getValue(NetworkCook::class.java)
                networkCook?.id = snapshot.key
                networkCook?.let {
                    val allNetworkCooks = _allNetworkCooks.value
                    allNetworkCooks?.remove(networkCook)
                    _allNetworkCooks.value = allNetworkCooks
                }
            }
            //=========================================
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }
            //=========================================
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