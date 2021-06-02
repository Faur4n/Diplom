package com.fauran.diplom.main.home.recommendations

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.algolia.instantsearch.core.searchbox.SearchBoxView
import com.algolia.instantsearch.helper.searcher.SearcherSingleIndex
import com.algolia.search.client.ClientSearch
import com.algolia.search.helper.deserialize
import com.algolia.search.model.APIKey
import com.algolia.search.model.ApplicationID
import com.algolia.search.model.IndexName
import com.algolia.search.model.search.Query
import com.fauran.diplom.BuildConfig
import com.fauran.diplom.TAG
import com.fauran.diplom.main.home.recommendations.models.Intersection
import com.fauran.diplom.main.home.recommendations.models.RecData
import com.fauran.diplom.main.home.recommendations.models.RecommendationUser
import com.fauran.diplom.models.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import io.ktor.client.features.logging.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class RecommendationViewModelFactory(
    val user: User
) : ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(User::class.java).newInstance(user)
    }

}

class RecommendationViewModel(
    val user: User
) : ViewModel() {

    private val store = Firebase.firestore

    private val client = ClientSearch(
        ApplicationID("V6M81ZHM66"),
        APIKey(BuildConfig.ANGOLIA_SEARCH_API_KEY),
        LogLevel.ALL
    )

    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> = _users

    private val index = client.initIndex(IndexName("finder"))
    private val searcher = SearcherSingleIndex(
        index, query = Query(
            aroundLatLngViaIP = true
        )
    )
    val hitsPager = SearcherSingleIndexPager(
        searcher = searcher,
        transformer = { response ->
            response.hits.deserialize(RecData.serializer()).mapNotNull {
                val newUser = getUser(it.id) ?: return@mapNotNull null
                val intersections = RecUtils.findIntersections(newUser,user)
                RecommendationUser(
                    newUser,
                    intersections
                )
            }
        }
    )

    init {
        viewModelScope.launch {
            searcher.setQuery("rock")
//            delay(5000)
//            searcher.setQuery("кирилл")
//            hitsPager.notifySearcherChanged()
        }
    }

    fun loadUsers(items: List<RecData>) {
        viewModelScope.launch {
            val users = items.mapNotNull {
                getUser(it.id)
            }
            _users.postValue(users)
        }
    }

    suspend fun getUser(id: String): User? {
        Log.d(TAG, "getUser: USER WITH ID $id")
        val user = store.document("users/$id").get().await().toObject<User>()
        Log.d(TAG, "getUser: $user")
        return user
    }

    fun getCategories(user: User) {
        viewModelScope.launch {
            val cities = mutableSetOf<String>()
            user.friends?.forEach {
                val city = it.city
                if (city != null) {
                    cities.add(city)
                }
            }
            Log.d(TAG, "getCategories: $cities")
        }
    }

//    suspend fun getUser(id: String){
//        store.document("users/$id").get()
//            .addOnSuccessListener {
//                val user = it.toObject<User>()
//            }
//    }

    override fun onCleared() {
        super.onCleared()
        searcher.cancel()
    }
}

