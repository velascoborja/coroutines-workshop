package com.touchsurgery.coroutines

import android.util.Log
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class MainPresenter {

    private val compositeDisposable = CompositeDisposable()

    /* In this case, requests are called sequentially. When the first one finishes,
     * the next one is triggered, and so on. */
    fun loadDataSequentially() {
        val disposable = Single.timer(1, TimeUnit.SECONDS)
            .doOnSubscribe { Log.d("RxJavaTag", "Start first call") }
            .doOnSuccess { Log.d("RxJavaTag", "Response first call") }
            .flatMap {
                Single.timer(2, TimeUnit.SECONDS)
                    .doOnSubscribe { Log.d("RxJavaTag", "Start second call") }
                    .doOnSuccess { Log.d("RxJavaTag", "Response second call") }
            }
            .subscribe()

        compositeDisposable.add(disposable)
    }

    /* In this case, instead of waiting for the first request to finish,
     * both are launched at the same time, and as soon as each one finishes,
     * it prints its result. */
    fun loadDataInParallel() {
        val disposable1 = Single.timer(1, TimeUnit.SECONDS)
            .doOnSubscribe { Log.d("RxJavaTag", "Start first call") }
            .doOnSuccess { Log.d("RxJavaTag", "Response first call") }
            .subscribe()

        val disposable2 = Single.timer(2, TimeUnit.SECONDS)
            .doOnSubscribe { Log.d("RxJavaTag", "Start second call") }
            .doOnSuccess { Log.d("RxJavaTag", "Response second call") }
            .subscribe()

        compositeDisposable.addAll(disposable1, disposable2)
    }

    /* In this case, we launch all requests within the same function,
     * and since we don't know which one will take longer,
     * we wait until the last one finishes and then process the results. */
    fun loadDataAndWait() {
        val firstRequest = Single.fromCallable {
            val delay = Random.nextLong(1, 4)
            Log.d("RxJavaTag", "Start first call")
            TimeUnit.SECONDS.sleep(delay)
            Log.d("RxJavaTag", "Response first call")
            delay
        }.subscribeOn(Schedulers.io())

        val secondRequest = Single.fromCallable {
            val delay = Random.nextLong(1, 4)
            Log.d("RxJavaTag", "Start second call")
            TimeUnit.SECONDS.sleep(delay)
            Log.d("RxJavaTag", "Response second call")
            delay
        }.subscribeOn(Schedulers.io())

        val thirdRequest = Single.fromCallable {
            val delay = Random.nextLong(1, 4)
            Log.d("RxJavaTag", "Start third call")
            TimeUnit.SECONDS.sleep(delay)
            Log.d("RxJavaTag", "Response third call")
            delay
        }.subscribeOn(Schedulers.io())

        val disposable = Single.zip(firstRequest, secondRequest, thirdRequest) { first, second, third ->
            Log.d("RxJavaTag", "All responses have been received")
            Log.d("RxJavaTag", "First delay: $first")
            Log.d("RxJavaTag", "Second delay: $second")
            Log.d("RxJavaTag", "Third delay: $third")
        }.subscribe()

        compositeDisposable.add(disposable)
    }

    /* Clears all active subscriptions to prevent memory leaks */
    fun dispose() {
        compositeDisposable.clear()
    }
}
