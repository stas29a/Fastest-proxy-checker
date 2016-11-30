package com.s29a.ProxyChecker;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by xxx on 28.02.16.
 */
public class UrlContainer implements IProxyContainer {
    protected ArrayList<String> url = new ArrayList<String>();
    protected ArrayList<Proxy> proxies = new ArrayList<Proxy>();
    final Pattern IP_PATTERN =
            Pattern.compile("([0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}):([0-9]{1,5})");

    public void addToLoading(String websiteUrl)
    {
        url.add(websiteUrl);
    }

    public void load() {
        for(String u : url)
        {
            loadUrl(u);
        }
    }

    public ArrayList<Proxy> extract() {
        return proxies;
    }

    protected void loadUrl(String url)
    {
        try {
            StringBuilder content = new StringBuilder();
            URL website = new URL(url);
            URLConnection connection = website.openConnection();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            connection.getInputStream()));

            String inputLine;

            while ((inputLine = in.readLine()) != null)
                content.append(inputLine);

            in.close();

            Matcher matcher = IP_PATTERN.matcher(content.toString());

            while (matcher.find())
            {
                String ip = matcher.group(1);
                String port = matcher.group(2);

                Proxy proxy = new Proxy(ip, port);
                proxies.add(proxy);
            }
        }
        catch (Throwable e)
        {
            System.out.println(e);
        }
    }
}
