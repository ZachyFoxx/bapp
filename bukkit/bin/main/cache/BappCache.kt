package sh.foxboy.bapp.cache

import org.jetbrains.annotations.NotNull
import sh.foxboy.bapp.api.cache.Cache
import sh.foxboy.bapp.api.cache.Cacheable
import sh.foxboy.bapp.api.entity.User
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.FutureTask
import kotlin.reflect.KClass


class BappCache<T : Cacheable>(private val clazz: KClass<T>) : Cache<T> {
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

    var ttl = (30 * 60e3).toLong()
    var maxSize = 0

    private fun objectExpiryTask() {
        if (ttl <= 0) {
            return
        }
        objectInsertionTimestamps.forEach { (k: String, v: Long) ->
            if (v + ttl < System.currentTimeMillis()) {
                removeKey(k)
            }
        }
        return
    }

    /**
     * Return the size of this cache.
     *
     * @return The size of this cache.
     */
    override fun size(): Int {
        return objects.size
    }

    /**
     * Retrieve an object from the cache.
     *
     * @param key The key of the object
     * @return The requested object, if it exists
     */
    override fun get(key: String): T? {
        return objects[key]
    }

    /**
     * Return all values in the cache.
     *
     * @return All values in the cache
     */
    override fun getAll() = objects.values

    /**
     * Find an object using the given tester lambda.
     *
     * @param tester A cache tester implemented for any necessary criteria you are
     * looking for
     * @return The first object that evaluates the tester to true, if there is one
     */
    fun find(tester: Predicate<T>): T? {
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
    override fun put(`object`: T) {
        if (objects.containsKey(`object`.key)) {
            return
        }
        if (maxSize > 0) {
            while (objects.size >= maxSize) {
                removeOldestEntry()
            }
        }
        objects[`object`.key] = `object`
        objectInsertionTimestamps[`object`.key] = System.currentTimeMillis()
        objectExpiryTask()
    }

    /**
     * Update a value in the cache - bypasses not-null check!
     *
     * @param object The object to update
     */
    override fun update(`object`: T) {
        if (objects.containsKey(`object`.key)) {
            remove(`object`)
        }
        put(`object`)
        objectExpiryTask()
    }

    /**
     * Remove an object from the cache, returning the old object.
     *
     * @param object The object to remove
     * @return The removed object, if it exists
     */
    override fun remove(`object`: T): T? {
        val didRemove = objects.remove(`object`.key) ?: return null

        objectExpiryTask()
        return didRemove
    }

    /**
     * Remove an object from the cache using its key.
     *
     * @param key The key to remove
     * @return The removed object, if it exists
     */
    override fun removeKey(key: String): T? {
        val `object` = objects[key] ?: return null
        return remove(`object`)
    }

    /**
     * Fetch the oldest entry in the cache.
     *
     * @return The oldest entry in the cache, if it exists
     */
    override fun getOldestEntry(): T? {
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
    override fun removeOldestEntry(): T? {
        return oldestEntry?.let { remove(it) }
    }

    override fun find(tester: Cache.Predicate<T>): T? {
        for (`object` in objects.values) {
            if (tester.match(`object`)) {
                return `object`
            }
        }
        return null
    }

}
