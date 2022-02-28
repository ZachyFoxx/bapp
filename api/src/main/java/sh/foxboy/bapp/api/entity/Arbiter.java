/*
 * Copyright (c) 2022 Zachery Elliot <notzachery@gmail.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package sh.foxboy.bapp.api.entity;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import sh.foxboy.bapp.api.punishment.Punishment;
import sh.foxboy.bapp.api.punishment.SortBy;

public interface Arbiter extends User {
    @NotNull
    List<Punishment> getBoundPunishments();

    @NotNull
    List<Punishment> getBoundPunishments(@NotNull SortBy order);

    @NotNull
    List<Punishment> getBoundPunishments(
        @NotNull SortBy order,
        @NotNull Integer page
    );

    @NotNull
    List<Punishment> getBoundPunishments(
        @NotNull SortBy order,
        @NotNull Integer page,
        @NotNull Integer pageSize
    );

    @NotNull
    List<Punishment> getBoundPunishments(@NotNull Integer page);

    @NotNull
    List<Punishment> getBoundPunishments(
        @NotNull Integer page,
        @NotNull Integer pageSize
    );
}
