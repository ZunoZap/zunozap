package com.javazilla.chromium.listeners;

import com.javazilla.chromium.events.ResourceRequestEvent;

public interface ResourceRequestListener extends BrowserListener {

    public void onBeforeResourceLoad(ResourceRequestEvent event);

    @Override
    public default Class<?> getType() {
        return ResourceRequestListener.class;
    }

}