package com.example.timeshirt.di.modules

import com.example.timeshirt.repository.BleScannerRepository
import com.example.timeshirt.repository.BleScannerRepositoryImpl
import com.example.timeshirt.repository.BleSerialCommunicationRepository
import com.example.timeshirt.repository.BleSerialCommunicationRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun provideBleScannerRepository(bleScannerRepositoryImpl: BleScannerRepositoryImpl): BleScannerRepository

    @Binds
    @Singleton
    abstract fun provideBleSerialCommunicationRepository(bleSerialCommunicationRepositoryImpl: BleSerialCommunicationRepositoryImpl): BleSerialCommunicationRepository

}