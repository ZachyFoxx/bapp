/*
 * Copyright (c) 2021 Zachery Elliot <zachery@foxboy.sh>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package sh.foxboy.bapp.api.punishment;

import org.jetbrains.annotations.NotNull;

public enum PunishmentType {
    BAN,
    MUTE,
    KICK,
    WARN,
    UNKNOWN;

    @NotNull
    public static PunishmentType fromOrdinal(@NotNull Integer ordinal) {
        PunishmentType[] values = PunishmentType.values();
        if (ordinal < 0 || ordinal >= values.length) {
            throw new IllegalArgumentException(
                "Invalid ordinal for PunishmentType: " + ordinal
            );
        }
        return values[ordinal];
    }
}
