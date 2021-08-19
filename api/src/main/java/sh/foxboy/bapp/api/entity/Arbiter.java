package sh.foxboy.bapp.api.entity;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface Arbiter {

    /**
     * Username of this arbiter
     */
    @NotNull
    String getName();

    /**
     * Unique Id of this arbiter
     * @return {@Link UUID}
     */
    @NotNull
    UUID getUniqueId();
}
