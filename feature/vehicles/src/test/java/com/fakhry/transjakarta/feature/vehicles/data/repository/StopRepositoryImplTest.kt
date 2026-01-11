package com.fakhry.transjakarta.feature.vehicles.data.repository

import com.fakhry.transjakarta.core.domain.DomainResult
import com.fakhry.transjakarta.feature.vehicles.data.remote.response.StopAttributesDto
import com.fakhry.transjakarta.feature.vehicles.data.remote.response.StopDataDto
import com.fakhry.transjakarta.feature.vehicles.data.remote.response.StopResponse
import com.fakhry.transjakarta.feature.vehicles.data.remote.service.StopMbtaApiService
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class StopRepositoryImplTest {

    @Test
    fun `getStop returns mapped stop on success`() = runTest {
        val fakeApi = object : StopMbtaApiService {
            override suspend fun getStop(id: String, fields: String): StopResponse {
                return StopResponse(
                    data = StopDataDto(
                        id = id,
                        attributes = StopAttributesDto(
                            name = "Stop Name",
                            latitude = 1.0,
                            longitude = 2.0,
                            municipality = "City",
                            platformCode = "PC",
                        )
                    )
                )
            }
        }
        val repository = StopRepositoryImpl(fakeApi)

        val result = repository.getStop("s1")

        assertTrue(result is DomainResult.Success)
        val stop = (result as DomainResult.Success).data
        assertEquals("s1", stop.id)
        assertEquals("Stop Name", stop.name)
        assertEquals(1.0, stop.latitude)
        assertEquals(2.0, stop.longitude)
        assertEquals("City", stop.municipality)
        assertEquals("PC", stop.platformCode)
    }

    @Test
    fun `getStop returns error on exception`() = runTest {
        val fakeApi = object : StopMbtaApiService {
            override suspend fun getStop(id: String, fields: String): StopResponse {
                throw RuntimeException("API Error")
            }
        }
        val repository = StopRepositoryImpl(fakeApi)

        val result = repository.getStop("s1")

        assertTrue(result is DomainResult.Error)
        assertEquals("API Error", (result as DomainResult.Error).message)
    }
}
