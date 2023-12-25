package com.obrekht.coffeeshops.geolocation.data.di

import com.obrekht.coffeeshops.geolocation.data.repository.GeoLocationRepository
import com.obrekht.coffeeshops.geolocation.data.repository.PlayServicesGeoLocationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class GeoLocationRepositoryModule {

    @Singleton
    @Binds
    abstract fun bindGeoLocationRepository(
        repository: PlayServicesGeoLocationRepository
    ): GeoLocationRepository
}
