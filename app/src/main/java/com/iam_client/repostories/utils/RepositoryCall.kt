package com.iam_client.repostories.utils

import androidx.lifecycle.*
import com.iam_client.utills.reactive.observeOnce
import kotlinx.coroutines.*
import org.jetbrains.anko.custom.async
import androidx.lifecycle.Observer as Observer


class RepositoryCall<T>(

    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val f: suspend (RepositoryCall<T>) -> T
) : LaunchedRepositoryCall<T>() {



    var isForcedRefresh: Boolean = false
        private set(value) {
            field = value
        }

    fun forceRefresh(): RepositoryCall<T> {
        isForcedRefresh = true; return this
    }

    private var lifeCycleOwner: LifecycleOwner? = null
    fun setLifeCycleOwner(lifecycleOwner: LifecycleOwner): RepositoryCall<T> {
        lifeCycleOwner = lifecycleOwner
        return this
    }


    private var _onSuccess: Observer<T>? = null
    fun onSuccess(onSuccess: (T) -> Unit): RepositoryCall<T> {
        _onSuccess = Observer(onSuccess)
        return this
    }

    private var _onFailure: Observer<Throwable>? = null
    fun onFailure(onFailure: (Throwable) -> Unit): RepositoryCall<T> {
        _onFailure = Observer(onFailure)
        return this
    }

    private var _onFinish: Observer<Unit>? = null
    fun onFinish(onFinish: (Unit) -> Unit): RepositoryCall<T> {
        _onFinish = Observer(onFinish)
        return this
    }


    private lateinit var _cancel : MutableLiveData<Boolean>
    override fun cancel() = _cancel.postValue(true)

    fun launch(scope: CoroutineScope): LaunchedRepositoryCall<T> {
        //if lifecycle owner is destroyed don't execute the call
        if (lifeCycleOwner?.lifecycle?.currentState != null
            && lifeCycleOwner?.lifecycle?.currentState == Lifecycle.State.DESTROYED
        ) {
            postStatus(Status.Canceled)
            return this
        }
        postStatus(Status.Loading)

        //observe call with or without lifecycle owner
        val successObserver = MutableLiveData<T>()
        val errorObserver = MutableLiveData<Throwable>()
        val finallyObserver = MutableLiveData<Unit>()

        val obsList = listOf<Pair<Observer<*>?,LiveData<*>>>(
            Pair(_onSuccess,successObserver),
            Pair(_onFailure,errorObserver),
            Pair(_onFinish,finallyObserver))

        obsList.filter { it.first!=null }.forEach {
            when(lifeCycleOwner){
                null-> it.second.observeForever(it.first as Observer<Any?>)
                else-> it.second.observe(lifeCycleOwner!!,it.first as Observer<Any?>)
            }
        }
        //cancel observer if cancel is called
        _cancel = MutableLiveData()
        _cancel.observeOnce(Observer{ cancel ->
            if (cancel) {
                obsList.filter { it.first != null }.forEach {
                    it.second.removeObserver(it.first as Observer<Any?>)
                }
                postStatus(Status.Canceled)
            }
        })

        //launch call on coroutine
        scope.launch(dispatcher) {
                try {
                    val result = f(this@RepositoryCall)
                    successObserver.postValue(result)
                } catch (throwable: Throwable) {
                    errorObserver.postValue(throwable)
                }
            }.invokeOnCompletion {
                postStatus(Status.Finished)
                finallyObserver.postValue(Unit)
            }
        return this
    }

    override suspend fun awaitAndGet(): T {
        return withContext(dispatcher){ f(this@RepositoryCall) }
    }
}

abstract class LaunchedRepositoryCall<T> {
    val status: LiveData<Status> = MutableLiveData()
    protected fun postStatus(status: Status) {
        (this.status as MutableLiveData).postValue(status)
    }

    init {
        postStatus(Status.Init)
    }

    abstract suspend fun awaitAndGet() : T

    abstract fun cancel()

    enum class Status { Init, Loading, Finished, Canceled }
}

