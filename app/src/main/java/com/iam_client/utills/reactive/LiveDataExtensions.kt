package com.iam_client.utills.reactive

import androidx.databinding.Observable
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import java.util.concurrent.atomic.AtomicBoolean

fun <T> LiveData<T>.observeOnce(observer: Observer<T>) {
    observeForever(object : Observer<T> {
        override fun onChanged(t: T?) {
            observer.onChanged(t)
            removeObserver(this)
        }
    })
}

inline fun <T> LiveData<Event<T>>.observeEvent(owner: LifecycleOwner, crossinline onEventUnhandledContent: (T) -> Unit) {
    observe(owner, Observer {
        val container = it?.getContentIfNotHandled()
        if (container != null)
            container.get().let(onEventUnhandledContent)
    })
}

inline fun <T> LiveData<T>.observe(owner: LifecycleOwner, crossinline onChanged: (T) -> Unit) {
    observe(owner, Observer { onChanged(it) })
}

inline fun <T,R> LiveData<EventWithReturn<T,R>>.observeEventWithResult(owner: LifecycleOwner, crossinline onEventUnhandledContent: (T) -> LiveData<R>) {
    observe(owner, Observer {
        val container = it?.getContentIfNotHandled()
        if (container != null){
            val x = container.get().let(onEventUnhandledContent)
            x.observeForever(it)
        }
    })
}


inline fun <reified T: Observable> T.addOnPropertyChanged(crossinline callback: (T) -> Unit): Disposable =
    object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(observable: Observable?, i: Int) =
            callback(observable as T)
    }.also { addOnPropertyChangedCallback(it) }.let {
        Disposables.fromAction {removeOnPropertyChangedCallback(it)}
    }
/**
 * Used as a wrapper for data that is exposed via a LiveData that represents an event.
 */
open class Event<out T>(private val content: T) {
    /**
     * the container allows the generic T to be nullable
     */
    private val container: Container<T>
    private val hasBeenHandled =AtomicBoolean(false)

    init {
        container =  Container(content)
    }
    val isHandled : Boolean
        get()= hasBeenHandled.get()

    /**
     * Returns the content and prevents its use again.
     */
    fun getContentIfNotHandled(): Container<T>? {
        return if (hasBeenHandled.compareAndSet(false,true)) {
            container
        } else { null }
    }

    /**
     * Returns the content, even if it's already been handled.
     */
    fun peekContent(): T = content

    class Container<out T>(val content : T){
        fun get() = content
    }
}
open class EventWithReturn<out T,R>(content: T, private val onResult: (R) -> Unit )
    : Event<T>(content) , Observer<R>{
    override fun onChanged(t: R) {
        onResult(t)
    }
}

