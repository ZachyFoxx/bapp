/*
 * Copyright (c) 2020-2021 Zachery Elliot <zachery@foxboy.sh>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package sh.foxboy.bapp

import kr.entree.spigradle.annotations.PluginMain
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import sh.foxboy.bapp.api.BappAPI

@PluginMain
class Bapp : JavaPlugin(), BappAPI {

    companion object {
        lateinit var plugin: Bapp
    }

    override fun getProvider(): Plugin {
        return this
    }

    override fun onLoad() {
        plugin = this
    }

    override fun onEnable() {
        BappAPI.registerService(this, this)
        logger.info("Hello!")
    }

    override fun onDisable() {
    }
}
