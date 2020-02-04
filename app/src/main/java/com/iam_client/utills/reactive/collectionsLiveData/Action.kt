package com.iam_client.utills.reactive.collectionsLiveData

data class ObservableCollectionsAction<T>(
    val type: Type
) {
    var element: T? = null
    var elements: Collection<T>? = null
    var index: Int? = null
    var resultElement: T? = null
    var resultBoolean: Boolean? = null
    var resultInt: Int? = null

    enum class Type {
        Add, AddAll, AddElement, AddFirst, AddLast,
        Clear,
        Remove,RemoveAll, RemoveAt, RetainAll,
        Set,
    }
}