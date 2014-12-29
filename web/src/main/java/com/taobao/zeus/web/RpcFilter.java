package com.taobao.zeus.web;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RPC;
import com.google.gwt.user.server.rpc.RPCRequest;
import com.google.gwt.user.server.rpc.RPCServletUtils;
import com.google.gwt.user.server.rpc.SerializationPolicy;
import com.google.gwt.user.server.rpc.SerializationPolicyLoader;
import com.google.gwt.user.server.rpc.SerializationPolicyProvider;
import com.taobao.zeus.model.LogDescriptor;
import com.taobao.zeus.store.mysql.MysqlLogManager;

/**
 * rpc过滤器,过滤所有.rpc请求,输出rpc响应内容
 * 
 * @author wb-chenshengyi
 * @date 2011-10-31 下午06:43:16
 * @version V1.0
 */
public class RpcFilter implements Filter, SerializationPolicyProvider {
	private static Logger log=LogManager.getLogger(RpcFilter.class);
	private final Map<String, SerializationPolicy> serializationPolicyCache = new HashMap<String, SerializationPolicy>();
	private ServletContext context;
	private WebApplicationContext webApplicationContext;
	@Autowired
	private MysqlLogManager zeusLogManager;
	
	@Override
	public void init(FilterConfig config) throws ServletException {
		this.context = config.getServletContext();
		webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(context);
//		zeusLogManager = new MysqlLogManager();
		zeusLogManager = (MysqlLogManager)webApplicationContext.getBean("zeusLogManager");
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		
		if (request.getRequestURI().endsWith(".rpc")) {
			String remoteIP = request.getRemoteAddr();
			String requestURI = request.getRequestURI();
			
			String rpc = requestURI.substring(requestURI.lastIndexOf("/") + 1);
			Object delegate =webApplicationContext.getBean(rpc);
			if (delegate == null) {
				throw new AssertionError("RPC实例为null");
			}
			String payload = RPCServletUtils.readContent(request,
					"text/x-gwt-rpc", null);
			RPCRequest rpcRequest = RPC.decodeRequest(payload,
					delegate.getClass(), this);
			Method rpcRequestMethod = rpcRequest.getMethod();
			try {
				String responsePayload = RPC4Monitor.invokeAndEncodeResponse(delegate,
						rpcRequestMethod, rpcRequest.getParameters(),
						rpcRequest.getSerializationPolicy(),
						rpcRequest.getFlags());
				RPCServletUtils.writeResponse(null, (HttpServletResponse) resp,
						responsePayload, false);
				//**********add rpc logs**********
				/*SimpleDateFormat df=new SimpleDateFormat("HH");
				int currentHour = Integer.parseInt(df.format(new Date()));
				if(currentHour >= 9 && currentHour < 22){
					try{
						LogDescriptor logDescriptor = new LogDescriptor();
						logDescriptor.setLogType("rpc");
						logDescriptor.setIp(remoteIP);
						logDescriptor.setUserName(LoginUser.user.get().getUid());
						logDescriptor.setUrl(requestURI);
						logDescriptor.setRpc(rpc);
						logDescriptor.setDelegate(delegate.getClass().getSimpleName());
						logDescriptor.setMethod(rpcRequestMethod.getName());
						String description = "";
						if(rpcRequest.getParameters().length>0 && rpcRequest.getParameters()[0]!=null){
							description = rpcRequest.getParameters()[0].toString();
						}
						logDescriptor.setDescription(description);
						zeusLogManager.addLog(logDescriptor);
					}catch(Exception ex){
						log.error(ex);
					}
				}*/
				//*********************************
/*				System.out.println("user: "+LoginUser.user.get().getUid()+
						" | IP: "+remoteIP+
						" | url: "+requestURI+
						" | rpc: "+rpc+
						" | delegate: "+delegate.getClass().getSimpleName()+
						" | method: "+rpcRequestMethod.getName()+
						" | parameter: "+ (rpcRequest.getParameters().length>0 ? rpcRequest.getParameters()[0].toString() : ""));*/
			} catch (SerializationException e) {
				log.error(e);
				throw new RuntimeException("RPC序列化异常", e);
			}
		} else {
			chain.doFilter(req, resp);
		}
	}
	/**
	 * 临时从RPC类中复制过来的，用来监控gwt反序列化消耗了多少时间，如果测试完成，可以删掉此代码，恢复调用RPC类中的方法
	 * @author zhoufang
	 *
	 */
	private static class RPC4Monitor{
		public static String invokeAndEncodeResponse(Object target, Method serviceMethod, Object[] args,
			      SerializationPolicy serializationPolicy, int flags) throws SerializationException {
			    if (serviceMethod == null) {
			      throw new NullPointerException("serviceMethod");
			    }

			    if (serializationPolicy == null) {
			      throw new NullPointerException("serializationPolicy");
			    }

			    String responsePayload;
			    try {
			      Object result=null;
			      result = serviceMethod.invoke(target, args);
			      responsePayload = RPC.encodeResponseForSuccess(serviceMethod, result, serializationPolicy, flags);
			    } catch (IllegalAccessException e) {
			      SecurityException securityException =
			          new SecurityException(formatIllegalAccessErrorMessage(target, serviceMethod));
			      securityException.initCause(e);
			      throw securityException;
			    } catch (IllegalArgumentException e) {
			      SecurityException securityException =
			          new SecurityException(formatIllegalArgumentErrorMessage(target, serviceMethod, args));
			      securityException.initCause(e);
			      throw securityException;
			    } catch (InvocationTargetException e) {
			      // Try to encode the caught exception
			      //
			      Throwable cause = e.getCause();
			      
			      responsePayload = RPC.encodeResponseForFailure(serviceMethod, cause, serializationPolicy, flags);
			    }

			    return responsePayload;
			  }
		  private static String formatIllegalArgumentErrorMessage(Object target, Method serviceMethod,
			      Object[] args) {
			    StringBuffer sb = new StringBuffer();
			    sb.append("Blocked attempt to invoke method '");
			    sb.append(getSourceRepresentation(serviceMethod));
			    sb.append("'");

			    if (target != null) {
			      sb.append(" on target '");
			      sb.append(printTypeName(target.getClass()));
			      sb.append("'");
			    }

			    sb.append(" with invalid arguments");

			    if (args != null && args.length > 0) {
			      sb.append(Arrays.asList(args));
			    }

			    return sb.toString();
			  }
		  private static String formatIllegalAccessErrorMessage(Object target, Method serviceMethod) {
			    StringBuffer sb = new StringBuffer();
			    sb.append("Blocked attempt to access inaccessible method '");
			    sb.append(getSourceRepresentation(serviceMethod));
			    sb.append("'");

			    if (target != null) {
			      sb.append(" on target '");
			      sb.append(printTypeName(target.getClass()));
			      sb.append("'");
			    }

			    sb.append("; this is either misconfiguration or a hack attempt");

			    return sb.toString();
			  }
		  private static String getSourceRepresentation(Method method) {
			    return method.toString().replace('$', '.');
			  }
		  private static String printTypeName(Class<?> type) {
			    // Primitives
			    //
			    if (type.equals(Integer.TYPE)) {
			      return "int";
			    } else if (type.equals(Long.TYPE)) {
			      return "long";
			    } else if (type.equals(Short.TYPE)) {
			      return "short";
			    } else if (type.equals(Byte.TYPE)) {
			      return "byte";
			    } else if (type.equals(Character.TYPE)) {
			      return "char";
			    } else if (type.equals(Boolean.TYPE)) {
			      return "boolean";
			    } else if (type.equals(Float.TYPE)) {
			      return "float";
			    } else if (type.equals(Double.TYPE)) {
			      return "double";
			    }

			    // Arrays
			    //
			    if (type.isArray()) {
			      Class<?> componentType = type.getComponentType();
			      return printTypeName(componentType) + "[]";
			    }

			    // Everything else
			    //
			    return type.getName().replace('$', '.');
			  }
	}
	
