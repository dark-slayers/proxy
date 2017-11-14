package person.liuxx.proxy.business;

import com.google.common.base.Objects;

/**
 * @author 刘湘湘
 * @version 1.0.0<br>
 *          创建时间：2017年8月29日 下午2:21:54
 * @since 1.0.0
 */
public class ProxyAddress implements Comparable<ProxyAddress>
{
    private final String host;
    private final int port;

    public ProxyAddress(String host, int port)
    {
        this.host = host;
        this.port = port;
    }

    public String getHost()
    {
        return host;
    }

    public int getPort()
    {
        return port;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((host == null) ? 0 : host.hashCode());
        result = prime * result + port;
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ProxyAddress other = (ProxyAddress) obj;
        if (host == null)
        {
            if (other.host != null)
                return false;
        } else if (!host.equals(other.host))
            return false;
        if (port != other.port)
            return false;
        return true;
    }

    @Override
    public String toString()
    {
        return "ProxyAddress [host=" + host + ", port=" + port + "]";
    }

    @Override
    public int compareTo(ProxyAddress o)
    {
        if (Objects.equal(host, o.getHost()))
        {
            return ((Integer) port).compareTo(o.getPort());
        } else
        {
            return host.compareTo(o.getHost());
        }
    }
}
