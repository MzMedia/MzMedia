package com.mz.network.client;


public interface ClientRepository {
    Client getClient(String idOrClientId);

    Client register(Client client);

    Client unregister(String idOrClientId);

    long total();
}
