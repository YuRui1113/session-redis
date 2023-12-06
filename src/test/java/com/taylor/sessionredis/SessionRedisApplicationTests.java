package com.taylor.sessionredis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import redis.clients.jedis.Jedis;

@SpringBootTest
class SessionRedisApplicationTests {

	private Jedis jedis;
	private TestRestTemplate httpClient;
	private TestRestTemplate httpClientWithAuth;

	private static final String REDIS_HOST = "192.168.137.40";
	private static final int REDIS_PORT = 6379;
	private static final String URL_TEST_SESSION = "http://localhost:8080/api/v1/session";
	private static final String URL_TEST_SESSION_HELLO = URL_TEST_SESSION + "/hello";
	private static final String URL_TEST_SESSION_DATA = URL_TEST_SESSION + "/data";
	private static final String EXPECTED_API_RESULT_HELLO = "Hello, World!";
	private static final String EXPECTED_API_RESULT_OK = "OK";

	@BeforeEach
	public void init() {
		httpClient = new TestRestTemplate(TestRestTemplate.HttpClientOption.ENABLE_COOKIES);
		httpClientWithAuth = new TestRestTemplate("admin", "password",
				TestRestTemplate.HttpClientOption.ENABLE_COOKIES);

		jedis = new Jedis(REDIS_HOST, REDIS_PORT);
		jedis.flushAll();
	}

	@Test
	public void testRedisIsEmpty() {
		Set<String> result = jedis.keys("*");
		assertEquals(0, result.size());
	}

	@Test
	public void testRedisControlsSession() {
		// Test Redis is empty at the beginning
		Set<String> redisKeys = jedis.keys("*");
		assertEquals(0, redisKeys.size());

		// Test authorization
		ResponseEntity<String> result = httpClientWithAuth.getForEntity(URL_TEST_SESSION_HELLO, String.class);
		assertEquals(EXPECTED_API_RESULT_HELLO, result.getBody()); // Login worked

		// Test authentication data stored in session by Redis
		redisKeys = jedis.keys("*");
		assertTrue(redisKeys.size() > 0); // Redis is populated with session data

		// Get session info for next request
		String sessionCookie = result.getHeaders().get("Set-Cookie").get(0).split(";")[0];
		HttpHeaders headers = new HttpHeaders();
		headers.add("Cookie", sessionCookie);
		HttpEntity<String> httpEntitySession = new HttpEntity<>(headers);

		// Test accessing anonymous
		result = httpClient.getForEntity(URL_TEST_SESSION_HELLO, String.class);
		assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());

		// Test accessing anonymous but with authentication session data
		result = httpClient.exchange(URL_TEST_SESSION_HELLO,
				HttpMethod.GET,
				httpEntitySession,
				String.class);
		assertEquals(EXPECTED_API_RESULT_HELLO, result.getBody());

		// clear all keys in Redis
		jedis.flushAll();

		// Test accessing denied after sessions are removed in Redis
		result = httpClient.exchange(URL_TEST_SESSION_HELLO,
				HttpMethod.GET,
				httpEntitySession,
				String.class);
		assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
	}

	@Test
	public void testSetAndGetSession() {
		String sessionKey = "session-key";
		String sessionValue = "session-value";

		// Test manually storing data in session by Redis
		ResponseEntity<String> result = httpClientWithAuth.exchange(URL_TEST_SESSION_DATA + "/" + sessionKey,
				HttpMethod.PUT,
				new HttpEntity<>(sessionValue),
				String.class);
		assertEquals(EXPECTED_API_RESULT_OK, result.getBody());

		String sessionCookie = result.getHeaders().get("Set-Cookie").get(0).split(";")[0];
		HttpHeaders headers = new HttpHeaders();
		headers.add("Cookie", sessionCookie);
		HttpEntity<String> httpEntity = new HttpEntity<>(headers);

		// Test retrieving data from session
		result = httpClientWithAuth.exchange(URL_TEST_SESSION_DATA + "/" + sessionKey,
				HttpMethod.GET,
				// Must include previous session info, or else server side can't know its
				// session object
				httpEntity,
				String.class);
		assertEquals(sessionValue, result.getBody());
	}

	@Test
	public void testJedisOperateString() {
		String key = "events/student/taylor";
		String value = "1,3,5,7";

		jedis.set(key, value);
		String cachedResponse = jedis.get(key);

		assertEquals(value, cachedResponse);
	}

	@Test
	public void testJedisOperateList() {
		String key = "queue#tasks";
		String value = "firstTask";

		jedis.lpush(key, "firstTask");
		jedis.lpush(key, "secondTask");

		String task = jedis.rpop(key);

		assertEquals(value, task);
	}

	@Test
	public void testJedisOperateSet() {
		String key = "nicknames";

		jedis.sadd(key, "nickname#1");
		jedis.sadd(key, "nickname#2");
		jedis.sadd(key, "nickname#1");

		Set<String> nicknames = jedis.smembers(key);
		assertEquals(2, nicknames.size());

		boolean exists = jedis.sismember("nicknames", "nickname#1");
		assertTrue(exists);
	}

	@Test
	public void testJedisOperateHash() {
		jedis.hset("user#1", "name", "Peter");
		jedis.hset("user#1", "job", "politician");

		String name = jedis.hget("user#1", "name");
		assertEquals("Peter", name);

		Map<String, String> fields = jedis.hgetAll("user#1");
		String job = fields.get("job");
		assertEquals("politician", job);
	}

	@Test
	public void testJedisOperateSortedSet() {
		String key = "ranking";
		String value = "firstTask";

		Map<String, Double> scores = new HashMap<>();

		scores.put("PlayerOne", 3000.0);
		scores.put("PlayerTwo", 1500.0);
		scores.put("PlayerThree", 8200.0);

		scores.entrySet().forEach(playerScore -> {
			jedis.zadd(key, playerScore.getValue(), playerScore.getKey());
		});

		String player = jedis.zrevrange(key, 0, 1).iterator().next();
		assertEquals("PlayerThree", player);
		long rank = jedis.zrevrank(key, "PlayerOne");
		assertEquals(1, rank);
	}
}