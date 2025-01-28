/*
 * Copyright (c) 2022-2025 Zachery Elliot <notzachery@gmail.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package sh.foxboy.bapp.api.managers;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sh.foxboy.bapp.api.entity.Arbiter;
import sh.foxboy.bapp.api.entity.User;
import sh.foxboy.bapp.api.punishment.Punishment;
import sh.foxboy.bapp.api.punishment.PunishmentType;
import sh.foxboy.bapp.api.punishment.SortBy;

public interface PunishmentManager {
    @NotNull
    Arbiter getConsoleArbiter();

    @NotNull
    List<Punishment> getPunishments();

    @NotNull
    List<Punishment> getPunishments(@NotNull SortBy order);

    @NotNull
    List<Punishment> getPunishments(
        @NotNull SortBy order,
        @NotNull Integer page
    );

    @NotNull
    List<Punishment> getPunishments(
        @NotNull SortBy order,
        @NotNull Integer page,
        @NotNull Integer pageSize
    );

    @NotNull
    List<Punishment> getPunishments(@NotNull Integer page);

    @NotNull
    List<Punishment> getPunishments(
        @NotNull Integer page,
        @NotNull Integer pageSize
    );

    @NotNull
    Punishment createPunishment(
        @NotNull PunishmentType type,
        @NotNull Arbiter arbiter,
        @Nullable User target,
        @NotNull String reason,
        @Nullable Long expiry
    );

    @Nullable
    Punishment deletePunishment(@NotNull Punishment punishment);
}
