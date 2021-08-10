/*
 * Copyright (c) 2021 Zachery Elliot <zachery@foxboy.sh>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package sh.foxboy.bapp.api.punishment;

import java.util.Date;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Nullable;

public interface Punishment {
    public PunishmentResponse commit();

    public Integer getId();

    public PunishmentType getType();

    public OfflinePlayer getArbiter();

    @Nullable
    public OfflinePlayer getTarget();

    public String getReason();

    public Date getExpiry();

    public Boolean isAppealed();
}
