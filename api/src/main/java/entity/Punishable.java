package entity;

import org.jetbrains.annotations.NotNull;
import sh.foxboy.bapp.api.cache.Cacheable;
import sh.foxboy.bapp.api.punishment.PunishmentResponse;

import java.util.Date;

public interface Punishable extends Cacheable {

	@NotNull PunishmentResponse ban(@NotNull String reason);

	@NotNull PunishmentResponse mute(@NotNull String reason);

	@NotNull PunishmentResponse warn(@NotNull String reason);

	@NotNull PunishmentResponse kick(@NotNull String reason);

	@NotNull PunishmentResponse ban(@NotNull String reason, @NotNull Date expiry);

	@NotNull PunishmentResponse mute(@NotNull String reason, @NotNull Date expiry);

	@NotNull PunishmentResponse warn(@NotNull String reason, @NotNull Date expiry);
}
