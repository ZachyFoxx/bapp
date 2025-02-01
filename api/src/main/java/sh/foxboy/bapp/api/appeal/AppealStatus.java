/*
 * Copyright (c) 2025 Zachery Elliot <notzachery@gmail.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package sh.foxboy.bapp.api.appeal;

import org.jetbrains.annotations.NotNull;

public enum AppealStatus {
    UNKNOWN,
    PENDING,
    REJECTED,
    APPROVED,
    NO_APPEAL;

    @NotNull
    public static AppealStatus fromOrdinal(@NotNull Integer ordinal) {
        AppealStatus[] values = AppealStatus.values();
        if (ordinal < 0 || ordinal >= values.length) {
            throw new IllegalArgumentException(
                "Invalid ordinal for AppealStatus: " + ordinal
            );
        }
        return values[ordinal];
    }
}
