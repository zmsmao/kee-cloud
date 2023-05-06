package com.kee.common.core.constant;

/**
 * 缓存的key 常量
 * 
 * @author zms
 */
public class CacheConstants
{

    /**
     * oauth 缓存前缀
     */
    public static final String OAUTH_ACCESS = "oauth:access:";

    /**
     * oauth 客户端信息
     */
    public static final String CLIENT_DETAILS_KEY = "oauth:client:details";

    /**
     * 令牌自定义标识
     */
    public static final String HEADER = "Authorization";

    /**
     * 令牌前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * 权限缓存前缀
     */
    public final static String LOGIN_TOKEN_KEY = "login_tokens:";

    /**
     * 用户ID字段
     */
    public static final String DETAILS_USER_ID = "user_id";

    /**
     * 用户名字段
     */
    public static final String DETAILS_USERNAME = "username";

    /**
     * 授权信息字段
     */
    public static final String AUTHORIZATION_HEADER = "authorization";

    /**
     * 验证码前缀
     */
    public static final String DEFAULT_CODE_KEY = "code_key:";

    /**
     * 邮箱服务器地址
     */
    public static final String SMTP_IMAP_HOST = "smtp_imap_host";
    public static final String SYS_CONFIG_KEY = "SYS_CONFIG_KEY";
    public static final String SYS_DICT_KEY = "SYS_DICT_KEY";
}
