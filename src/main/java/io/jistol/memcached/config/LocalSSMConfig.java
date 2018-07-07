package io.jistol.memcached.config;

import com.google.code.ssm.CacheFactory;
import com.google.code.ssm.config.AbstractSSMConfiguration;
import com.google.code.ssm.config.DefaultAddressProvider;
import com.google.code.ssm.providers.xmemcached.MemcacheClientFactoryImpl;
import com.google.code.ssm.providers.xmemcached.XMemcachedConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LocalSSMConfig extends AbstractSSMConfiguration {
    @Value("${mem-server}") private String memServer;

    @Override
    public CacheFactory defaultMemcachedClient() {
        final XMemcachedConfiguration conf = new XMemcachedConfiguration();
        conf.setUseBinaryProtocol(true);

        final CacheFactory cf = new CacheFactory();
        cf.setCacheClientFactory(new MemcacheClientFactoryImpl());
        cf.setAddressProvider(new DefaultAddressProvider(memServer));
        cf.setConfiguration(conf);
        return cf;
    }
}
