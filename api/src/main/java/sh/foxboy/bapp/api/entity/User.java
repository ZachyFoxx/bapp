package sh.foxboy.bapp.api.entity;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface User extends Punishable {

	@NotNull
	public String getName();

	@NotNull
	public UUID getUniqueId();
}
