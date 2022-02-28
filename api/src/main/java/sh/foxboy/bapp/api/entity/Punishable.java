package sh.foxboy.bapp.api.entity;

import org.jetbrains.annotations.NotNull;
import sh.foxboy.bapp.api.punishment.Punishment;
import sh.foxboy.bapp.api.punishment.PunishmentResponse;

import java.util.Date;
import java.util.List;

public interface Punishable {

	@NotNull
	PunishmentResponse ban(@NotNull String reason, @NotNull Arbiter arbiter);

	@NotNull
	PunishmentResponse mute(@NotNull String reason, @NotNull Arbiter arbiter);

	@NotNull
	PunishmentResponse warn(@NotNull String reason, @NotNull Arbiter arbiter);

	@NotNull
	PunishmentResponse kick(@NotNull String reason, @NotNull Arbiter arbiter);

	@NotNull
	PunishmentResponse ban(@NotNull String reason, @NotNull Arbiter arbiter, @NotNull Date expiry);

	@NotNull
	PunishmentResponse mute(@NotNull String reason, @NotNull Arbiter arbiter, @NotNull Date expiry);

	@NotNull
	PunishmentResponse warn(@NotNull String reason, @NotNull Arbiter arbiter, @NotNull Date expiry);

	@NotNull
	List<Punishment> getPunishments();
}
