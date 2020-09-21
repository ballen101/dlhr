package com.corsair.server.csession;

import java.util.HashMap;
import java.util.Map;

import com.corsair.server.base.ConstsSw;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Redis工具类
 * 
 * @author Administrator
 *
 */
public class RedisUtil {
	private static Map<String, JedisPool> maps = new HashMap<String, JedisPool>();

	private static JedisPool getPool(String ip, int port) {
		String key = ip + ":" + port;
		JedisPool pool = null;
		if (!maps.containsKey(key)) {
			try {
				pool = new JedisPool(new JedisPoolConfig(), ip, port);
				maps.put(key, pool);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			pool = maps.get(key);
		}
		return pool;
	}

	public static void setfdexpire(String field, int secends) throws Exception {

		Jedis jds = null;
		try {
			jds = getJedis(ConstsSw._redis_ip, ConstsSw._redis_port);
			jds.expire(field, ConstsSw._redis_timeout * 60);
		} finally {
			if (jds != null)
				jds.close();
		}
	}

	public static void settokenfdvalue(String token, String field, String value) throws Exception {
		Jedis jds = null;
		try {
			jds = getJedis(ConstsSw._redis_ip, ConstsSw._redis_port);
			jds.hset(token, field, value);
		} finally {
			if (jds != null)
				jds.close();
		}
	}

	public static String gettokefdvalue(String token, String field) throws Exception {
		Jedis jds = null;
		try {
			jds = getJedis(ConstsSw._redis_ip, ConstsSw._redis_port);
			String rst = jds.hget(token, field);
			return rst;
		} finally {
			if (jds != null)
				jds.close();
		}
	}

	public static String getfdvalue(String sessionkey, String field) throws Exception {
		Jedis jds = null;
		try {
			jds = getJedis(ConstsSw._redis_ip, ConstsSw._redis_port);
			String rst = jds.hget(sessionkey, field);
			jds.expire(sessionkey, ConstsSw._redis_timeout * 60);
			return rst;
		} finally {
			if (jds != null)
				jds.close();
		}
	}

	private static Jedis getJedis(String ip, int port) {
		return getPool(ip, port).getResource();
	}

	private static void closeJedis(Jedis jedis) {
		jedis.close();
	}
}
