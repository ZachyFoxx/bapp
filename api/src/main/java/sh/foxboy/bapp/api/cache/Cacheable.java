package sh.foxboy.bapp.api.cache;

import org.jetbrains.annotations.NotNull;

public interface Cacheable {

	@NotNull
	String getKey();
}
