package sh.foxboy.bapp.api.entity;

import org.jetbrains.annotations.NotNull;
import sh.foxboy.bapp.api.punishment.Punishment;
import sh.foxboy.bapp.api.punishment.PunishmentResponse;

import java.util.Date;
import java.util.List;

public interface Punishable {

	@NotNull
	PunishmentResponse ban(@NotNull String reason);

	@NotNull
	PunishmentResponse mute(@NotNull String reason);

	@NotNull
	PunishmentResponse warn(@NotNull String reason);

	@NotNull
	PunishmentResponse kick(@NotNull String reason);

	@NotNull
	PunishmentResponse ban(@NotNull String reason, @NotNull Date expiry);

	@NotNull
	PunishmentResponse mute(@NotNull String reason, @NotNull Date expiry);

	@NotNull
	PunishmentResponse warn(@NotNull String reason, @NotNull Date expiry);

	@NotNull
	List<Punishment> getPunishments();
}
