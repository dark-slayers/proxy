package person.liuxx.proxy.business;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.apache.hc.client5.http.config.AuthSchemes;
import org.apache.hc.client5.http.config.CookieSpecs;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.sync.CloseableHttpClient;
import org.apache.hc.client5.http.impl.sync.HttpClients;
import org.apache.hc.client5.http.protocol.ClientProtocolException;
import org.apache.hc.client5.http.sync.methods.HttpGet;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.ResponseHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import person.liuxx.util.log.LogUtil;

/**
 * @author 刘湘湘
 * @version 1.0.0<br>
 *          创建时间：2017年8月29日 下午2:26:10
 * @since 1.0.0
 */
public class ProxyTestTask implements Callable<ProxyTestResult>
{
    private Logger log = LogManager.getLogger();
    private ProxyAddress address;
    private String targetAddress;
    private final long TIME_OUT = 15;

    public ProxyTestTask(ProxyAddress address, String targetAddress)
    {
        this.address = address;
        this.targetAddress = targetAddress;
    }

    @Override
    public ProxyTestResult call() throws Exception
    {
        log.traceEntry();
        long startTime = System.nanoTime();
        try (CloseableHttpClient httpclient = HttpClients.createDefault())
        {
            final HttpGet httpget = new HttpGet(targetAddress);
            RequestConfig defaultRequestConfig = RequestConfig.custom()
                    .setCookieSpec(CookieSpecs.DEFAULT)
                    .setExpectContinueEnabled(true)
                    .setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM,
                            AuthSchemes.DIGEST))
                    .setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC))
                    .build();
            RequestConfig requestConfig = RequestConfig.copy(defaultRequestConfig)
                    .setSocketTimeout(TIME_OUT, TimeUnit.SECONDS)
                    .setConnectTimeout(TIME_OUT, TimeUnit.SECONDS)
                    .setConnectionRequestTimeout(TIME_OUT, TimeUnit.SECONDS)
                    .setProxy(new HttpHost(address.getHost(), address.getPort()))
                    .build();
            httpget.setConfig(requestConfig);
            httpget.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)"
                    + " AppleWebKit/537.36 (KHTML, like Gecko)"
                    + " Chrome/60.0.3112.78 Safari/537.36");
            log.info("Executing request {} -> {}", httpget.getMethod(), httpget.getUri());
            final ResponseHandler<Long> responseHandler = new ResponseHandler<Long>()
            {
                @Override
                public Long handleResponse(final ClassicHttpResponse response) throws IOException
                {
                    final int status = response.getCode();
                    if (status >= HttpStatus.SC_SUCCESS && status < HttpStatus.SC_REDIRECTION)
                    {
                        long endTime = System.nanoTime() - startTime;
                        return endTime / 1_000_000;
                    } else
                    {
                        throw new ClientProtocolException("Unexpected response status: " + status);
                    }
                }
            };
            long time = httpclient.execute(httpget, responseHandler);
            log.traceExit();
            return new ProxyTestResult(address, time);
        } catch (IOException | URISyntaxException e)
        {
            log.error("测试未通过，测试的代理地址：{}，目标地址：{}", address, targetAddress);
            log.error(LogUtil.errorInfo(e));
        }
        log.warn("测试未通过 !");
        return new ProxyTestResult(address, -1000);
    }
}
