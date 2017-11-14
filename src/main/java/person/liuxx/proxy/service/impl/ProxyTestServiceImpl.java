package person.liuxx.proxy.service.impl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import person.liuxx.proxy.business.ProxyAddress;
import person.liuxx.proxy.business.ProxyTestResult;
import person.liuxx.proxy.business.ProxyTestTask;
import person.liuxx.proxy.dao.ProxyAddressRepository;
import person.liuxx.proxy.domain.ProxyAddressDO;
import person.liuxx.proxy.service.ProxyService;
import person.liuxx.proxy.source.ProxySource;
import person.liuxx.util.base.StringUtil;
import person.liuxx.util.log.LogUtil;

/**
 * @author 刘湘湘
 * @version 1.0.0<br>
 *          创建时间：2017年8月30日 下午1:59:27
 * @since 1.0.0
 */
@Service
public class ProxyTestServiceImpl implements ProxyService
{
    private Logger log = LogManager.getLogger();
    @Autowired
    private ProxyAddressRepository proxyAddressDao;
    private static AtomicBoolean testCacheIsRunning = new AtomicBoolean(false);
    private static AtomicBoolean testDatabaseIsRunning = new AtomicBoolean(false);
    private static AtomicBoolean addressIsFlushing = new AtomicBoolean(false);
    private static CountDownLatch hasList = new CountDownLatch(1);
    private static Set<ProxyAddress> addressSet = new ConcurrentSkipListSet<ProxyAddress>();
    private static Set<ProxyAddress> databaseAddressSet = new ConcurrentSkipListSet<ProxyAddress>();
    private static final String DEFAULT_TARGET_URL = "https://www.facebook.com/";
    private String targetUrl;

    @Override
    public List<ProxyAddress> listAddress()
    {
        return proxyAddressDao.findAll().stream().filter(p ->
        {
            return p.getLastUsableTime().until(LocalDateTime.now(), ChronoUnit.HOURS) < 24;
        }).map(p ->
        {
            return new ProxyAddress(p.getHost(), p.getPort());
        }).collect(Collectors.toList());
    }

    @Override
    public void testCacheAddressList()
    {
        try
        {
            log.info("等待获取最新的代理列表...");
            hasList.await();
        } catch (InterruptedException e1)
        {
            log.error(LogUtil.errorInfo(e1));
        }
        if (testCacheIsRunning.compareAndSet(false, true))
        {
            log.info("开始验证缓存代理列表...");
            try
            {
                testAddressList(addressSet);
            } catch (InterruptedException | ExecutionException e)
            {
                log.error(LogUtil.errorInfo(e));
            } finally
            {
                testCacheIsRunning.set(false);
            }
        }
    }

    @Override
    public void flushAddressList()
    {
        if (addressIsFlushing.compareAndSet(false, true))
        {
            log.info("刷新缓存的代理地址列表...");
            ProxySource.allSource().flatMap(s -> s.getProxyAddressStream()).forEach(a -> addressSet
                    .add(a));
            addressIsFlushing.set(false);
            hasList.countDown();
            log.info("缓存的代理地址列表刷新完毕！");
            log.info("最新获取的代理地址列表长度：{}", addressSet.size());
        }
    }

    @Override
    public void testDatabaseAddressList()
    {
        if (testDatabaseIsRunning.compareAndSet(false, true))
        {
            log.info("开始验证数据库代理列表...");
            databaseAddressSet.clear();
            proxyAddressDao.findAll().stream().map(p ->
            {
                return new ProxyAddress(p.getHost(), p.getPort());
            }).forEach(a -> databaseAddressSet.add(a));
            try
            {
                testAddressList(databaseAddressSet);
                proxyAddressDao.findAll().stream().forEach(p ->
                {
                    long days = p.getLastUsableTime().until(LocalDateTime.now(), ChronoUnit.DAYS);
                    if (days > 10)
                    {
                        proxyAddressDao.delete(p);
                    }
                });
            } catch (InterruptedException | ExecutionException e)
            {
                log.error(LogUtil.errorInfo(e));
            } finally
            {
                testDatabaseIsRunning.set(false);
            }
        }
    }

    private void testAddressList(Set<ProxyAddress> set) throws InterruptedException,
            ExecutionException
    {
        log.info("运行代理列表的有效性测试...");
        ExecutorService executor = Executors.newFixedThreadPool(30);
        CompletionService<ProxyTestResult> service = new ExecutorCompletionService<ProxyTestResult>(
                executor);
        for (final ProxyAddress p : set)
        {
            service.submit(new ProxyTestTask(p, getTargetUrl()));
        }
        for (int i = 0, max = set.size(); i < max; i++)
        {
            Future<ProxyTestResult> f = service.take();
            ProxyTestResult r = f.get();
            if (r.getTime() > 0)
            {
                Optional<ProxyAddressDO> optional = proxyAddressDao.findByHostAndPort(r.getAddress()
                        .getHost(), r.getAddress().getPort());
                if (optional.isPresent())
                {
                    proxyAddressDao.setLastTestUrlAndLastUsableTimeAndUseTimeById(getTargetUrl(),
                            LocalDateTime.now(), r.getTime(), optional.get().getId());
                } else
                {
                    ProxyAddressDO address = new ProxyAddressDO();
                    address.setHost(r.getAddress().getHost());
                    address.setPort(r.getAddress().getPort());
                    address.setLastTestUrl(getTargetUrl());
                    address.setLastUsableTime(LocalDateTime.now());
                    address.setUseTime(r.getTime());
                    proxyAddressDao.save(address);
                }
            }
        }
        log.info("代理列表的有效性测试完毕！");
    }

    public String getTargetUrl()
    {
        return StringUtil.isEmpty(targetUrl) ? DEFAULT_TARGET_URL : targetUrl;
    }

    public void setTargetUrl(String targetUrl)
    {
        this.targetUrl = targetUrl;
    }
}
