package person.liuxx.proxy.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author 刘湘湘
 * @version 1.0.0<br>
 *          创建时间：2017年9月4日 下午3:09:36
 * @since 1.0.0
 */
@Entity
@Table(name = "proxy_address")
public class ProxyAddressDO
{
    @Id
    @GeneratedValue
    private Long id;
    private String host;
    private int port;
    @Column(name = "last_usable_time")
    private LocalDateTime lastUsableTime;
    @Column(name = "last_test_url")
    private String lastTestUrl;
    @Column(name = "use_time")
    private Long useTime;

    public ProxyAddressDO()
    {
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getHost()
    {
        return host;
    }

    public void setHost(String host)
    {
        this.host = host;
    }

    public int getPort()
    {
        return port;
    }

    public void setPort(int port)
    {
        this.port = port;
    }

    public LocalDateTime getLastUsableTime()
    {
        return lastUsableTime;
    }

    public void setLastUsableTime(LocalDateTime lastUsableTime)
    {
        this.lastUsableTime = lastUsableTime;
    }

    public String getLastTestUrl()
    {
        return lastTestUrl;
    }

    public void setLastTestUrl(String lastTestUrl)
    {
        this.lastTestUrl = lastTestUrl;
    }

    public Long getUseTime()
    {
        return useTime;
    }

    public void setUseTime(Long useTime)
    {
        this.useTime = useTime;
    }
}
