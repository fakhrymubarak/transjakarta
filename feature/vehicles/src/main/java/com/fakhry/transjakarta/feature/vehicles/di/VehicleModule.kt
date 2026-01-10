package com.fakhry.transjakarta.feature.vehicles.di

import com.fakhry.transjakarta.feature.vehicles.data.remote.service.VehicleMbtaApiService
import com.fakhry.transjakarta.feature.vehicles.data.repository.VehicleRepositoryImpl
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
}

@Module
@InstallIn(SingletonComponent::class)
object VehicleModule {
    @Provides
    @Singleton
    fun provideMbtaApiService(retrofit: Retrofit): VehicleMbtaApiService =
        retrofit.create(VehicleMbtaApiService::class.java)
}
