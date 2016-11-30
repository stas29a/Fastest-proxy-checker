package com.s29a.ProxyChecker;

import java.util.ArrayList;

/**
 * Created by xxx on 28.02.16.
 */
public interface IProxyContainer {
    void load();
    ArrayList<Proxy> extract();
}
