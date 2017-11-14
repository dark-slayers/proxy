package person.liuxx.proxy.util;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.apache.hc.client5.http.protocol.ClientProtocolException;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.ResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import person.liuxx.util.base.StringUtil;

/**
 * @author 刘湘湘
 * @version 1.0.0<br>
 *          创建时间：2017年8月31日 下午3:56:39
 * @since 1.0.0
 */
public class SimpleResponseHandler implements ResponseHandler<Optional<String>>
{
    private String charsetName;

    @Override
    public Optional<String> handleResponse(ClassicHttpResponse response) throws HttpException,
            IOException
    {
        String result = null;
        final int status = response.getCode();
        if (status >= HttpStatus.SC_SUCCESS && status < HttpStatus.SC_REDIRECTION)
        {
            final HttpEntity entity = response.getEntity();
            Charset charset = StringUtil.isEmpty(charsetName) ? StandardCharsets.UTF_8
                    : Charset.forName(charsetName);
            result = entity != null ? EntityUtils.toString(entity, charset) : null;
        } else
        {
            throw new ClientProtocolException("Unexpected response status: " + status);
        }
        return Optional.ofNullable(result);
    }
}
