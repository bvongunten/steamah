package ch.nostromo.steamah.steam;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import javafx.concurrent.Task;

/**
 * Abstract steam api json loader
 * 
 * @author Bernhard von Gunten <bvg@nostromo.ch>
 *
 */
public abstract class AbstractLoader<T> extends Task<T> {

    private static final Logger LOGGER = Logger.getLogger(AbstractLoader.class.getName()); 
    
    private HttpClient httpClient;

    private String key;
    private String steamId;

    private String language;

    public AbstractLoader(String key, String steamId, String language) {
        this.key = key;
        this.steamId = steamId;
        this.language = language;
    }

    /**
     * Lazy http client initialization.
     */
    protected void getHttpClient() {
        if (httpClient == null) {
            httpClient = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).build();
        }
    }

    /**
     * Returns a json from a URL. If the http response code is 400 (bad request), null is returned as steam tends to be inconsistent on having
     * achievements on a game.
     * 
     * @param url
     * @return
     * @throws MalformedURLException
     * @throws IOException
     */
    protected String loadJson(String url) throws MalformedURLException, IOException {
        LOGGER.debug("Loading json from url: " + url);
        
        getHttpClient();

        HttpGet httpGet = new HttpGet(url);
        HttpResponse response = httpClient.execute(httpGet);
        HttpEntity enitity = response.getEntity();

        if (response.getStatusLine().getStatusCode() == 400) {
            LOGGER.warn("Url " + url + " failed with error 400, content ignored");
            EntityUtils.consume(enitity);
            return null;
        }

        String result = EntityUtils.toString(enitity, "UTF-8");
        EntityUtils.consume(enitity);

        return result;
    }

    protected abstract T call() throws Exception;

    protected Boolean convertToBooleanValue(String value) {
        if (value == null) {
            return Boolean.FALSE;
        } else if (value.equals("1")) {
            return Boolean.TRUE;
        } else if (value.equalsIgnoreCase("true")) {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    protected Integer convertToIntegerValue(String value) {
        if (value == null) {
            return new Integer(0);
        } else {
            return Integer.valueOf(value);
        }
    }

    protected String convertToStringValue(String value) {
        if (value == null) {
            return "";
        } else {
            return value;
        }
    }

    protected String convertToDateStringValue(String value) {
        if (value == null) {
            return "";
        } else if (value.equals("0")) {
            return "";
        } else {
            try {
                Date date = new Date(Long.valueOf(value).longValue() * 1000);
                return new SimpleDateFormat("yyyy.MM.dd hh:mm:ss").format(date);
                                                
            } catch (Exception ignored) {
                LOGGER.warn("Unable to process date " + value);
                return "";
            }
        }
    }

    protected String getKey() {
        return key;
    }

    public String getSteamId() {
        return steamId;
    }

    public String getLanguage() {
        return language;
    }

}
