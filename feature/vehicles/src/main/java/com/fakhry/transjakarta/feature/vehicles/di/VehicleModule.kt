package com.fakhry.transjakarta.feature.vehicles.di

import com.fakhry.transjakarta.feature.vehicles.data.remote.service.RouteMbtaApiService
import com.fakhry.transjakarta.feature.vehicles.data.remote.service.StopMbtaApiService
import com.fakhry.transjakarta.feature.vehicles.data.remote.service.TripMbtaApiService
import com.fakhry.transjakarta.feature.vehicles.data.remote.service.VehicleMbtaApiService
import com.fakhry.transjakarta.feature.vehicles.data.repository.RouteRepositoryImpl
import com.fakhry.transjakarta.feature.vehicles.data.repository.StopRepositoryImpl
import com.fakhry.transjakarta.feature.vehicles.data.repository.TripRepositoryImpl
import com.fakhry.transjakarta.feature.vehicles.data.repository.VehicleRepositoryImpl
import com.fakhry.transjakarta.feature.vehicles.domain.repository.RouteRepository
import com.fakhry.transjakarta.feature.vehicles.domain.repository.StopRepository
import com.fakhry.transjakarta.feature.vehicles.domain.repository.TripRepository
import com.fakhry.transjakarta.feature.vehicles.domain.repository.VehicleRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class VehicleRepositoryModule {
    @Binds
    abstract fun bindVehicleRepository(
        vehicleRepositoryImpl: VehicleRepositoryImpl,
    ): VehicleRepository

    @Binds
    abstract fun bindRouteRepository(routeRepositoryImpl: RouteRepositoryImpl): RouteRepository

    @Binds
    abstract fun bindTripRepository(tripRepositoryImpl: TripRepositoryImpl): TripRepository

    @Binds
    abstract fun bindStopRepository(stopRepositoryImpl: StopRepositoryImpl): StopRepository
}

@Module
@InstallIn(SingletonComponent::class)
object VehicleModule {
    @Provides
    @Singleton
    fun provideMbtaApiService(retrofit: Retrofit): VehicleMbtaApiService =
        retrofit.create(VehicleMbtaApiService::class.java)

    @Provides
    @Singleton
    fun provideRouteMbtaApiService(retrofit: Retrofit): RouteMbtaApiService =
        retrofit.create(RouteMbtaApiService::class.java)

    @Provides
    @Singleton
    fun provideTripMbtaApiService(retrofit: Retrofit): TripMbtaApiService =
        retrofit.create(TripMbtaApiService::class.java)

    @Provides
    @Singleton
    fun provideStopMbtaApiService(retrofit: Retrofit): StopMbtaApiService =
        retrofit.create(StopMbtaApiService::class.java)
}
