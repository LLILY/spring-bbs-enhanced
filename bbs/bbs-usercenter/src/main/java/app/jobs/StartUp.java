package app.jobs;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.ui.velocity.VelocityEngineFactory;
/*
 * 项目启动后初始化
 */
 public class StartUp implements ApplicationListener<ContextRefreshedEvent>{

	public void onApplicationEvent(ContextRefreshedEvent event) {
		System.out.println(event.getApplicationContext().getParent());
		//避免启动时调用两次
		if(event.getApplicationContext().getParent() == null){//root application context 没有parent，他就是老大.
	           //需要执行的逻辑代码，当spring容器初始化完成后就会执行该方法。
			System.out.println("********************************************");
	      }
		
		
	}

}
