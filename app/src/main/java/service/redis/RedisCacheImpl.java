package service.redis;

import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.redisson.config.ReplicatedServersConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Class Implementation for Redis cache interactions. This sample code is for a String object retrieval, but we can change it to whatever is needed.
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
 */
public class RedisCacheImpl implements RedisCache<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisCacheImpl.class);
    private static final String REDIS_WILDCARD = "*";
    private static final long REDIS_TTL = 1;
    private static final TimeUnit IN_DAYS = TimeUnit.DAYS;

    private final String redisPrefix;
    private final RedissonClient redisClient;

    /**
     * Sample code to instantiate and run the client.
     *
     * @param args Passed in process's arguments
     */
    @SuppressWarnings("checkstyle:UncommentedMain")
    public static void main(String[] args) {
        final RedisCacheImpl redisCache = new RedisCacheImpl("Testing_", List.of());
        final String key = "Demo";
        final String value = "Demo Value";

        // Place value in Redis
        redisCache.cachePut(key, value);

        // Fetch placed value
        final String fetchedResult = redisCache.cacheGet(key);
        LOGGER.info("GET {} from Redis yielded: {}", key, fetchedResult);

        // Delete placed key
        final boolean isDeleted = redisCache.cacheDelete(key);
        LOGGER.info("Deleted successfully = {}", isDeleted);

        // Using the get multiple to expose format - we will get the first result from the returned list.
        final String fetchPostDelete = redisCache.cacheGetMultiple(List.of(key)).get(0);
        LOGGER.info("GET {} from Redis now yields: {}", key, fetchPostDelete);

        // ======================
        // GetAll:
        final List<String> fetchAll = redisCache.cacheGetAll();
        LOGGER.info("All values from Redis = {}", fetchAll);

        Map<String, Boolean> deletedMap = redisCache.cacheDeleteWithRegEx(key);
        LOGGER.info("Performing a delete for all keys matching {}*, results: {}", key, deletedMap);
    }

    /**
     * Redisson client constructor. Takes a prefix as an input and is used for all Redis cache interactions.
     * <br>Using this technique we can have multiple uses of the same cache with unique prefixes.
     * <br>Example: Prefix "FIX_GATEWAY_*", "UI_GATEWAY_*", "REST_GATEWAY_*" all stored in the same cache.
     *
     * @param redisPrefix   Prefix associated with Redis session
     * @param nodeAddresses node addresses that we will connect for redis. Sample format: [redis://host:port]
     */
    public RedisCacheImpl(final String redisPrefix, final List<String> nodeAddresses) {
        LOGGER.info("Instantiating Redis Cache");
        this.redisPrefix = redisPrefix;
        final Config config = new Config();

        ReplicatedServersConfig rsc = config.useReplicatedServers().setDatabase(0);
        for (String eachNodeAddress : nodeAddresses) {
            rsc.addNodeAddress(eachNodeAddress);
        }

        config.setCodec(JsonJacksonCodec.INSTANCE);

        this.redisClient = Redisson.create(config);
    }

    @Override
    public String cacheGet(final String cacheKey) {
        final String key = redisPrefix + cacheKey;
        LOGGER.info("Redis Cache Get {}", key);

        RBucket<String> bucket = redisClient.getBucket(key);
        return bucket.get();
    }

    @Override
    public List<String> cacheGetMultiple(List<String> cacheKeys) {
        final String keys = redisPrefix + cacheKeys;
        LOGGER.info("Redis Cache Get Multiple {}", keys);
        final List<String> results = new ArrayList<>();

        cacheKeys.forEach(key -> {
            final RBucket<String> bucket = redisClient.getBucket(redisPrefix + key);
            results.add(bucket.get());
        });

        return results;
    }

    @Override
    public List<String> cacheGetAll() {
        LOGGER.info("Redis Cache Get All");
        // Command to fetch all keys with a prefix matching what this instance is associated with $redisPrefix
        final List<String> allKeys = redisClient.getKeys().getKeysStreamByPattern(redisPrefix + REDIS_WILDCARD).toList();
        final List<String> results = new ArrayList<>();

        allKeys.forEach(key -> {
            final RBucket<String> bucket = redisClient.getBucket(key);
            results.add(bucket.get());
        });

        return results;

    }

    @Override
    public void cachePut(String cacheKey, String value) {
        final String key = redisPrefix + cacheKey;
        LOGGER.info("Redis Cache Put {}", key);

        RBucket<String> bucket = redisClient.getBucket(key);
        bucket.set(value, REDIS_TTL, IN_DAYS);
    }

    @Override
    public boolean cacheDelete(String cacheKey) {
        final String key = redisPrefix + cacheKey;
        LOGGER.info("Redis Cache Delete key [{}]", key);

        return cacheDeleteWithExactKey(key);
    }

    @Override
    public Map<String, Boolean> cacheDeleteWithRegEx(String regExKey) {
        final Map<String, Boolean> result = new HashMap<>();
        LOGGER.info("Redis Cache Delete regEx [{}]", regExKey);

        redisClient.getKeys().getKeysStreamByPattern(redisPrefix + regExKey + REDIS_WILDCARD)
            .forEach(key -> result.put(key, cacheDeleteWithExactKey(key)));

        return result;
    }

    private boolean cacheDeleteWithExactKey(String key) {
        return redisClient.getBucket(key).delete();
    }

}
