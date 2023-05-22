package service.redis;

import java.util.List;
import java.util.Map;

/**
 * Interface for Redis cache interactions.
 * <br>Commands to interact with Redis based on data type we are working with:
 * <a href="https://stackoverflow.com/questions/37953019/wrongtype-operation-against-a-key-holding-the-wrong-kind-of-value-php">Source</a>
 *
 * <br>* if value is of type string -> GET $key
 * <br>* if value is of type hash -> HGET or HMGET or HGETALL $key
 * <br>* if value is of type lists -> lrange $key $start $end
 * <br>* if value is of type sets -> smembers $key
 * <br>* if value is of type sorted sets -> ZRANGEBYSCORE $key $min $max
 * <br>* if value is of type stream -> xread count $count streams $key $ID.
 * <a href="https://redis.io/commands/xread">https://redis.io/commands/xread</a>
 *
 * <p>* Use the TYPE command to check the type of value a key is mapping to: type $key
 *
 * @param <T> Class type of the object we expect to fetch from the Cache.
 */
public interface RedisCache<T> {

    /**
     * Fetched cached data from Redis for provided Key.
     * <br>Returns Null if key was not found.
     *
     * @param cacheKey Key to use to retrieve data of interest
     * @return Data associated with the provided key if they exist, null otherwise.
     */
    T cacheGet(String cacheKey);

    /**
     * Fetched cached data from Redis for provided Key.
     * <br>Returns Null value for keys not found.
     *
     * @param cacheKeys Keys to use to retrieve data of interest
     * @return List of Data associated with the provided keys. Null values provided for keys not found.
     */
    List<T> cacheGetMultiple(List<String> cacheKeys);

    /**
     * Fetch all cached data.
     *
     * @return All cached data
     */
    List<T> cacheGetAll();

    /**
     * Cache a value into the reddis cache with the provided key.
     *
     * @param key   Key used to store the value in the cahce
     * @param value Data we wish to cache
     */
    void cachePut(String key, String value);

    /**
     * Delete data from the Redis cache associated with the provided key.
     *
     * @param key Key we wish to purge its contents
     * @return True if delete was successful, false is key was not present or delete was unsuccessful
     */
    boolean cacheDelete(String key);

    /**
     * Delete data from the Redis cache who's key matches the provided regular expression.
     *
     * @param regExKey Key we wish to purge its contents
     * @return True if delete was successful, false is key was not present or delete was unsuccessful
     */
    Map<String, Boolean> cacheDeleteWithRegEx(String regExKey);
}
