package person.liuxx.proxy.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.apache.hc.client5.http.config.AuthSchemes;
import org.apache.hc.client5.http.config.CookieSpecs;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.sync.CloseableHttpClient;
import org.apache.hc.client5.http.impl.sync.HttpClients;
import org.apache.hc.client5.http.sync.methods.HttpGet;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.io.ResponseHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import person.liuxx.util.base.StringUtil;
import person.liuxx.util.log.LogUtil;

/**
 * @author 刘湘湘
 * @version 1.0.0<br>
 *          创建时间：2017年8月31日 下午4:06:26
 * @since 1.0.0
 */
public class HttpClientUtil
{
    private static Logger log = LogManager.getLogger();
    private static final long TIME_OUT = 15;

    public static Optional<String> simpleGet(String url)
    {
        try (CloseableHttpClient httpclient = HttpClients.createDefault())
        {
            final HttpGet httpget = new HttpGet(url);
            httpget.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)"
                    + " AppleWebKit/537.36 (KHTML, like Gecko)"
                    + " Chrome/60.0.3112.78 Safari/537.36");
            log.info("Executing request {} -> {}", httpget.getMethod(), httpget.getUri());
            final ResponseHandler<Optional<String>> responseHandler = new SimpleResponseHandler();
            Optional<String> responseBody = httpclient.execute(httpget, responseHandler);
            return responseBody;
        } catch (IOException | URISyntaxException e)
        {
            log.error(LogUtil.errorInfo(e));
        }
        return Optional.empty();
    }

    public static Optional<String> cookieGet(String url, String cookie)
    {
        try (CloseableHttpClient httpclient = HttpClients.createDefault())
        {
            final HttpGet httpget = new HttpGet(url);
            RequestConfig defaultRequestConfig = RequestConfig.custom()
                    .setCookieSpec(CookieSpecs.STANDARD)
                    .setExpectContinueEnabled(true)
                    .setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM,
                            AuthSchemes.DIGEST))
                    .setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC))
                    .build();
            RequestConfig requestConfig = RequestConfig.copy(defaultRequestConfig)
                    .setSocketTimeout(TIME_OUT, TimeUnit.SECONDS)
                    .setConnectTimeout(TIME_OUT, TimeUnit.SECONDS)
                    .setConnectionRequestTimeout(TIME_OUT, TimeUnit.SECONDS)
                    .build();
            httpget.setConfig(requestConfig);
            httpget.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
                    + "AppleWebKit/537.36 (KHTML, like Gecko) "
                    + "Chrome/60.0.3112.78 Safari/537.36");
            httpget.addHeader("Accept",
                    "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
            httpget.addHeader("Accept-Language", "zh-CN,zh;q=0.8");
            httpget.addHeader("Accept-Encoding", "gzip, deflate");
            if (!StringUtil.isEmpty(cookie))
            {
                httpget.addHeader("Cookie", cookie);
            }
            log.info("Executing request {} -> {}", httpget.getMethod(), httpget.getUri());
            final ResponseHandler<Optional<String>> responseHandler = new ResponseHandler<Optional<String>>()
            {
                @Override
                public Optional<String> handleResponse(ClassicHttpResponse response)
                        throws HttpException, IOException
                {
                    return null;
                }
            };
            Optional<String> op = httpclient.execute(httpget, responseHandler);
            if (op.isPresent())
            {
                return op;
            }
            httpget.addHeader("Cookie", cookie);
            Optional<String> op2 = httpclient.execute(httpget, responseHandler);
            if (op2.isPresent())
            {
                return op2;
            }
        } catch (IOException | URISyntaxException e)
        {
            log.error(LogUtil.errorInfo(e));
        }
        log.warn("无法获取服务器的正确响应！");
        return Optional.empty();
    }
}
