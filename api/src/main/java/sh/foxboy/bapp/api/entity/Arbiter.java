package sh.foxboy.bapp.api.entity;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface Arbiter {

    @NotNull
    String getName();

    @NotNull
    UUID getUniqueId();
}
