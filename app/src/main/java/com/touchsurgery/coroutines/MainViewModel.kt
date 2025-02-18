package com.touchsurgery.coroutines

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

class MainViewModel : ViewModel() {

    /* In this case, requests are called in a sequential manner, when the first one finishes, then the next
    * one is triggered and so on */
    fun loadDataSequentially() {
        viewModelScope.launch {
            Log.d("CoroutinesTag", "Start first call")
            delay(1000)
            Log.d("CoroutinesTag", "Response first call")

            Log.d("CoroutinesTag", "Start second call")
            delay(2000)
            Log.d("CoroutinesTag", "Response second call")
        }
    }

    /* In this case instead of waiting for the first request to finish
    * we launch both at the same time and as soon as each one finishes, they print their result */
    fun loadDataInParallel() {
        viewModelScope.launch {
            Log.d("CoroutinesTag", "Start first call")
            delay(1000)
            Log.d("CoroutinesTag", "Response first call")
        }

        viewModelScope.launch {
            Log.d("CoroutinesTag", "Start second call")
            delay(2000)
            Log.d("CoroutinesTag", "Response second call")
        }
    }

    /* In this case, we launch all of them in the same coroutine and as we don't know which one is going to take
    * more time, we wait until the last of them finished and then we can do whatever we want with the result */
    fun loadDataAndWait() {
        viewModelScope.launch {
            val firstRequest = async {
                val randomDelay = 1000 * Random.nextLong(1, 4)
                Log.d("CoroutinesTag", "Start first call")
                delay(randomDelay)
                Log.d("CoroutinesTag", "Response first call")

                randomDelay
            }

            val secondRequest = async {
                val randomDelay = 1000 * Random.nextLong(1, 4)
                Log.d("CoroutinesTag", "Start second call")
                delay(randomDelay)
                Log.d("CoroutinesTag", "Response second call")

                randomDelay
            }

            val thirdRequest = async {
                val randomDelay = 1000 * Random.nextLong(1, 4)
                Log.d("CoroutinesTag", "Start third call")
                delay(randomDelay)
                Log.d("CoroutinesTag", "Response third call")

                randomDelay
            }

            val requests = listOf(firstRequest, secondRequest, thirdRequest)
            val results = requests.awaitAll()

            Log.d("CoroutinesTag", "All responses have been received")

            Log.d("CoroutinesTag", "First delay: ${results.first()}")
            Log.d("CoroutinesTag", "Second delay: ${results[1]}")
            Log.d("CoroutinesTag", "Third delay: ${results.last()}")
        }
    }

    /* In this case, instead of waiting for all to finish, we just request the result
    * when we needed and we block the coroutine until we get the result with .await() */
    fun loadDataAndAsync() {
        viewModelScope.launch {
            val firstRequest = async {
                val randomDelay = 1000 * Random.nextLong(1, 4)
                Log.d("CoroutinesTag", "Start first call")
                delay(randomDelay)
                Log.d("CoroutinesTag", "Response first call")

                randomDelay
            }

            val secondRequest = async {
                val randomDelay = 1000 * Random.nextLong(1, 4)
                Log.d("CoroutinesTag", "Start second call")
                delay(randomDelay)
                Log.d("CoroutinesTag", "Response second call")

                randomDelay
            }

            val thirdRequest = async {
                val randomDelay = 1000 * Random.nextLong(1, 4)
                Log.d("CoroutinesTag", "Start third call")
                delay(randomDelay)
                Log.d("CoroutinesTag", "Response third call")

                randomDelay
            }

            Log.d("CoroutinesTag", "All responses have been prepared")

            Log.d("CoroutinesTag", "First delay: ${firstRequest.await()}")
            Log.d("CoroutinesTag", "Second delay: ${secondRequest.await()}")
            Log.d("CoroutinesTag", "Third delay: ${thirdRequest.await()}")
        }
    }

    /* This function loads some data on a different thread and then gets back to the original one
    * to show the results (in the logger in this case) */
    fun loadDataOnADifferentThread() {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                Log.d("CoroutinesTag", "Start request in thread ${Thread.currentThread().name}")
                // This will happen in the IO background thread
                someRequest()
            }

            Log.d("CoroutinesTag", "Response in thread ${Thread.currentThread().name}: $result")
        }
    }

    /* This function loads some data on a different thread and then gets back to the original one
    * to show the results (in the logger in this case) with a different approach */
    fun loadDataOnADifferentThreadOption2() {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("CoroutinesTag", "Start request in thread ${Thread.currentThread().name}")
            val result = someRequest()

            withContext(Dispatchers.Main) {
                Log.d("CoroutinesTag", "Response in thread ${Thread.currentThread().name}: $result")
            }
        }
    }

    private suspend fun someRequest(): String {
        delay(2000)
        // throw Exception("This is an exception")
        return "I'm a result"
    }

    /* This is an example of using runCatching to get a Kotlin.Result object response instead of the standard callbacks */
    fun loadDataWithResult() {
        viewModelScope.launch {
            val result = runCatching {
                someRequest()
            }

            result.onSuccess {
                Log.d("CoroutinesTag", "Success: $it")
            }

            result.onFailure {
                Log.d("CoroutinesTag", "Failure: ${it.message}")
            }

            val alternative = result.getOrElse { "This is an alternative" }
            Log.d("CoroutinesTag", "Alternative: $alternative")
        }
    }

    override fun onCleared() {
        /* We can clear whatever is not attached to the viewModelScope here
        * so that we don't get memory leaks */

        super.onCleared()
    }
}
