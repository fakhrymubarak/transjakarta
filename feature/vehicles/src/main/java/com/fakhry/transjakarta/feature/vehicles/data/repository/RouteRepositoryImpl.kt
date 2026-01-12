package com.fakhry.transjakarta.feature.vehicles.data.repository

import com.fakhry.transjakarta.core.domain.DomainResult
import com.fakhry.transjakarta.core.networking.util.mapNetworkCall
import com.fakhry.transjakarta.feature.vehicles.data.mapper.toRoute
import com.fakhry.transjakarta.feature.vehicles.data.mapper.toRoutes
import com.fakhry.transjakarta.feature.vehicles.data.remote.service.RouteMbtaApiService
import com.fakhry.transjakarta.feature.vehicles.domain.model.Route
import com.fakhry.transjakarta.feature.vehicles.domain.repository.RouteRepository
import com.fakhry.transjakarta.utils.coroutines.DispatcherProvider
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

class RouteRepositoryImpl @Inject constructor(
    private val api: RouteMbtaApiService,
    private val dispatchers: DispatcherProvider,
) : RouteRepository {
    private var cachedRoutes: List<Route>? = null

    override suspend fun getRoutes(): DomainResult<List<Route>> = withContext(dispatchers.io) {
        cachedRoutes?.let { return@withContext DomainResult.Success(it) }

        runCatching {
            val routes = mutableListOf<Route>()
            var offset = 0
            while (true) {
                val page = api.getRoutes(
                    offset = offset,
                    limit = PAGE_SIZE,
                ).data.toRoutes()
                routes.addAll(page)
                if (page.size < PAGE_SIZE) break
                offset += PAGE_SIZE
            }

            val sortedRoutes =
                routes.sortedBy { route -> route.shortName.ifBlank { route.longName } }
            cachedRoutes = sortedRoutes
            DomainResult.Success(sortedRoutes)
        }.getOrElse { throwable ->
            when (throwable) {
                is IOException -> DomainResult.Error(
                    message = "Network error",
                    cause = throwable,
                    isNetworkError = true,
                )

                else -> DomainResult.Error(
                    message = throwable.message ?: "Unexpected error",
                    cause = throwable,
                )
            }
        }
    }

    override suspend fun getRouteById(id: String): DomainResult<Route> = withContext(dispatchers.io) {
        cachedRoutes?.firstOrNull { it.id == id }?.let { return@withContext DomainResult.Success(it) }

        mapNetworkCall { api.getRoute(id).data.toRoute() }
    }

    companion object {
        private const val PAGE_SIZE = 100
    }
}
