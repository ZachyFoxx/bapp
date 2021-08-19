/*
 * Copyright (c) 2021 Zachery Elliot <zachery@foxboy.sh>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package sh.foxboy.bapp.api.punishment;

import java.util.Date;
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

    @NotNull
    Date getExpiry();

    @NotNull
    Boolean isAppealed();
}
