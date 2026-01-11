package com.fakhry.transjakarta.feature.vehicles.domain.repository

import com.fakhry.transjakarta.core.domain.DomainResult
import com.fakhry.transjakarta.feature.vehicles.domain.model.Route

interface RouteRepository {
    suspend fun getRoutes(): DomainResult<List<Route>>
    suspend fun getRouteById(id: String): DomainResult<Route>
}
