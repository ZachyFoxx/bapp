package entity;

import org.jetbrains.annotations.NotNull;
import sh.foxboy.bapp.api.cache.Cacheable;

public interface Arbiter extends Cacheable {
	@NotNull
	String getName();
}
