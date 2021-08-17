package sh.foxboy.bapp.cache

import sh.foxboy.bapp.api.cache.Cache
import sun.jvm.hotspot.debugger.Debugger

import java.util.concurrent.FutureTask

import java.util.concurrent.ConcurrentHashMap

import sh.foxboy.bapp.api.cache.Cacheable
import java.util.concurrent.Callable
import java.util.function.BiConsumer
import org.jetbrains.annotations.NotNull


class BappCache<T : Cacheable?>(private val clazz: Class<T>) : Cache<T : Cacheable> {
    interface Predicate<T : Cacheable?> {
        /**
         * Matching function that should evaluate to true if a given object matches any
         * necessary criteria.
         *
         * @param object The object being tested
         * @return True if this object matches the necessary criteria.
         */
        fun match(`object`: T): Boolean
    }

    private val objects = ConcurrentHashMap<String?, T>()
    private val objectInsertionTimestamps = ConcurrentHashMap<String, Long>()

    val ttl = (30 * 60e3).toLong()
    val maxSize = 0

    val objectExpiryTask: FutureTask<*> = FutureTask<Void>(object : Callable<Boolean?> {
        override fun call(): Boolean {
            if (ttl <= 0) {
                return false
            }
            objectInsertionTimestamps.forEach({ k: String, v: Long ->
                if (v + ttl < System.currentTimeMillis()) {
                    removeKey(k)
                }
            })
            return true
        }
    })

    /**
     * Return the size of this cache.
     *
     * @return The size of this cache.
     */
    fun size(): Int {
        return objects.size
    }

    /**
     * Retrieve an object from the cache.
     *
     * @param key The key of the object
     * @return The requested object, if it exists
     */
    operator fun get(@NotNull key: String): T? {
        val `object` = objects[key]
        return `object`
    }

    /**
     * Return all values in the cache.
     *
     * @return All values in the cache
     */
    val all: Collection<T>
    get() = objects.values

    /**
     * Find an object using the given tester lambda.
     *
     * @param tester A cache tester implemented for any necessary criteria you are
     * looking for
     * @return The first object that evaluates the tester to true, if there is one
     */
    fun find(@NotNull tester: Predicate<T>): T? {
        for (`object`: T in objects.values) {
            if (tester.match(`object`)) {
                return `object`
            }
        }
        return null
    }

    /**
     * Store an object in the cache.
     *
     * @param object The object to store
     */
    fun put(@NotNull `object`: T) {
        if (objects.containsKey(`object`.getKey())) {
            return
        }
        if (maxSize > 0) {
            while (objects.size >= maxSize) {
                removeOldestEntry()
            }
        }
        objects[`object`.getKey()] = `object`
        objectInsertionTimestamps[`object`.getKey()] = System.currentTimeMillis()
    }

    /**
     * Update a value in the cache - bypasses not-null check!
     *
     * @param object The object to update
     */
    fun update(@NotNull1 `object`: T) {
        if (objects.containsKey(`object`.getKey())) {
            remove(`object`)
        }
        put(`object`)
    }

    /**
     * Remove an object from the cache, returning the old object.
     *
     * @param object The object to remove
     * @return The removed object, if it exists
     */
    fun remove(@NotNull1 `object`: T?): T? {
        debug.reset()
        val didRemove = objects.remove(`object`.getKey())
        if (didRemove == null) {
            debug.print(
                "Could not remove entry for " + clazz.simpleName + " with key " + `object`.getKey()
                        + " - does not exist"
            )
            return null
        }

        // if (maxMemoryUsage > 0) {
        // memoryUsage -= MemoryUtil.getSizeOf(object);
        // }
        return didRemove
    }

    /**
     * Remove an oibject from the cache using its key.
     *
     * @param key The key to remove
     * @return The removed object, if it exists
     */
    fun removeKey(@NotNull1 key: String?): T? {
        val `object` = objects.get(key) ?: return null
        return remove(`object`)
    }

    /**
     * Fetch the oldest entry in the cache.
     *
     * @return The oldest entry in the cache, if it exists
     */
    val oldestEntry: T?
    get() {
        var oldest: String? = null
        var oldestTimestamp: Long? = Long.MAX_VALUE
        for (k: String in objectInsertionTimestamps.keys) {
            val v = objectInsertionTimestamps[k]
            if (v!! < (oldestTimestamp)!!) {
                oldest = k
                oldestTimestamp = v
            }
        }
        return objects[oldest]
    }

    /**
     * Remove the oldest entry from the cache.
     *
     * @return The oldest entry in the cache, if it exists
     */
    fun removeOldestEntry(): T? {
        return remove(oldestEntry)
    }
}