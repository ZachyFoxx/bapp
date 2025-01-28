/*
 * Copyright (c) 2021-2025 Zachery Elliot <notzachery@gmail.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package sh.foxboy.bapp.api.punishment;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sh.foxboy.bapp.api.cache.Cacheable;
import sh.foxboy.bapp.api.entity.Arbiter;
import sh.foxboy.bapp.api.entity.User;

public interface Punishment extends Cacheable {
    @NotNull
    PunishmentResponse commit();

    @NotNull
    Integer getId();

    @NotNull
    PunishmentType getType();

    @NotNull
    Arbiter getArbiter();

    @Nullable
    User getTarget();

    @NotNull
    String getReason();

    @Nullable
    Long getExpiry();

    @NotNull
    Boolean isAppealed();
}
