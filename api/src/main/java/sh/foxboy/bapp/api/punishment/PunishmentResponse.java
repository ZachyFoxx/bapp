/*
 * Copyright (c) 2021 Zachery Elliot <zachery@foxboy.sh>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package sh.foxboy.bapp.api.punishment;

public enum PunishmentResponse {
    OK,
    TARGET_NOT_EXIST,
    TARGET_ALREADY_PUNISHED,
    PUNISHMENT_ALREADY_PUSHED,
    PERMISSION_DENIED,
    DATABASE_BUSY,
    SERVER_ERROR,
}
