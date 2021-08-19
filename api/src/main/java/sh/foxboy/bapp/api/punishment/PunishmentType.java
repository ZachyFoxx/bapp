/*
 * Copyright (c) 2021 Zachery Elliot <zachery@foxboy.sh>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package sh.foxboy.bapp.api.punishment;

public enum PunishmentType {
    BAN,
    MUTE,
    KICK,
    WARN,
    UNKNOWN;

    public static PunishmentType fromOrdinal(Integer ordinal) {
        return switch (ordinal) {
            case 0 -> BAN;
            case 1 -> MUTE;
            case 2 -> KICK;
            case 3 -> WARN;
            default -> UNKNOWN;
        };
    }
}
