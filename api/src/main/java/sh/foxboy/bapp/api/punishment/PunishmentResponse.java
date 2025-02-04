/*
 * Copyright (c) 2021-2025 Zachery Elliot <notzachery@gmail.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package sh.foxboy.bapp.api.punishment;

public enum PunishmentResponse {
    OK,
    TARGET_NOT_EXIST,
    TARGET_ALREADY_PUNISHED,
    TARGET_IMMUNE,
    PUNISHMENT_ALREADY_PUSHED,
    PERMISSION_DENIED,
    DATABASE_BUSY,
    SERVER_ERROR,
    DURATION_EXCEEDS_PERMISSION,
}
