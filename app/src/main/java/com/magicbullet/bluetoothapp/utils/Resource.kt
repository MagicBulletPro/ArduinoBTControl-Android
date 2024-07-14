package com.magicbullet.bluetoothapp.utils

sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    class Initial<T> : Resource<T>()
    class Loading<T> : Resource<T>()
    class Connected<T>(message: String?) : Resource<T>(message = message)
    class Received<T>(data: T?, message: String?) : Resource<T>(data, message)
    class Disconnected<T>(message: String?) : Resource<T>(message = message)
    class CommandSent<T>(message: String?) : Resource<T>(message = message)
    class Error<T>(message: String?) : Resource<T>(message = message)
}