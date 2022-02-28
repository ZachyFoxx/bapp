/*
 * Copyright (c) 2021 Zachery Elliot <zachery@foxboy.sh>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package sh.foxboy.bapp.database

import org.jetbrains.exposed.sql.SqlLogger
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.statements.StatementContext
import org.jetbrains.exposed.sql.statements.expandArgs
import sh.foxboy.bapp.WithPlugin

class ExposedLogger : WithPlugin, SqlLogger {
    override fun log(context: StatementContext, transaction: Transaction) {
        this.logger.fine("[bapp] ${context.expandArgs(transaction)}")
    }
}
