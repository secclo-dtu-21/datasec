package dk.dtu.util.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class Configuration {
	private volatile static Configuration instance;
	Properties userProperties = new Properties();
	Properties serviceProperties = new Properties();
	Properties databaseProperties = new Properties();

	public Configuration() throws IOException {
		InputStream userPropertiesStream = this.getClass().getClassLoader().getResourceAsStream("user.properties");
		InputStream servicePropertiesStream = this.getClass().getClassLoader()
				.getResourceAsStream("service.properties");
		InputStream databasePropertiesStream = this.getClass().getClassLoader()
				.getResourceAsStream("database.properties");
		userProperties.load(userPropertiesStream);
		serviceProperties.load(servicePropertiesStream);
		databaseProperties.load(databasePropertiesStream);
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

	/* Get the information defined in the user.properties file */
	public String getTestUsername() {
		return (String) userProperties.get("testUsername");
	}

	public String getTestUserPassword() {
		return (String) userProperties.get("testUserPassword");
	}

	public int getValidSessionTime() {
		return Integer.parseInt((String) userProperties.get("validSessionTime"));
	}

	public List<String> getACTestUsers() {
		String testACUser = (String) userProperties.get("testACUser");
		return new ArrayList<>(Arrays.asList(testACUser.split(", ")));
	}

	public List<String> getUserACLists() {
		String testAccessControlList = (String) userProperties.get("testAccessControlList");
		return new ArrayList<>(Arrays.asList(testAccessControlList.split(", ")));
	}

	public List<String> getUserRoles() {
		String testAccessControlRole = (String) userProperties.get("testUserRole");
		return new ArrayList<>(Arrays.asList(testAccessControlRole.split(", ")));
	}


	/* Get information defined in the service.properties file */
	public int getServicePort() {
		return Integer.parseInt((String) serviceProperties.get("port"));
	}

	public String getServiceUrl() {
		return (String) serviceProperties.get("url");
	}

	public String getRandomHash() {
		return (String) serviceProperties.get("randomHash");
	}

	public String getAccessControlModel() {
		return (String) serviceProperties.get("accessControlModel");
	}

	/* Get the information defined in the database.properties file */
	public String getDBUrl() {
		return (String) databaseProperties.getProperty("url");
	}

	public String getDBUsername() {
		return (String) databaseProperties.getProperty("dbUsername");
	}

	public String getDBPassword() {
		return (String) databaseProperties.getProperty("password");
	}

}
