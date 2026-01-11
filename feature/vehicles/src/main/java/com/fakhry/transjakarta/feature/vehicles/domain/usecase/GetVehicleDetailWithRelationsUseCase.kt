package com.fakhry.transjakarta.feature.vehicles.domain.usecase

import androidx.annotation.VisibleForTesting
import com.fakhry.transjakarta.core.domain.DomainResult
import com.fakhry.transjakarta.feature.vehicles.domain.model.Route
import com.fakhry.transjakarta.feature.vehicles.domain.model.Shape
import com.fakhry.transjakarta.feature.vehicles.domain.model.Stop
import com.fakhry.transjakarta.feature.vehicles.domain.model.Trip
import com.fakhry.transjakarta.feature.vehicles.domain.model.VehicleDetailWithRelations
import com.fakhry.transjakarta.feature.vehicles.domain.repository.RouteRepository
import com.fakhry.transjakarta.feature.vehicles.domain.repository.ShapeRepository
import com.fakhry.transjakarta.feature.vehicles.domain.repository.StopRepository
import com.fakhry.transjakarta.feature.vehicles.domain.repository.TripRepository
import com.fakhry.transjakarta.feature.vehicles.domain.repository.VehicleRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class GetVehicleDetailWithRelationsUseCase @Inject constructor(
    private val vehicleRepository: VehicleRepository,
    private val routeRepository: RouteRepository,
    private val tripRepository: TripRepository,
    private val stopRepository: StopRepository,
    private val shapeRepository: ShapeRepository,
) {
    private var ioContext: CoroutineContext = Dispatchers.IO

    @VisibleForTesting
    internal constructor(
        vehicleRepository: VehicleRepository,
        routeRepository: RouteRepository,
        tripRepository: TripRepository,
        stopRepository: StopRepository,
        shapeRepository: ShapeRepository,
        ioContext: CoroutineContext,
    ) : this(
        vehicleRepository,
        routeRepository,
        tripRepository,
        stopRepository,
        shapeRepository,
    ) {
        this.ioContext = ioContext
    }

    suspend operator fun invoke(vehicleId: String): DomainResult<VehicleDetailWithRelations> =
        withContext(ioContext) {
            when (val vehicleResult = vehicleRepository.getVehicleDetail(vehicleId)) {
                is DomainResult.Error -> DomainResult.Error(
                    message = vehicleResult.message,
                    code = vehicleResult.code,
                    data = null,
                    cause = vehicleResult.cause,
                    isNetworkError = vehicleResult.isNetworkError,
                )
                DomainResult.Empty -> DomainResult.Empty
                is DomainResult.Success -> {
                    val vehicle = vehicleResult.data
                    val routeId = vehicle.routeId
                    val tripId = vehicle.tripId
                    val stopId = vehicle.stopId

                    coroutineScope {
                        val routeDeferred = async { routeId?.let { safeRouteDetail(it) } }
                        val tripDeferred = async { tripId?.let { safeTripDetail(it) } }
                        val stopDeferred = async { stopId?.let { safeStopDetail(it) } }

                        val trip = tripDeferred.await()
                        val shapeDeferred = async { trip?.shapeId?.let { safeShapeDetail(it) } }

                        DomainResult.Success(
                            VehicleDetailWithRelations(
                                vehicle = vehicle,
                                route = routeDeferred.await(),
                                trip = trip,
                                stop = stopDeferred.await(),
                                shape = shapeDeferred.await(),
                            ),
                        )
                    }
                }
            }
        }

    private suspend fun safeRouteDetail(id: String): Route? =
        when (val result: DomainResult<Route> = routeRepository.getRouteById(id)) {
            is DomainResult.Success -> result.data
            else -> null
        }

    private suspend fun safeTripDetail(id: String): Trip? =
        when (val result: DomainResult<Trip> = tripRepository.getTripById(id)) {
            is DomainResult.Success -> result.data
            else -> null
        }

    private suspend fun safeStopDetail(id: String): Stop? =
        when (val result: DomainResult<Stop> = stopRepository.getStop(id)) {
            is DomainResult.Success -> result.data
            else -> null
        }

    private suspend fun safeShapeDetail(id: String): Shape? =
        when (val result: DomainResult<Shape> = shapeRepository.getShapeById(id)) {
            is DomainResult.Success -> result.data
            else -> null
        }
}
