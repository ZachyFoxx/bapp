/*
 * Copyright (c) 2025 Zachery Elliot <notzachery@gmail.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package sh.foxboy.bapp.api.entity;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sh.foxboy.bapp.api.flag.BehaviorFlag;
import sh.foxboy.bapp.api.punishment.Punishment;
import sh.foxboy.bapp.api.punishment.PunishmentResponse;

public interface Punishable {
    @NotNull
    PunishmentResponse ban(@NotNull String reason, @NotNull Arbiter arbiter);

    @NotNull
    PunishmentResponse mute(@NotNull String reason, @NotNull Arbiter arbiter);

    @NotNull
    PunishmentResponse warn(@NotNull String reason, @NotNull Arbiter arbiter);

    @NotNull
    PunishmentResponse kick(@NotNull String reason, @NotNull Arbiter arbiter);

    @NotNull
    PunishmentResponse ban(
        @NotNull String reason,
        @NotNull Arbiter arbiter,
        @Nullable Long expiry
    );

    @NotNull
    PunishmentResponse mute(
        @NotNull String reason,
        @NotNull Arbiter arbiter,
        @Nullable Long expiry
    );

    @NotNull
    PunishmentResponse warn(
        @NotNull String reason,
        @NotNull Arbiter arbiter,
        @Nullable Long expiry
    );

    @NotNull
    PunishmentResponse ban(
        @NotNull String reason,
        @NotNull Arbiter arbiter,
        @Nullable Long expiry,
        @Nullable List<BehaviorFlag> flags
    );

    @NotNull
    PunishmentResponse mute(
        @NotNull String reason,
        @NotNull Arbiter arbiter,
        @Nullable Long expiry,
        @Nullable List<BehaviorFlag> flags
    );

    @NotNull
    PunishmentResponse warn(
        @NotNull String reason,
        @NotNull Arbiter arbiter,
        @Nullable Long expiry,
        @Nullable List<BehaviorFlag> flags
    );

    @NotNull
    List<Punishment> getPunishments();
}
