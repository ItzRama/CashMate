package ramadevs.com;

import ramadevs.com.Core.Init;

public class OnEnable {
    Init init = new Init();
    public void onEnable() throws InterruptedException {
        init.Initialize();
    }
}
