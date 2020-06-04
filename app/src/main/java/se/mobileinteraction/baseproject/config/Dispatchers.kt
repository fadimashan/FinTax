package se.mobileinteraction.baseproject.config

import io.reactivex.Scheduler

data class Dispatchers(val main: Scheduler, val io: Scheduler, val computation: Scheduler)
