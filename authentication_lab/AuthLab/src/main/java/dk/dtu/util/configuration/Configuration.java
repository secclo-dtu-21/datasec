package dk.dtu.util.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {
	private volatile static Configuration instance;
	Properties userProperties = new Properties();
	Properties serviceProperties = new Properties();

	public Configuration() throws IOException {
		InputStream userPropertiesStream = this.getClass().getClassLoader().getResourceAsStream("user.properties");
		InputStream servicePropertiesStream = this.getClass().getClassLoader()
				.getResourceAsStream("service.properties");
		userProperties.load(userPropertiesStream);
		serviceProperties.load(servicePropertiesStream);
	}

	public static Configuration getInstance() throws IOException {
		if (instance == null) {
			synchronized (Configuration.class) {
				if (instance == null)
					instance = new Configuration();
			}
		}
		return instance;
	}

	public String getTestUsername() {
		return (String) userProperties.get("testUsername");
	}

	public String getTestUserPassword() {
		return (String) userProperties.get("testUserPassword");
	}

	public int getValidSessionTime() {
		return Integer.parseInt((String) userProperties.get("validSessionTime"));
	}

	public int getServicePort() {
		return Integer.parseInt((String) serviceProperties.get("port"));
	}

	public String getServiceUrl() {
		return (String) serviceProperties.get("url");
	}

	public String getRandomHash() {
		return (String) serviceProperties.get("randomHash");
	}

}
