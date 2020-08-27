package com.example.android.reminder.network

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.lang.Exception

data class Cook(
    @get:Exclude
    var id: String? = null, // this want be stored in the object but will be the key of the object.
    val name: String? = null,
    var lastTimeCooked: Long = System.currentTimeMillis(),
)

class FirebaseDatabase{
    companion object{

        private lateinit var userId: String
        fun mySetUserId(id: String){
            userId = id
        }
        //=========================================

        private val dbCooks = Firebase.database.getReference("cooks")

        fun getInstance(): DatabaseReference{
            return dbCooks
        }

        private val _result = MutableLiveData<Exception?>()
        val result: LiveData<Exception?>
            get() = _result
        
        private val _allCooks = MutableLiveData<MutableList<Cook>>()
        val allCooks: LiveData<MutableList<Cook>>
            get() = _allCooks

        //=========================================

        fun addNewCook(cook: Cook){
            cook.id = dbCooks.push().key
            dbCooks.child(cook.id!!).setValue(cook)
                .addOnCompleteListener{
                    if(it.isSuccessful){
                        _result.value = null
                    }else{
                        _result.value = it.exception
                    }
                }
        }

        //=========================================

        fun updateCook(cook: Cook){
            dbCooks.child(cook.id!!).setValue(cook)
                .addOnCompleteListener{
                    if(it.isSuccessful){
                        _result.value = null
                    }else{
                        _result.value = it.exception
                    }
                }
        }

        //=========================================

        fun deleteCook(cook: Cook){
            dbCooks.child(cook.id!!).setValue(null)
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
            //=========================================
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val cook = snapshot.getValue(Cook::class.java)
                cook?.id = snapshot.key
                cook?.let {
                    val allCooks = _allCooks.value
                    if(allCooks != null){
                        allCooks.add(cook)
                        _allCooks.value = allCooks
                    }else{
                        val newAllNetworkCooks = mutableListOf(cook)
                        _allCooks.value = newAllNetworkCooks
                    }
                }
            }
            //=========================================
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val cook = snapshot.getValue(Cook::class.java)
                cook?.id = snapshot.key
                cook?.let {
                    val allCooks = _allCooks.value
                    if(allCooks != null){
                        val cookToBeChanged = allCooks.firstOrNull { it.id == cook.id }
                        val index = allCooks.indexOf(cookToBeChanged)
                        allCooks[index] = cook
                        _allCooks.value = allCooks
                    }
                }
            }
            //=========================================
            override fun onChildRemoved(snapshot: DataSnapshot) {
                val cook = snapshot.getValue(Cook::class.java)
                cook?.id = snapshot.key
                cook?.let {
                    val allCooks = _allCooks.value
                    allCooks?.remove(cook)
                    _allCooks.value = allCooks
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