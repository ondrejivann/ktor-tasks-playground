package config.di

import domain.ports.TaskRepository
import infrastructure.persistence.TaskRepositoryImpl
import org.koin.dsl.module

//val repositoryModule = module {
//    single<TaskRepository> { TaskRepositoryImpl() }
//    // Alternative
//    //singleOf(::TaskRepositoryImpl) { bind<TaskRepository>() }
//}