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

import javafx.scene.Node;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Default dialog layout according to material design guidelines.
 *
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2016-03-09
 */
public class JFXDialogLayout extends VBox {

    private StackPane heading = new StackPane();
    private StackPane body = new StackPane();
    private FlowPane actions = new FlowPane();

    public JFXDialogLayout() {
        getStyleClass().add("jfx-dialog-layout");
        heading.getStyleClass().addAll("jfx-layout-heading", "title");
        body.getStyleClass().add("jfx-layout-body");
        VBox.setVgrow(body, Priority.ALWAYS);
        actions.getStyleClass().add("jfx-layout-actions");
        getChildren().setAll(heading, body, actions);
    }

    public void heading(Node... n) { heading.getChildren().setAll(n); }
    public void body(Node... n)    { body.getChildren().setAll(n);    }
    public void actions(Node... n) { actions.getChildren().setAll(n); }

    @Override
    public String getUserAgentStylesheet() {
        return this.getClass().getClassLoader().getResource("jfx-dialog-layout.css").toExternalForm();
    }

}
