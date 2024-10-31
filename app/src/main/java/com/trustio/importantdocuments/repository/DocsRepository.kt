package com.trustio.importantdocuments.repository

import com.trustio.importantdocuments.data.remote.request.CollectionRequest
import com.trustio.importantdocuments.data.remote.response.CollectionAddResponse
import com.trustio.importantdocuments.utils.ResultApp
import kotlinx.coroutines.flow.Flow

interface DocsRepository {
   suspend fun addCollection(collectionRequest: CollectionRequest): Flow<Result<CollectionAddResponse>>
}