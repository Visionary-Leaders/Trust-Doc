package com.trustio.importantdocuments.repository.imp

import com.trustio.importantdocuments.data.remote.api.DocApi
import com.trustio.importantdocuments.data.remote.request.CollectionRequest
import com.trustio.importantdocuments.data.remote.response.CollectionAddResponse
import com.trustio.importantdocuments.repository.DocsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class DocsRepositoryImp @Inject constructor(private val docApi: DocApi) : DocsRepository {
    override suspend fun addCollection(collectionRequest: CollectionRequest) =
        flow {
            val response = docApi.addCollection(collectionRequest)
            if (response.isSuccessful) emit(Result.success(response.body()!!))
            else emit(Result.failure(Exception(response.errorBody()!!.string())))
        }.flowOn(Dispatchers.IO)
}