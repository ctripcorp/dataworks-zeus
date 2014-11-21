Hadoop任务的调试运行到生产任务的周期调度
宙斯支持任务的整个生命周期

从功能上来说，支持：  
Hadoop MapReduce任务的调试运行  
Hive任务的调试运行  
Shell任务的运行    
Hive元数据的可视化查询与数据预览  
Hadoop任务的自动调度  

V0.15修改内容：
	1. 修改邮件发送bug,增加多个邮件发送人
	2. 修改Job自动开启关闭功能
	3. Noc报警配置写到配置文件
	4. JOB文件夹增加写的权限

v0.15.1
	增加自动开启关闭依赖检测。下游依赖存在开启，任务不能关闭。上游依赖全部开启，任务才能开启。
	从界面上移除了周期调度
	增加了历史action从内存中清除，并在数据库中备份，默认设置为两个月前的

v0.16修改漏跑程序

v0.18.1
修改默认依赖周期为同一天
添加双击任务栏可以跳到job详情功能
添加job任务总览按日期范围进行过滤功能

v0.19
优化依赖job的action方法
修复了pagingtoolbar的空指针和显示异常
zeus密码显示加密
修复work断线任务重新分配bug
修复指定不存在host导致队列阻塞bug
修复了依赖host不能指定的bug
修复了时间截取的bug

v0.20
增加job和action资源，脚本和目录同步更新功能
修改action生成时间，改为整点生成
JOB时间变更，action生成做调整：
	1. 情况一：job定时修改前和修改后都在下一次action生成之前，当天有两个版本，后生成的版本status=failed，漏跑检查时不执行。
	2. 情况二：job定时修改前在下一次action生成之前，修改后在下一次action生成之后，当天有两个版本，修改后的时间在schedule中继续等待调度。
	3. 情况三：job定时修改前和修改后都在下一次action生成之后，当天只有一个版本，删除修改前的时间版本。
	4. 情况四：job定时修改后在下一次action生成之前，修改前在下一次action生成之后，当天只有一个版本，删除修改前的时间版本，后生成的版本status=failed，不会漏跑调度。
	
v0.21
增加host修改即时生效功能.
增加hive元数据展示功能.
修复hive分区数据下载bug，改为访问hdfs api之前同时加载core-site.xml和hdfs-site.xml.
屏蔽系统配置项"zeus.dependency.cycle"; "run.priority.level";"roll.back.times";"roll.back.wait.time";"zeus.secret.script"配置会展现但是不能够通过配置栏编辑。