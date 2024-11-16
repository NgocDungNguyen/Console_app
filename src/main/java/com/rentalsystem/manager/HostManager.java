package com.rentalsystem.manager;

import com.rentalsystem.model.Host;
import java.util.List;

public interface HostManager {
    void addHost(Host host);
    void updateHost(Host host);
    void deleteHost(String hostId);
    Host getHost(String hostId);
    List<Host> getAllHosts();
    List<Host> searchHosts(String keyword);
}