package com.javazilla.chromium.listeners;

import com.javazilla.chromium.events.TitleEvent;

public interface TitleListener extends BrowserListener {

    public void onTitleChange(TitleEvent event);

    @Override
    public default Class<?> getType() {
        return TitleListener.class;
    }

}