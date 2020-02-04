package com.iam_client.viewModels.main.documents

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import com.iam_client.repostories.data.docs.Document
import com.iam_client.utills.reactive.Event
import kotlin.reflect.KClass

interface IDocumentListViewModel {
    val type: KClass<out Document>
    val refreshEvent: LiveData<Event<Boolean>>
    val errorMessage: LiveData<Event<Throwable>>
    fun getDocumentList(): LiveData<PagedList<Document>>
    fun refresh()
}