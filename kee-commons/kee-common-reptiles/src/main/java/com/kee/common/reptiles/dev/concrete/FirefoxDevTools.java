package com.kee.common.reptiles.dev.concrete;

import com.kee.common.reptiles.dev.AbstractDevToolsBase;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v109.network.Network;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.Optional;

/**
 * @Description : Object
 * @author: zeng.maosen
 */
public class FirefoxDevTools extends AbstractDevToolsBase {

    private FirefoxDriver firefoxDriver;

    public FirefoxDevTools() {
        super();
    }

    public FirefoxDevTools(WebDriver driver) {
        super();
        initDevtools(driver);
    }

    @Override
    protected void initDevtools(WebDriver driver) {
        FirefoxDriver firefoxDriver = (FirefoxDriver) driver;
        DevTools devTools = firefoxDriver.getDevTools();
        devTools.createSession();
        devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
        this.devTools = devTools;
        this.firefoxDriver = firefoxDriver;
    }

    public void setFirefoxDriver(FirefoxDriver firefoxDriver) {
        this.firefoxDriver = firefoxDriver;
    }

    public FirefoxDriver getFirefoxDriver() {
        return firefoxDriver;
    }
}
