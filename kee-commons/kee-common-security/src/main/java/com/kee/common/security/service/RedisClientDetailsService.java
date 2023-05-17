package com.kee.common.security.service;

import com.kee.common.core.constant.CacheConstants;
import com.kee.common.core.constant.SecurityConstants;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;

import javax.sql.DataSource;

/**
 * 重写原生方法支持redis缓存
 *
 * @author zms
 */
public class RedisClientDetailsService extends JdbcClientDetailsService
{
    public RedisClientDetailsService(DataSource dataSource)
    {
        super(dataSource);
        // 设置通过 client_id 获取客户端信息的 sql
        super.setSelectClientDetailsSql(SecurityConstants.DEFAULT_SELECT_STATEMENT);
        // 设置获取所有客户端信息的 sql
        super.setFindClientDetailsSql(SecurityConstants.DEFAULT_FIND_STATEMENT);
    }

    @Override
    @Cacheable(value = CacheConstants.CLIENT_DETAILS_KEY, key = "#clientId", unless = "#result == null")
    public ClientDetails loadClientByClientId(String clientId)
    {
        return super.loadClientByClientId(clientId);
    }
}
