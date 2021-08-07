/*
 * Copyright (c) 2020 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.myawesomeplugin

import kr.entree.spigradle.annotations.PluginMain
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin

@PluginMain
class MyAwesomePlugin : JavaPlugin(), MyAwesomeAPI {
    override fun getProvider(): Plugin {
        return this
    }

    override fun getWelcomeMessage(): String {
        return "Hewwo~ wowwd owo!"
    }
}
