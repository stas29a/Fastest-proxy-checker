package com.s29a.ProxyChecker;

import java.sql.Time;
import java.util.*;

import com.ning.http.client.*;
import com.ning.http.client.providers.netty.request.timeout.TimeoutTimerTask;

import java.util.concurrent.*;

public class Checker
{
    protected ArrayList<Proxy> availProxyList = new ArrayList<Proxy>();
    protected IProxyContainer proxyContainer;
    protected int checkedProxyCount = 0;
    protected Object isTaskCompleted = new Object();
    protected final int maxConnections = 10000;

    public Future<?> check()
    {
            final ExecutorService executorService = Executors.newFixedThreadPool(1);
            return executorService.submit(new Runnable() {
                protected void processPart(ArrayList<Proxy> proxies, int indexFrom, int indexTo)
                {
                    AsyncHttpClient httpClient = new AsyncHttpClient();
                    ArrayList<ListenableFuture<Response>> responses = new ArrayList<ListenableFuture<Response>>(10);
                    Map<ListenableFuture<Response>, Proxy> results = new HashMap<ListenableFuture<Response>, Proxy>();
                    int totalProxyCount = proxies.size();

                    for(int i=indexFrom; i <= indexTo && i < totalProxyCount; i++)
                    {
                        Proxy proxy = proxies.get(i);
                        ProxyServer proxyServer = new ProxyServer(proxy.getIp(), Integer.parseInt(proxy.getPort()));
                        Request request = new RequestBuilder("GET").
                                setProxyServer(proxyServer).
                                setUrl("https://yandex.com/internet").
                                setFollowRedirects(true).
                                setRequestTimeout(10 * 1000). //10 sec
                                build();

                        ListenableFuture<Response> response = httpClient.executeRequest(request);
                        responses.add(response);
                        results.put(response, proxy);
                        System.out.println("Execute request through " + proxy.getIp());
                    }


                        Iterator<ListenableFuture<Response>> iterator = responses.iterator();
                        while (iterator.hasNext()) {
                            try {
                                System.out.println("Get next waiting for response");
                                ListenableFuture<Response> response = iterator.next();
                                Response r = response.get();
                                System.out.println("Got response");
                                if (r != null) {
                                    if (r.getStatusCode() == 200) {
                                        availProxyList.add(results.get(response));
                                        System.out.println("Good proxy: " + results.get(response).getIp());
                                    } else {
                                        System.out.println("Bad proxy: " + results.get(response).getIp());
                                    }

                                    //responses.remove(currentIndex);
                                    checkedProxyCount++;
                                    iterator.remove();
                                }
                            }
                            catch (Throwable e) {
                                System.out.println("Got exception while taking response");
                                System.out.println(e);
                                iterator.remove();
                                checkedProxyCount++;
                            }
                        }

                    httpClient.close();
                }

                public void run() {
                    try {
                        synchronized (isTaskCompleted) {
                            proxyContainer.load();
                            ArrayList<Proxy> proxies = proxyContainer.extract();
                            int proxiesCount = proxies.size();

                            for(int i = 0; i< proxiesCount; i+= maxConnections)
                            {
                                processPart(proxies, i, i+ maxConnections);
                            }
                        }

                        System.out.println("Checking is complete");
                    }
                    catch (Throwable e)
                    {
                        System.out.print(e.getMessage());
                        e.printStackTrace();
                        //throw new RuntimeException(e);
                    }
                    executorService.shutdownNow();
                }
            });
    }

    public void setProxyContainer(IProxyContainer proxyContainer) {
        this.proxyContainer = proxyContainer;
    }

    public ArrayList<Proxy> getAvailProxyList()
    {
        synchronized (isTaskCompleted)
        {
            return availProxyList;
        }
    }
}