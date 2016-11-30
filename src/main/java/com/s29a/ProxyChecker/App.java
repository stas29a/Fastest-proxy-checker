package com.s29a.ProxyChecker;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.concurrent.Future;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        try {
            IProxyContainer proxyContainer;
            Checker checker = new Checker();
            CliArgs cliArgs = new CliArgs(args);

            String command = cliArgs.switchValue("-command");

            if(command == null)
            {
                System.out.println("Command not given");
                return;
            }

            switch (command) {
                case "check": {
                    String fileName = cliArgs.switchValue("-proxy-file");

                    if(fileName == null)
                    {
                        System.out.println("No proxy file given");
                        return;
                    }

                    proxyContainer = new FileContainer(fileName);
                    break;
                }
                case "parse-and-check": {
                    String urls[] = cliArgs.switchValues("-urls");
                    UrlContainer container = new UrlContainer();

                    for(String u : urls)
                    {
                        container.addToLoading(u);
                    }

                    proxyContainer = container;
                    break;
                }
                default:{
                    System.out.println("Unknown command given");
                    return;
                }
            }

            checker.setProxyContainer(proxyContainer);
            Future<?> isCompleted = checker.check();
            isCompleted.get();

            ArrayList<Proxy>  goodProxies = checker.getAvailProxyList();
            String saveTo = cliArgs.switchValue("-s");

            if(saveTo != null)
            {
                Path file = Paths.get(saveTo);
                StringBuilder content = new StringBuilder();

                for(Proxy proxy : goodProxies)
                {
                    content.append(proxy.getIp() + ":" + proxy.getPort() + "\n");
                }

                if(!Files.exists(file))
                    Files.createFile(file);

                Files.write(file, content.toString().getBytes(), StandardOpenOption.APPEND);
                System.out.println("Saved to file " + saveTo);
            }

            System.out.println("Good proxies: " + goodProxies.size());
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }
}
