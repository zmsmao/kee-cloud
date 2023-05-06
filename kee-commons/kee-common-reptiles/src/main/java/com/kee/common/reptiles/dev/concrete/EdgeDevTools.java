package com.kee.common.reptiles.dev.concrete;

import com.kee.common.reptiles.dev.AbstractDevToolsBase;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v109.network.Network;
import org.openqa.selenium.edge.EdgeDriver;

import java.util.Optional;

/**
 * @Description : Object
 * @author: zms
 */
public class EdgeDevTools extends AbstractDevToolsBase {

    private EdgeDriver edgeDriver;

    public EdgeDevTools() {
        super();
    }

    public EdgeDevTools(WebDriver driver) {
        super();
        initDevtools(driver);
    }

    @Override
    protected void initDevtools(WebDriver driver) {
        EdgeDriver edgeDriver = (EdgeDriver) driver;
        DevTools devTools = edgeDriver.getDevTools();
        devTools.createSession();
        devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
        this.devTools = devTools;
        this.edgeDriver = edgeDriver;
    }

    public void setEdgeDriver(EdgeDriver edgeDriver) {
        this.edgeDriver = edgeDriver;
    }

    public EdgeDriver getEdgeDriver() {
        return edgeDriver;
    }
}
