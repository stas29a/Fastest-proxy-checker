package com.s29a.ProxyChecker;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by xxx on 28.02.16.
 */
public class FileContainer  implements IProxyContainer {
    protected String fileName;
    protected ArrayList<Proxy> proxies = new ArrayList<Proxy>();

    public FileContainer(String fileName)
    {
        this.fileName = fileName;
    }

    public void load()
    {
        ArrayList<String> proxyData = new ArrayList<String>();
        File file = new File(fileName);
        FileInputStream fis = null;
        BufferedReader br= null;

        try {
            fis = new FileInputStream(file);
            br = new BufferedReader(new InputStreamReader(fis));
            String tmp = null;
            while ((tmp = br.readLine()) != null) {
                proxyData.add(tmp);
            }
            fis.close();
            br.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String ip,port;
        for (int i = 0;i<proxyData.size();i++){
            String[] stuff = proxyData.get(i).split(":");

            if(stuff.length != 2)
                continue;

            ip = stuff[0];
            port = stuff[1];
            proxies.add(new Proxy(ip,port));
        }
    }

    public ArrayList<Proxy> extract()
    {
        return proxies;
    }
}
