package entity;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface User {

	@NotNull
	public String getName();

	@NotNull
	public UUID getUniqueId();

}
