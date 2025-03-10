package config.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

@Module
@ComponentScan("application", "domain", "config", "infrastructure")
class AppModule