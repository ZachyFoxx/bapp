package entity;

import org.jetbrains.annotations.NotNull;
import sh.foxboy.bapp.api.cache.Cacheable;

import java.util.UUID;

public interface User extends Arbiter, Punishable, Cacheable {

	@NotNull
	String getName();

	@NotNull
	UUID getUniqueId();

}
