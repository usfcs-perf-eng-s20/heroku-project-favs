package cs.usfca.edu.histfavcheckout.utils;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import redis.clients.jedis.JedisPoolConfig;

@ComponentScan(basePackages = {"cs.usfca.edu"})
@Configuration
public class RedisConfig {
	@Bean
    public RedisConnectionFactory jedisConnectionFactory() throws URISyntaxException{
		URI redisURI = new URI(System.getenv("REDIS_URL"));
	    JedisPoolConfig poolConfig = new JedisPoolConfig();
	    poolConfig.setMaxTotal(10);
	    poolConfig.setMaxIdle(5);
	    poolConfig.setMinIdle(1);
	    poolConfig.setTestOnBorrow(true);
	    poolConfig.setTestOnReturn(true);
	    poolConfig.setTestWhileIdle(true);
	    JedisClientConfiguration jedisClientConfiguration = JedisClientConfiguration.builder()
			   .usePooling().poolConfig(poolConfig)
			   .build();
	    RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(redisURI.getHost(), redisURI.getPort());
	    redisStandaloneConfiguration.setPassword(redisURI.getUserInfo().split(":",2)[1]);
	    JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(redisStandaloneConfiguration, jedisClientConfiguration);
	    return jedisConnectionFactory;
    }
	
	@Bean
	public RedisTemplate<Integer, Object> redisTemplate() throws URISyntaxException {
	    RedisTemplate<Integer, Object> template = new RedisTemplate<>();
	    template.setConnectionFactory(jedisConnectionFactory());
	    return template;
	}
}