package sh.foxboy.bapp.api.entity;

import org.jetbrains.annotations.NotNull;
import sh.foxboy.bapp.api.cache.Cacheable;

import java.util.UUID;

public interface User extends Punishable, Cacheable {

	@NotNull
	String getName();

	@NotNull
	UUID getUniqueId();
}
