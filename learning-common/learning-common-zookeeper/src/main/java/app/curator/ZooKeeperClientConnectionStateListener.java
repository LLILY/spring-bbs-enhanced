package app.curator;

/**
 * Created by lili19289 on 2016/8/18.
 */
public interface ZooKeeperClientConnectionStateListener {


    public void notifyConnected();

    public void notifyDisconnected();
}
