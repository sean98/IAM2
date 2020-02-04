package com.iam_client.repostories.documents

import com.iam_client.repostories.data.docs.Quotation

interface IQuotationRepository {

    suspend fun cancelDoc(quotation: Quotation)

}