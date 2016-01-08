package org.virutor.chess.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

//FIXME redo completely or even better build upon something off the shelf
public class MultiProperties {

	private static final Logger logger = LogManager.getLogger(MultiProperties.class);

	public static final List<String> ENGINES_PLAYERS_PROPRS = Arrays.asList("engine", "player");
	
	private static final Pattern PROPERTY_PATTERN = Pattern.compile("^(.*)=(.*)$");
	
	private final List<String> mainProperties;
	private final String file;
	
	public MultiProperties(List<String> mainProperties, String file) {
		this.mainProperties = mainProperties;
		this.file = file;
	}
	
	public MultiProperties(String file) {
		this(ENGINES_PLAYERS_PROPRS, file);
	}
	
	public static void save(String file, Map<String, List<Map<String, String>>> data) throws Exception {
		
		OutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(file);
			BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));			
			
			for(List<Map<String, String>> maps : data.values()) {
				for(Map<String, String> map : maps) {
					for(Map.Entry<String, String> entry : map.entrySet()) {
						bufferedWriter.write(entry.getKey() + "=" + entry.getValue() + "\n");
					}
				}
			}
			
			bufferedWriter.flush();
			
		} finally {
			IOUtils.closeQuietly(outputStream);		
		}
		
	}
	
	
	public Map<String, List<Map<String, String>>> load() {
		
		
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(file);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			
			Map<String, List<Map<String, String>>> ret = new HashMap<String, List<Map<String,String>>>();

			for(String pr : mainProperties) {
				ret.put(pr, new ArrayList<Map<String, String>>());
			}
			
			Map<String, String> lastMainProps = null;
			
			String line;
			while((line = bufferedReader.readLine()) != null) {
				
				if(StringUtils.isBlank(line)) {
					continue;
				}
				
				Matcher matcher = PROPERTY_PATTERN.matcher(line);
				if(!matcher.find()) {
					//FIXME: let's ignore it for now, but it's no good
					continue;
				}
				String key = matcher.group(1).trim();
				String value = matcher.group(2).trim();
				if(mainProperties.contains(key)) {					
					lastMainProps = new HashMap<String, String>();					
					ret.get(key).add(lastMainProps);
				}
				if(lastMainProps == null) {
					throw new IllegalStateException();
				}
				lastMainProps.put(key, value);
			}
			
			
			return ret;
			
		} catch (Exception e) {
			logger.error("Caught exception", e);
			//FIXME: this is bad, but in order to fix it I have to change the API
			return null;
		} finally {
			IOUtils.closeQuietly(inputStream);
		}
		
	} 	
	
}
