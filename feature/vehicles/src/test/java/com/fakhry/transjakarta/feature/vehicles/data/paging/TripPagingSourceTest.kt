package com.fakhry.transjakarta.feature.vehicles.data.paging

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.fakhry.transjakarta.feature.vehicles.data.remote.response.TripAttributesDto
import com.fakhry.transjakarta.feature.vehicles.data.remote.response.TripDataDto
import com.fakhry.transjakarta.feature.vehicles.data.remote.response.TripsResponse
import com.fakhry.transjakarta.feature.vehicles.data.remote.service.TripMbtaApiService
import com.fakhry.transjakarta.feature.vehicles.domain.model.Trip
import com.fakhry.transjakarta.feature.vehicles.domain.model.TripFilters
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.IOException

class TripPagingSourceTest {
    private lateinit var fakeApiService: FakeTripMbtaApiService

    @BeforeEach
    fun setup() {
        fakeApiService = FakeTripMbtaApiService()
    }

    @Test
    fun `load returns first page successfully`() = runTest {
        val trips = createTestTrips(TripPagingSource.PAGE_SIZE)
        fakeApiService.setResponse(TripsResponse(data = trips))

        val pagingSource = TripPagingSource(fakeApiService, TripFilters())

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = TripPagingSource.PAGE_SIZE,
                placeholdersEnabled = false,
            ),
        )

        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        assertEquals(TripPagingSource.PAGE_SIZE, page.data.size)
        assertNull(page.prevKey)
        assertEquals(TripPagingSource.PAGE_SIZE, page.nextKey)
    }

    @Test
    fun `load returns last page with null nextKey when less than page size`() = runTest {
        val trips = createTestTrips(3)
        fakeApiService.setResponse(TripsResponse(data = trips))

        val pagingSource = TripPagingSource(fakeApiService, TripFilters())

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = TripPagingSource.PAGE_SIZE,
                placeholdersEnabled = false,
            ),
        )

        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        assertEquals(3, page.data.size)
        assertNull(page.nextKey)
    }

    @Test
    fun `load returns error on network failure`() = runTest {
        fakeApiService.setError(IOException("Network error"))

        val pagingSource = TripPagingSource(fakeApiService, TripFilters())

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = TripPagingSource.PAGE_SIZE,
                placeholdersEnabled = false,
            ),
        )

        assertTrue(result is PagingSource.LoadResult.Error)
        val error = result as PagingSource.LoadResult.Error
        assertTrue(error.throwable is IOException)
    }

    @Test
    fun `load with offset returns correct prevKey`() = runTest {
        val trips = createTestTrips(TripPagingSource.PAGE_SIZE)
        fakeApiService.setResponse(TripsResponse(data = trips))

        val pagingSource = TripPagingSource(fakeApiService, TripFilters())

        val result = pagingSource.load(
            PagingSource.LoadParams.Append(
                key = TripPagingSource.PAGE_SIZE,
                loadSize = TripPagingSource.PAGE_SIZE,
                placeholdersEnabled = false,
            ),
        )

        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        assertEquals(0, page.prevKey)
        assertEquals(TripPagingSource.PAGE_SIZE * 2, page.nextKey)
    }

    @Test
    fun `trips are correctly mapped from DTOs`() = runTest {
        val trips =
            listOf(
                TripDataDto(
                    id = "trip-1",
                    attributes = TripAttributesDto(
                        name = "Trip 42",
                        headsign = "Downtown",
                    ),
                ),
            )
        fakeApiService.setResponse(TripsResponse(data = trips))

        val pagingSource = TripPagingSource(fakeApiService, TripFilters())

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = TripPagingSource.PAGE_SIZE,
                placeholdersEnabled = false,
            ),
        )

        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        assertEquals(1, page.data.size)
        val trip = page.data.first()
        assertEquals("trip-1", trip.id)
        assertEquals("Trip 42", trip.name)
        assertEquals("Downtown", trip.headsign)
    }

    @Test
    fun `load passes filters to api`() = runTest {
        val trips = createTestTrips(1)
        fakeApiService.setResponse(TripsResponse(data = trips))
        val filters = TripFilters(
            routeIds = setOf("route-2", "route-1"),
            nameQuery = "Local",
        )
        val pagingSource = TripPagingSource(fakeApiService, filters)

        pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = TripPagingSource.PAGE_SIZE,
                placeholdersEnabled = false,
            ),
        )

        assertEquals(
            mapOf(
                "filter[route]" to "route-1,route-2",
                "filter[name]" to "Local",
            ),
            fakeApiService.lastFilters,
        )
    }

    @Test
    fun `getRefreshKey returns closest offset from anchor position`() {
        val pagingSource = TripPagingSource(fakeApiService, TripFilters())
        val pageSize = TripPagingSource.PAGE_SIZE
        val page1 =
            PagingSource.LoadResult.Page(
                data = createDomainTrips(startId = 1, count = pageSize),
                prevKey = null,
                nextKey = pageSize,
            )
        val page2 =
            PagingSource.LoadResult.Page(
                data = createDomainTrips(startId = 11, count = pageSize),
                prevKey = 0,
                nextKey = pageSize * 2,
            )

        val state =
            PagingState(
                pages = listOf(page1, page2),
                anchorPosition = 25,
                config = PagingConfig(pageSize = pageSize),
                leadingPlaceholderCount = 0,
            )

        val refreshKey = pagingSource.getRefreshKey(state)

        assertEquals(pageSize, refreshKey)
    }

    private fun createTestTrips(count: Int): List<TripDataDto> = (1..count).map { i ->
        TripDataDto(
            id = "trip-$i",
            attributes = TripAttributesDto(
                name = "Trip $i",
                headsign = "Headsign $i",
            ),
        )
    }

    private fun createDomainTrips(startId: Int, count: Int): List<Trip> =
        (startId until startId + count).map { i ->
            Trip(
                id = "trip-$i",
                name = "Trip $i",
                headsign = "Headsign $i",
                blockId = "BlockId $i",
                shapeId = null,
            )
        }
}

private class FakeTripMbtaApiService : TripMbtaApiService {
    private var response: TripsResponse? = null
    private var error: Exception? = null
    var lastFilters: Map<String, String>? = null

    fun setResponse(response: TripsResponse) {
        this.response = response
        this.error = null
    }

    fun setError(error: Exception) {
        this.error = error
        this.response = null
    }

    override suspend fun getTrips(
        filters: Map<String, String>,
        offset: Int,
        limit: Int,
        fields: String,
    ): TripsResponse {
        lastFilters = filters
        error?.let { throw it }
        return response ?: TripsResponse(emptyList())
    }

    override suspend fun getTrip(id: String, fields: String) = error("not used in paging tests")
}
