package sh.foxboy.bapp.api.punishment;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sh.foxboy.bapp.api.entity.Arbiter;
import sh.foxboy.bapp.api.entity.User;

import java.util.Date;
import java.util.List;

public interface PunishmentManager {

	@NotNull
	public List<Punishment> getPunishments();

	@NotNull
	public List<Punishment> getPunishments(@NotNull SortBy order);

	@NotNull
	public List<Punishment> getPunishments(@NotNull SortBy order, @NotNull Integer page);

	@NotNull
	public List<Punishment> getPunishments(@NotNull SortBy order, @NotNull Integer page, @NotNull Integer pageSize);

	@NotNull
	public List<Punishment> getPunishments(@NotNull Integer page);

	@NotNull
	public List<Punishment> getPunishments(@NotNull Integer page, @NotNull Integer pageSize);

	@NotNull
	public Punishment createPunishment(@NotNull PunishmentType type, @NotNull Arbiter arbiter, @Nullable User target, @NotNull String reason, @NotNull Date expiry);

	@Nullable
	public Punishment deletePunishment(@NotNull Punishment punishment);
}
