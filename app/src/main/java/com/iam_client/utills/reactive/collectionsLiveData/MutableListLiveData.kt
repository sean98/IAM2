package com.iam_client.utills.reactive.collectionsLiveData

import androidx.lifecycle.MutableLiveData
import com.iam_client.utills.reactive.Event


class MutableListLiveData<T> : MutableLiveData<Event<ObservableCollectionsAction<T>>>() {
    private val list = mutableListOf<T>()
    fun getList() :List<T> = list

    fun add(element: T): Boolean {
        val bool = list.add(element)
        val action = ObservableCollectionsAction<T>(ObservableCollectionsAction.Type.AddElement)
        action.apply {
            this.element = element
            this.index = list.indexOf(element)
            resultBoolean = bool
        }
        postValue(Event(action))
        return bool
    }

    fun add(index: Int, element: T) {
        list.add(index, element)
        val action = ObservableCollectionsAction<T>(ObservableCollectionsAction.Type.Add)
        action.apply {
            this.element = element
            this.index = index
        }
        postValue(Event(action))
    }

    fun addAll(index: Int, elements: Collection<T>): Boolean {
        val bool = list.addAll(index, elements)
        val action = ObservableCollectionsAction<T>(ObservableCollectionsAction.Type.AddAll)
        action.apply {
            this.elements = elements
            this.index = index
            resultBoolean = bool
        }
        postValue(Event(action))
        return bool
    }

    fun addAll(elements: Collection<T>): Boolean {
        val bool = list.addAll(elements)
        val action = ObservableCollectionsAction<T>(ObservableCollectionsAction.Type.AddAll)
        action.apply {
            this.elements = elements
            resultBoolean = bool
        }
        postValue(Event(action))
        return bool
    }

//    fun set(elements: Collection<T>): Boolean {
//        val bool = list.addAll(elements)
//        val action = ObservableCollectionsAction<T>(ObservableCollectionsAction.Type.AddAll)
//        action.apply {
//            this.elements = elements
//            resultBoolean = bool
//        }
//        postValue(Event(action))
//        return bool
//    }

    fun clear() {
        list.clear()
        val action = ObservableCollectionsAction<T>(ObservableCollectionsAction.Type.Clear)
        postValue(Event(action))
    }


    fun removeAll(elements: Collection<T>): Boolean {
        val bool = list.removeAll(elements)
        val action = ObservableCollectionsAction<T>(ObservableCollectionsAction.Type.RemoveAll)
        action.apply {
            this.elements = elements
            resultBoolean = bool
        }
        postValue(Event(action))
        return bool
    }

    fun removeAt(index: Int): T {
        val resultE = list.removeAt(index)
        val action = ObservableCollectionsAction<T>(ObservableCollectionsAction.Type.RemoveAt)
        action.apply {
            this.elements = elements
            this.index = index
            resultElement = resultE
        }
        postValue(Event(action))
        return resultE
    }

    fun remove(element: T): Boolean{
        val pos = list.indexOf(element)
        val bool = list.remove(element)
        val action = ObservableCollectionsAction<T>(ObservableCollectionsAction.Type.Remove)
        action.apply {
            this.element = element
            this.index = pos
            resultBoolean = bool
        }
        postValue(Event(action))
        return bool
    }

    fun retainAll(elements: Collection<T>): Boolean {
        val bool = list.retainAll(elements)
        val action = ObservableCollectionsAction<T>(ObservableCollectionsAction.Type.RetainAll)
        action.apply {
            this.elements = elements
            resultBoolean = bool
        }
        postValue(Event(action))
        return bool
    }

    fun set(index: Int, element: T): T {
        val resultE = list.set(index, element)
        val action = ObservableCollectionsAction<T>(ObservableCollectionsAction.Type.Set)
        action.apply {
            this.element = element
            this.index = index
            resultElement = resultE
        }
        postValue(Event(action))
        return resultE
    }


}