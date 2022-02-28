/*
 * Copyright (c) 2021 Zachery Elliot <zachery@foxboy.sh>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package sh.foxboy.bapp

interface WithPlugin {
    val plugin
        get() = Bapp.plugin

    val logger
        get() = this.plugin.logger

    val config
        get() = this.plugin.config
}
