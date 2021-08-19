package sh.foxboy.bapp.api.cache;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface Cache<T extends Cacheable> {
    public interface Predicate<T extends Cacheable> {
        /**
         * Matching function that should evaluate to true if a given object matches any
         * necessary criteria.
         *
         * @param object The object being tested
         * @return True if this object matches the necessary criteria.
         */
        boolean match(T object);
    }

    /**
     * Return the size of this cache.
     *
     * @return The size of this cache.
     */
    public int size();


    /**
     * Retrieve an object from the cache.
     *
     * @param key The key of the object
     * @return The requested object, if it exists
     */
    @Nullable
    public T get(@NotNull String key);

    /**
     * Return all values in the cache.
     *
     * @return All values in the cache
     */
    @NotNull
    public Collection<T> getAll();

    /**
     * Find an object using the given tester lambda.
     *
     * @param tester A cache tester implemented for any necessary criteria you are
     *               looking for
     * @return The first object that evaluates the tester to true, if there is one
     */
    @Nullable
    public T find(@NotNull Predicate<T> tester);

    /**
     * Store an object in the cache.
     *
     * @param object The object to store
     */
    public void put(@NotNull T object);

    /**
     * Update a value in the cache - bypasses not-null check!
     *
     * @param object The object to update
     */
    public void update(@NotNull T object);

    /**
     * Remove an object from the cache, returning the old object.
     *
     * @param object The object to remove
     * @return The removed object, if it exists
     */
    @Nullable
    public T remove(@NotNull T object);
    /**
     * Remove an object from the cache using its key.
     *
     * @param key The key to remove
     * @return The removed object, if it exists
     */
    public T removeKey(@NotNull String key);

    /**
     * Fetch the oldest entry in the cache.
     *
     * @return The oldest entry in the cache, if it exists
     */
    @Nullable
    public T getOldestEntry();

    /**
     * Remove the oldest entry from the cache.
     *
     * @return The oldest entry in the cache, if it exists
     */
    @Nullable
    public T removeOldestEntry();
}