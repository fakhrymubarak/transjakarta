package com.fakhry.transjakarta.feature.vehicles.domain.repository

import com.fakhry.transjakarta.core.domain.DomainResult
import com.fakhry.transjakarta.feature.vehicles.domain.model.Stop

interface StopRepository {
    suspend fun getStop(id: String): DomainResult<Stop>
}
