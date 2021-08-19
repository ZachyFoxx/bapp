package sh.foxboy.bapp.api.managers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sh.foxboy.bapp.api.entity.Arbiter;
import sh.foxboy.bapp.api.entity.User;
import sh.foxboy.bapp.api.punishment.Punishment;
import sh.foxboy.bapp.api.punishment.PunishmentType;
import sh.foxboy.bapp.api.punishment.SortBy;

import java.util.Date;
import java.util.List;

public interface PunishmentManager {

	@NotNull
	List<Punishment> getPunishments();

	@NotNull
	List<Punishment> getPunishments(@NotNull SortBy order);

	@NotNull
	List<Punishment> getPunishments(@NotNull SortBy order, @NotNull Integer page);

	@NotNull
	List<Punishment> getPunishments(@NotNull SortBy order, @NotNull Integer page, @NotNull Integer pageSize);

	@NotNull
	List<Punishment> getPunishments(@NotNull Integer page);

	@NotNull
	List<Punishment> getPunishments(@NotNull Integer page, @NotNull Integer pageSize);

	@NotNull
	Punishment createPunishment(@NotNull PunishmentType type, @NotNull Arbiter arbiter, @Nullable User target, @NotNull String reason, @NotNull Date expiry);

	@Nullable
	Punishment deletePunishment(@NotNull Punishment punishment);
}
