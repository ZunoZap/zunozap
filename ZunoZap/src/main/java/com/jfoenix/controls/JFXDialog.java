/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.jfoenix.controls;

import javafx.beans.DefaultProperty;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

/**
 * Note: for JFXDialog to work properly, the root node <b>MUST</b>
 * be of type {@link StackPane}
 *
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2016-03-09
 */
@DefaultProperty(value = "content")
public class JFXDialog extends StackPane {

    private StackPane contentHolder;

    private StackPane dialogContainer;
    private Region content;
    private boolean overlayClose;

    EventHandler<? super MouseEvent> closeHandler = e -> close();

    public JFXDialog(StackPane dialogContainer, Region content) {
        this(dialogContainer, content, true);
    }

    public JFXDialog(StackPane dialogContainer, Region content, boolean overlayClose) {
        this.overlayClose = overlayClose;
        initialize();
        this.content = content;
        this.content.setPickOnBounds(false);
        contentHolder.getChildren().setAll(content);
        this.dialogContainer = dialogContainer;
    }

    private void initialize() {
        this.getStyleClass().add("jfx-dialog");

        contentHolder = new StackPane();
        contentHolder.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(2), null)));
        contentHolder.setPickOnBounds(false);
        contentHolder.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        this.getChildren().add(contentHolder);
        this.getStyleClass().add("jfx-dialog-overlay-pane");
        StackPane.setAlignment(contentHolder, Pos.CENTER);
        this.setBackground(new Background(new BackgroundFill(Color.rgb(0,0,0,.1), null, null)));
        if (overlayClose)
            this.addEventHandler(MouseEvent.MOUSE_PRESSED, closeHandler);
        contentHolder.addEventHandler(MouseEvent.ANY, e -> e.consume());
    }

    public void show() {
        dialogContainer.getChildren().remove(this);
        dialogContainer.getChildren().add(this);
    }

    public void close() {
        dialogContainer.getChildren().remove(this);
    }

}