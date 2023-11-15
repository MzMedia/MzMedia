package com.mz.network.authority;


public interface DeviceAuthorityService {
    boolean verification(String clientId, String username, String password);
}
