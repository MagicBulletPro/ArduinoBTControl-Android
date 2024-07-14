package com.magicbullet.bluetoothapp.model

data class CommandModel(val type: String) {
    var accessory_list: List<Accessory>? = null
    var accessory: Accessory? = null
}
