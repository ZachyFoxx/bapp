/*
 * Copyright (c) 2022-2025 Zachery Elliot <notzachery@gmail.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package sh.foxboy.bapp.api.entity;

import java.util.List;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import sh.foxboy.bapp.api.cache.Cacheable;
import sh.foxboy.bapp.api.punishment.Punishment;
import sh.foxboy.bapp.api.punishment.SortBy;

public interface User extends Punishable, Cacheable {
    @NotNull
    String getName();

    @NotNull
    UUID getUniqueId();

    @NotNull
    @Override
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
}
