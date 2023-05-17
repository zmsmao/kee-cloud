package com.kee.common.reptiles.dev.concrete;

import com.kee.common.reptiles.dev.AbstractDevToolsBase;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v109.network.Network;

import java.util.Optional;

/**
 * <p>
 *     默认是只开启NetWork监听,如需开启其他,实现initDevtools即可
 * <p/>
 * @Description : Object
 * @author: zms
 */
public class ChromeDevTools extends AbstractDevToolsBase {

    private ChromeDriver chromeDriver;

    public ChromeDevTools() {
        super();
    }

    public ChromeDevTools(WebDriver driver) {
        super();
        initDevtools(driver);
    }

    @Override
    protected void initDevtools(WebDriver driver) {
        ChromeDriver chromeDriver = (ChromeDriver) driver;
        DevTools devTools = chromeDriver.getDevTools();
        devTools.createSession();
        devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
        this.devTools = devTools;
        this.chromeDriver = chromeDriver;
    }

    public void setDriver(ChromeDriver driver) {
        this.chromeDriver = driver;
    }

    public ChromeDriver getDriver() {
        return chromeDriver;
    }
}
