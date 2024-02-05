package com.example.starmebucks.starbuckslist

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.starmebucks.model.PlacesApiResponse
import com.example.starmebucks.repository.StarbucksRepository
import com.example.starmebucks.utils.resource.Resource
import com.example.starmebucks.model.Starbucks
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class StarBucksListViewModel @Inject constructor(
    private val starbucksRepository: StarbucksRepository
):ViewModel(){

    private val _starbucksList = MutableLiveData<PlacesApiResponse?>()
    // Public getter LiveData
    val starbucksList: LiveData<PlacesApiResponse?> = _starbucksList

    var loadError = MutableLiveData<String>("")
    var isLoading = MutableLiveData<Boolean>(false)

    fun getStarbucksList(latitude: Double, longitude: Double) {
        isLoading.postValue(true)
        viewModelScope.launch {
            val response = starbucksRepository.fetchNearbyStarbucks(latitude, longitude)
            response.collect {
              when(it){
                      is Resource.Success ->{
                          _starbucksList.postValue(it.value)
                          isLoading.postValue(it is Resource.Loading)
                      }
                      is Resource.Failure ->{
                          loadError.postValue(it.errorMsg.toString())
                      }
                      is Resource.Loading ->{
//                          isLoading.value = true
                      }
                  }
            }
        }
    }

//    init {
//        getStarbucksList(28.621271, 77.061325)
//    }

//    val starbucksList: MutableStateFlow<PlacesApiResponse?> = MutableStateFlow(null)
//    var loadError = mutableStateOf("")
//    var isLoading = mutableStateOf(false)
//
//     fun getStarbucksList(latitude:Double,longitude:Double) {
//      viewModelScope.launch{
//          val starbucksListResponse = starbucksRepository.fetchNearbyStarbucks(
//              latitude,longitude)
//          starbucksListResponse.collect{
//              when(it){
//                      is Resource.Success ->{
//                          starbucksList.value = it.value
//                          isLoading.value = false
//                      }
//                      is Resource.Failure ->{
//                          loadError.value = it.errorMsg.toString()
//                          isLoading.value = false
//                      }
//                      is Resource.Loading ->{
////                          isLoading.value = true
//                      }
//                  }
//          }
//        }
//    }
}