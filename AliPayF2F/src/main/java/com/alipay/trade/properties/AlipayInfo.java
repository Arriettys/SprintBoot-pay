package com.alipay.trade.properties;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
@Component
@ConfigurationProperties(prefix = "alipay")
@PropertySource("classpath:/alipayinfo.properties")
public class AlipayInfo
{
	protected Log log = LogFactory.getLog(getClass());
	
    private  String openApiDomain;   // 支付宝openapi域名
    private  String mcloudApiDomain;  // 支付宝mcloudmonitor域名
    private  String pid;             // 商户partner id
    private  String appid;           // 商户应用id

    private  String privateKey;      // RSA私钥，用于对商户请求报文加签
    private  String publicKey;       // RSA公钥，仅用于验证开发者网关
    private  String alipayPublicKey; // 支付宝RSA公钥，用于验签支付宝应答
    private  String signType;     // 签名类型  
    private  String format;		//数据格式，默认为json
    private  String charset;	//字符集，默认utf-8
    private  String notifyUrl;
    private  String appAuthToken;

    private  int maxQueryRetry;   // 最大查询次数
    private  long queryDuration;  // 查询间隔（毫秒）

    private  int maxCancelRetry;  // 最大撤销次数
    private  long cancelDuration; // 撤销间隔（毫秒）

    private  long heartbeatDelay ; // 交易保障线程第一次调度延迟（秒）
    private  long heartbeatDuration ; // 交易保障线程调度间隔（秒）
    
    @PostConstruct
    public void init() 
    {
        log.info(description());
    }
    
    public  String description() 
    {
        StringBuilder sb = new StringBuilder("Configs{");
        sb.append("支付宝openapi网关: ").append(openApiDomain).append("\n");
        if (StringUtils.isNotEmpty(mcloudApiDomain)) 
        {
            sb.append(", 支付宝mcloudapi网关域名: ").append(mcloudApiDomain).append("\n");
        }

        if (StringUtils.isNotEmpty(pid)) 
        {
            sb.append(", pid: ").append(pid).append("\n");
        }
        sb.append(", appid: ").append(appid).append("\n");

        sb.append(", 商户RSA私钥: ").append(getKeyDescription(privateKey)).append("\n");
        sb.append(", 商户RSA公钥: ").append(getKeyDescription(publicKey)).append("\n");
        sb.append(", 支付宝RSA公钥: ").append(getKeyDescription(alipayPublicKey)).append("\n");
        sb.append(", 签名类型: ").append(signType).append("\n");

        sb.append(", 查询重试次数: ").append(maxQueryRetry).append("\n");
        sb.append(", 查询间隔(毫秒): ").append(queryDuration).append("\n");
        sb.append(", 撤销尝试次数: ").append(maxCancelRetry).append("\n");
        sb.append(", 撤销重试间隔(毫秒): ").append(cancelDuration).append("\n");

        sb.append(", 交易保障调度延迟(秒): ").append(heartbeatDelay).append("\n");
        sb.append(", 交易保障调度间隔(秒): ").append(heartbeatDuration).append("\n");
        sb.append("}");
        return sb.toString();
    }
    
    private static String getKeyDescription(String key) 
    {
        int showLength = 6;
        if (StringUtils.isNotEmpty(key) && key.length() > showLength) 
        {
            return new StringBuilder(key.substring(0, showLength))
                    .append("******")
                    .append(key.substring(key.length() - showLength))
                    .toString();
        }
        return null;
    }

	public  String getOpenApiDomain()
	{
		return openApiDomain;
	}

	public String getMcloudApiDomain()
	{
		return mcloudApiDomain;
	}

	public void setMcloudApiDomain(String mcloudApiDomain)
	{
		this.mcloudApiDomain = mcloudApiDomain;
	}

	public String getPid()
	{
		return pid;
	}

	public void setPid(String pid)
	{
		this.pid = pid;
	}

	public String getAppid()
	{
		return appid;
	}

	public void setAppid(String appid)
	{
		this.appid = appid;
	}

	public String getPrivateKey()
	{
		return privateKey;
	}

	public void setPrivateKey(String privateKey)
	{
		this.privateKey = privateKey;
	}

	public String getPublicKey()
	{
		return publicKey;
	}

	public void setPublicKey(String publicKey)
	{
		this.publicKey = publicKey;
	}

	public String getAlipayPublicKey()
	{
		return alipayPublicKey;
	}

	public void setAlipayPublicKey(String alipayPublicKey)
	{
		this.alipayPublicKey = alipayPublicKey;
	}

	public String getSignType()
	{
		return signType;
	}

	public void setSignType(String signType)
	{
		this.signType = signType;
	}

	public int getMaxQueryRetry()
	{
		return maxQueryRetry;
	}

	public void setMaxQueryRetry(int maxQueryRetry)
	{
		this.maxQueryRetry = maxQueryRetry;
	}

	public long getQueryDuration()
	{
		return queryDuration;
	}

	public void setQueryDuration(long queryDuration)
	{
		this.queryDuration = queryDuration;
	}

	public int getMaxCancelRetry()
	{
		return maxCancelRetry;
	}

	public void setMaxCancelRetry(int maxCancelRetry)
	{
		this.maxCancelRetry = maxCancelRetry;
	}

	public long getCancelDuration()
	{
		return cancelDuration;
	}

	public void setCancelDuration(long cancelDuration)
	{
		this.cancelDuration = cancelDuration;
	}

	public long getHeartbeatDelay()
	{
		return heartbeatDelay;
	}

	public void setHeartbeatDelay(long heartbeatDelay)
	{
		this.heartbeatDelay = heartbeatDelay;
	}

	public long getHeartbeatDuration()
	{
		return heartbeatDuration;
	}

	public void setHeartbeatDuration(long heartbeatDuration)
	{
		this.heartbeatDuration = heartbeatDuration;
	}

	public void setOpenApiDomain(String openApiDomain)
	{
		this.openApiDomain = openApiDomain;
	}

	public String getFormat()
	{
		return format;
	}

	public void setFormat(String format)
	{
		this.format = format;
	}

	public String getCharset()
	{
		return charset;
	}

	public void setCharset(String charset)
	{
		this.charset = charset;
	}

	public String getNotifyUrl()
	{
		return notifyUrl;
	}

	public void setNotifyUrl(String notifyUrl)
	{
		this.notifyUrl = notifyUrl;
	}

	public String getAppAuthToken()
	{
		return appAuthToken;
	}

	public void setAppAuthToken(String appAuthToken)
	{
		this.appAuthToken = appAuthToken;
	}
	
}
