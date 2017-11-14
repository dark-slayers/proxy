package person.liuxx.proxy.business;

/**
 * @author 刘湘湘
 * @version 1.0.0<br>
 *          创建时间：2017年8月29日 下午2:24:44
 * @since 1.0.0
 */
public class ProxyTestResult
{
    private ProxyAddress address;
    private long time;

    public ProxyTestResult(ProxyAddress address, long time)
    {
        this.address = address;
        this.time = time;
    }

    public ProxyAddress getAddress()
    {
        return address;
    }

    public void setAddress(ProxyAddress address)
    {
        this.address = address;
    }

    public long getTime()
    {
        return time;
    }

    public void setTime(long time)
    {
        this.time = time;
    }

    @Override
    public String toString()
    {
        return "ProxyTestResult [address=" + address + ", time=" + time + "]";
    }
    
}
