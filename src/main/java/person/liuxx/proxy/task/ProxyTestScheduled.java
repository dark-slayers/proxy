package person.liuxx.proxy.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import person.liuxx.proxy.service.ProxyService;

/**
 * @author 刘湘湘
 * @version 1.0.0<br>
 *          创建时间：2017年8月30日 下午4:31:36
 * @since 1.0.0
 */
@Service
public class ProxyTestScheduled
{
    private static final int TEN_MINUTES = 1000 * 60 * 10;
    private static final int ONE_HOUR = 1000 * 60 * 60;
    private static final int THREE_HOURS = 1000 * 60 * 60 * 3;
    @Autowired
    private ProxyService proxyService;

    @Scheduled(fixedRate = TEN_MINUTES)
    public void flushAddressList()
    {
        proxyService.flushAddressList();
    }

    @Scheduled(fixedRate = ONE_HOUR)
    public void testCacheAddressList()
    {
        proxyService.testCacheAddressList();
    }

    @Scheduled(fixedRate = THREE_HOURS)
    public void testDatabaseAddressList()
    {
        proxyService.testDatabaseAddressList();
    }
}
