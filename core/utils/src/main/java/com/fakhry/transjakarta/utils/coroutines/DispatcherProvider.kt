package com.fakhry.transjakarta.utils.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

data class DispatcherProvider(
    val main: CoroutineDispatcher,
    val computation: CoroutineDispatcher,
    val io: CoroutineDispatcher,
) {
    constructor() : this(
        main = Dispatchers.Main,
        computation = Dispatchers.Default,
        io = Dispatchers.IO,
    )

    constructor(testDispatcher: CoroutineDispatcher) : this(
        main = testDispatcher,
        computation = testDispatcher,
        io = testDispatcher,
    )
}
