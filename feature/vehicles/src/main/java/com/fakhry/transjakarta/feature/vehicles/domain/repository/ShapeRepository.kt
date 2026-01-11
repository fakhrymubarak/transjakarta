package com.fakhry.transjakarta.feature.vehicles.domain.repository

import com.fakhry.transjakarta.core.domain.DomainResult
import com.fakhry.transjakarta.feature.vehicles.domain.model.Shape

interface ShapeRepository {
    suspend fun getShapeById(id: String): DomainResult<Shape>
}