	@Override
	public SerializationPolicy getSerializationPolicy(String moduleBaseURL,
			String strongName) {
		SerializationPolicy serializationPolicy = null;
		synchronized (serializationPolicyCache) {
			serializationPolicy = serializationPolicyCache.get(moduleBaseURL
					+ strongName);
		}
		if (serializationPolicy != null) {
			return serializationPolicy;
		}
		serializationPolicy = loadSerializationPolicy(moduleBaseURL, strongName);
		if (serializationPolicy == null) {
			serializationPolicy = RPC.getDefaultSerializationPolicy();
		}
		serializationPolicyCache.put(moduleBaseURL + strongName,
				serializationPolicy);
		return serializationPolicy;
	}

	SerializationPolicy loadSerializationPolicy(String moduleBaseURL,
			String strongName) {
		String modulePath = null;
		if (moduleBaseURL != null) {
			try {
				modulePath = new URL(moduleBaseURL).getPath();
				//web应用不在根路径情况下(contextPath 非 / 路径下)，矫正modulePath地址
				String temp=modulePath.substring(1, modulePath.length()-1);
				if(temp.contains("/")){
					modulePath=temp.substring(temp.indexOf("/"))+"/";
				}
			} catch (MalformedURLException ex) {
			}
		}
		SerializationPolicy serializationPolicy = null;
		String serializationPolicyFilePath = SerializationPolicyLoader
				.getSerializationPolicyFileName(modulePath + strongName);
		InputStream is = context
				.getResourceAsStream(serializationPolicyFilePath);
		try {
			try {
				serializationPolicy = SerializationPolicyLoader.loadFromStream(
						is, null);
			} catch (IOException e) {
			} catch (ParseException e) {
			}
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					// skip exception
				}
			}
		}
		return serializationPolicy;
	}

	@Override
	public void destroy() {

	}
}