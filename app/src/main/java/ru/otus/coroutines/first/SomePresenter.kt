package ru.otus.coroutines.first

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class SomePresenter {
    private val scope = CoroutineScope(Dispatchers.IO)
    /**
     * 1. Реализуйте получение данных из метода BlockingRepository#getHeavyData
     * 2. Если BlockingRepository#getHeavyData не ответит в течение 5 секунд, необходимо
     * заэмитить в LiveData/StateFlow объект Error
     * 3. При успешном сценарии пробросьте в в LiveData/StateFlow объект Success
     */
    fun populateHeavyData(): Flow<Result> {
        return channelFlow {
            val heavyJob = scope.launch {
                BlockingRepository().getHeavyData()
                if (isActive) {
                    send(Success)
                }
            }
            delay(5000)
            if (heavyJob.isActive) {
                heavyJob.cancel()
                send(Error(Exception()))
            }
        }
    }


    sealed interface Result
    data object Success : Result
    data class Error(val exception: Exception) : Result

}

fun main() {
    println("Main start")
    runBlocking {
        SomePresenter().populateHeavyData().first().let {
            println("Result: $it")
        }
    }
    println("Main end")
}