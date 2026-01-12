package com.fakhry.transjakarta.feature.vehicles.data.repository

import com.fakhry.transjakarta.core.domain.DomainResult
import com.fakhry.transjakarta.core.networking.util.mapNetworkCall
import com.fakhry.transjakarta.feature.vehicles.data.mapper.toStop
import com.fakhry.transjakarta.feature.vehicles.data.remote.service.StopMbtaApiService
import com.fakhry.transjakarta.feature.vehicles.domain.model.Stop
import com.fakhry.transjakarta.feature.vehicles.domain.repository.StopRepository
import com.fakhry.transjakarta.utils.coroutines.DispatcherProvider
import kotlinx.coroutines.withContext
import javax.inject.Inject

class StopRepositoryImpl @Inject constructor(
    private val api: StopMbtaApiService,
    private val dispatchers: DispatcherProvider,
) : StopRepository {
    override suspend fun getStop(id: String): DomainResult<Stop> = withContext(dispatchers.io) {
        mapNetworkCall {
            api.getStop(id).data.toStop()
        }
    }
}
