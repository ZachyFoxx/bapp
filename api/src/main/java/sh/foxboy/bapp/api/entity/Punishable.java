package sh.foxboy.bapp.api.entity;

import org.jetbrains.annotations.NotNull;
import sh.foxboy.bapp.api.punishment.Punishment;
import sh.foxboy.bapp.api.punishment.PunishmentResponse;

import java.util.Date;
import java.util.List;

public interface Punishable {

	@NotNull
	public PunishmentResponse ban(@NotNull String reason);

	@NotNull
	public PunishmentResponse mute(@NotNull String reason);

	@NotNull
	public PunishmentResponse warn(@NotNull String reason);

	@NotNull
	public PunishmentResponse kick(@NotNull String reason);

	@NotNull
	public PunishmentResponse ban(@NotNull String reason, @NotNull Date expiry);

	@NotNull
	public PunishmentResponse mute(@NotNull String reason, @NotNull Date expiry);

	@NotNull
	public PunishmentResponse warn(@NotNull String reason, @NotNull Date expiry);

	@NotNull
	public List<Punishment> getPunishments();
}

