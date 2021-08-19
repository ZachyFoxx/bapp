/*
 * Copyright (c) 2021 Zachery Elliot <zachery@foxboy.sh>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package sh.foxboy.bapp.api.punishment;

import java.util.Date;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Nullable;
import sh.foxboy.bapp.api.cache.Cacheable;
import sh.foxboy.bapp.api.entity.Arbiter;
import sh.foxboy.bapp.api.entity.User;

public interface Punishment extends Cacheable {
    public PunishmentResponse commit();

    public Integer getId();

    public PunishmentType getType();

    public Arbiter getArbiter();

    @Nullable
    public User getTarget();

    public String getReason();

    public Date getExpiry();

    public Boolean isAppealed();
}
