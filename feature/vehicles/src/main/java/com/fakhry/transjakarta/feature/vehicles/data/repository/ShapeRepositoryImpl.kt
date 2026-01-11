package com.fakhry.transjakarta.feature.vehicles.data.repository

import com.fakhry.transjakarta.core.domain.DomainResult
import com.fakhry.transjakarta.core.networking.util.mapNetworkCall
import com.fakhry.transjakarta.feature.vehicles.data.mapper.toShape
import com.fakhry.transjakarta.feature.vehicles.data.remote.service.ShapeMbtaApiService
import com.fakhry.transjakarta.feature.vehicles.domain.model.Shape
import com.fakhry.transjakarta.feature.vehicles.domain.repository.ShapeRepository
import javax.inject.Inject

class ShapeRepositoryImpl @Inject constructor(
    private val api: ShapeMbtaApiService,
) : ShapeRepository {
    override suspend fun getShapeById(id: String): DomainResult<Shape> =
        mapNetworkCall { api.getShape(id).data.toShape() }
}
