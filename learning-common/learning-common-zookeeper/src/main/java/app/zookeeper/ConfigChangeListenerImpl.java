package app.zookeeper;

/**
 * 监听器，监听配置的改变
 * 
 * @author june
 * 
 */
public  class ConfigChangeListenerImpl implements  ConfigChangeListener{
		public void configChanged(String key, String value) {
			System.out.println("*****接收到数据变更通知: key=" + key + ", value="
					+ value);
		}
}