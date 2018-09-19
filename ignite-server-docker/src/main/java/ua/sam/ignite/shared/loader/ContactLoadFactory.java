package ua.sam.ignite.shared.loader;

import javax.cache.configuration.Factory;

public class ContactLoadFactory implements Factory<ContactLoadAdaptor> {

    @Override
    public ContactLoadAdaptor create() {
        return new ContactLoadAdaptor();
    }
}
