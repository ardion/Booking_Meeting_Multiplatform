package com.bumi.app.di

import com.bumi.app.data.MeetingRemoteDataSource
import com.bumi.app.data.MeetingRemoteDataSourceImpl
import com.bumi.app.data.repository.AuthRepositoryImpl
import com.bumi.app.data.repository.MeetingRepositoryImpl
import com.bumi.app.domain.Irepository.AuthRepository
import com.bumi.app.domain.Irepository.MeetingRepository
import com.bumi.app.domain.usecase.CreateBookingUseCase
import com.bumi.app.domain.usecase.DeleteBookingUseCase
import com.bumi.app.domain.usecase.GetBookedSlotsUseCase
import com.bumi.app.domain.usecase.GetBookingsUseCase
import com.bumi.app.domain.usecase.LoginUseCase
import com.bumi.app.domain.usecase.MeetingUseCases
import com.bumi.app.domain.usecase.UpdateStatusUseCase
import com.bumi.app.presentation.booking.MeetingViewModel
import com.bumi.app.presentation.login.AuthViewModel
import com.bumi.app.utils.SessionManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import com.russhwolf.settings.Settings
import com.bumi.app.data.*
import com.bumi.app.data.remote.AuthApiService

val appModule = module {
    single { Settings() }
    single { SessionManager(get()) }
    single<CoroutineDispatcher> { Dispatchers.Default }

    single { AuthApiService(get()) }

    single { AuthRemoteDataSource(get()) }

    singleOf(::MeetingRemoteDataSourceImpl) { bind<MeetingRemoteDataSource>() }

    singleOf(::AuthRepositoryImpl) { bind<AuthRepository>() }
    singleOf(::MeetingRepositoryImpl) { bind<MeetingRepository>() }

    single { LoginUseCase(get()) }
    single {
        MeetingUseCases(
            getBookings = GetBookingsUseCase(get()),
            createBooking = CreateBookingUseCase(get()),
            updateStatus = UpdateStatusUseCase(get()),
            deleteBooking = DeleteBookingUseCase(get()),
            getBookedSlots = GetBookedSlotsUseCase(get())
        )
    }

    viewModel { AuthViewModel(get(), get()) }
    viewModel { MeetingViewModel(get()) }
}