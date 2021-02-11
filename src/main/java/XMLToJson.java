import java.io.*;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONPointer;
import org.json.XML;

public class XMLToJson {
	public static int INDENT_FACTOR = 4;
	
	// 1. Read an XML file (given as command line argument) into a JSON object and write the JSON object back on disk as a JSON file.
	public static void xmlToJson(String xmlInput) {
		JSONObject jsonOutput = new JSONObject();
		try {
	    	jsonOutput = XML.toJSONObject(xmlInput);
	    	String str = jsonOutput.toString(INDENT_FACTOR);
	    	System.out.println(str);   
			try {
				FileWriter file;
				file = new FileWriter("D:\\2021Winter\\262Programming Styles\\Milestone\\jsonOutput.json");
				file.write(str);
		    	file.flush();
		    	file.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
	    } catch (JSONException e) {
	    	System.out.println(e.toString());
	    }     
	  }

	// 2. Read an XML file into a JSON object, and extract some smaller sub-object inside, given a certain path (use JSONPointer). Write that smaller object to disk as a JSON file.
	public static void xmlToJson2(String xml, String pointer) {
		JSONObject jsonOutput = new JSONObject();
		try {
	    	jsonOutput = XML.toJSONObject(xml);
	    	Object subJsonOutput = jsonOutput.query(pointer);
	    	String str = subJsonOutput.toString();
	    	String str2 = null;
	    	if(subJsonOutput instanceof JSONObject) {
	    		JSONObject jsonTemp =  new JSONObject(str);
	    		str2 = jsonTemp.toString(INDENT_FACTOR); 
		    	System.out.println(str2);
	    	}
	    			
	    	if(subJsonOutput instanceof JSONArray) {
	    		JSONArray jsonTemp =  new JSONArray(str);
	    		str2 = jsonTemp.toString(INDENT_FACTOR); 
		    	System.out.println(str2);
	    	}
	    	
			try {
				FileWriter file;
				file = new FileWriter("D:\\2021Winter\\262Programming Styles\\Milestone\\jsonOutput2withSubObject.json");
				file.write(str2);
		    	file.flush();
		    	file.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    } catch (JSONException e) {
	    	System.out.println(e.toString());
	    }  
	}

	// 3. Read an XML file into a JSON object, check if it has a certain key path (given in the command line too). If so, save the JSON object to disk; if not, discard it.
	public static void xmlToJson3(String xml, String pointer) {
		JSONObject jsonOutput = new JSONObject();
		try {
	    	jsonOutput = XML.toJSONObject(xml);
	    	JSONPointer jsonPointer = new JSONPointer(pointer);
	    	if (jsonOutput.optQuery(jsonPointer) != null) {
	    		String str = jsonOutput.toString(INDENT_FACTOR);
		    	System.out.println(str);
				try {
					FileWriter file;
					file = new FileWriter("D:\\2021Winter\\262Programming Styles\\Milestone\\jsonOutput3checkKey.json");
					file.write(str);
			    	file.flush();
			    	file.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	}
	    } catch (JSONException e) {
	    	System.out.println(e.toString());
	    }  
	}
	
////// 4. Helper function. Reference: https://stackoverflow.com/questions/50465702/replace-all-keys-in-a-json-object-composed-of-jsonarray
	public static JSONObject performJSONObject(JSONObject inputObject) throws Exception {
        JSONObject resultObject = new JSONObject();
        Iterator<String> iterator = inputObject.keys();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            if (inputObject.get(key) instanceof JSONObject) {
                JSONObject jsonObject = (JSONObject) inputObject.get(key);
                // subProblem: performJSONObject(jsonObject)
                resultObject.put(getModifiedKey(key), performJSONObject(jsonObject));
            } else if (inputObject.get(key) instanceof JSONArray) {
                JSONArray jsonArray = (JSONArray) inputObject.get(key);
                resultObject.put(getModifiedKey(key), performJSONArray(jsonArray));
            } else {
                resultObject.put(getModifiedKey(key), inputObject.get(key));
            }
        }
        return resultObject;
    }
	
	public static JSONArray performJSONArray(JSONArray inputArray) throws Exception {
        JSONArray resultArray = new JSONArray();
        for (int i = 0; i < inputArray.length(); i++) {
            if (inputArray.get(i) instanceof JSONObject) {
                JSONObject jsonObject = (JSONObject) inputArray.get(i);
                resultArray.put(i, performJSONObject(jsonObject));
            } else if (inputArray.get(i) instanceof JSONArray) {
                JSONArray jsonArray = (JSONArray) inputArray.get(i);
                resultArray.put(i, performJSONArray(jsonArray));
            } else {
                resultArray.put(i, inputArray.get(i));
            }
        }
        return resultArray;
    }

    public static String getModifiedKey(String strn) {
        String result = "swe262_" + strn;
        return result.substring(0, result.length());
    }

	// 4. Read an XML file into a JSON object, and add the prefix "swe262_" to all of its keys.
	public static void xmlToJson4(String xml) throws Exception {
		JSONObject jsonOutput = new JSONObject();
		try {
	    	jsonOutput = XML.toJSONObject(xml);	 
	    	JSONObject jsonResult = performJSONObject(jsonOutput);
	    	String str = jsonResult.toString(INDENT_FACTOR);
	    	System.out.println(str);   
			try {
				FileWriter file;
				file = new FileWriter("D:\\2021Winter\\262Programming Styles\\Milestone\\jsonOutput4addPrefix.json");
				file.write(str);
		    	file.flush();
		    	file.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
	    } catch (JSONException e) {
	    	System.out.println(e.toString());
	    }     
	}
	
	//	input: /a/b/c
	//	get: /a/b
	//	res = get(/a/b)
	//	res.put(c) = your object
	// 5. Read an XML file into a JSON object, replace a sub-object on a certain key path with another JSON object that you construct, then write the result on disk as a JSON file. 
	public static void xmlToJson5(String xml, String pointer, JSONArray subObjToReplace) {
		String[] split = pointer.split("/");
		String bottomLevelKey = split[split.length - 1];
		
		System.out.println(bottomLevelKey);
		
		//    a/b      3 - 1 - 1       
		pointer = pointer.substring(0, pointer.length() - bottomLevelKey.length() - 1);
		JSONObject jsonOutput = new JSONObject();
		try {
	    	jsonOutput = XML.toJSONObject(xml);
	    	if (jsonOutput.query(pointer) instanceof JSONObject) {
	    		JSONObject subJsonOutput = (JSONObject) jsonOutput.query(pointer);
	    		subJsonOutput.put(bottomLevelKey, subObjToReplace);
	        } else if (jsonOutput.query(pointer) instanceof JSONArray) {
	        	JSONArray subJsonOutput = (JSONArray) jsonOutput.query(pointer);
	    		// Reference: https://stackoverflow.com/questions/38913318/how-to-update-jsonarray-value-on-java
	    		for (int i=0; i < subJsonOutput.length(); i++){
	    			JSONObject obj = (JSONObject) subJsonOutput.get(i);
	    			obj.put(bottomLevelKey, subObjToReplace);
	    		}
            } 
	    	String result = jsonOutput.toString(INDENT_FACTOR); 
	    	System.out.println(result);
			try {
				FileWriter file;
				file = new FileWriter("D:\\2021Winter\\262Programming Styles\\Milestone\\jsonOutput5replacesubObj.json");
				file.write(result);
		    	file.flush();
		    	file.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    } catch (JSONException e) {
	    	System.out.println(e.toString());
	    }  
	}
	
	//  when process a GB-scale file, it would throws OutOfMemoryError as the java heap space is running out. In addition, it would take a relatively long time to process medium-scale file.
	// Main Question: how do decide which method would call or doing all the following   / construct       5only jsonObject?  
	public static void main (String[] args) throws Exception {
		// "SmallSample.xml"
        File xmlFile = new File(args[1]);
		byte[] b = Files.readAllBytes(xmlFile.toPath());
		String smallSample = new String(b); 
		System.out.println(smallSample);
		// 1 toJson SmallSample.xml
		if (args[0].equals("toJson")) {
			xmlToJson(smallSample);
		}
	    // 2. sub  //       toSubJson SmallSample.xml /catalog    jsonObject    or toSubJson SmallSample.xml /catalog/book       jsonArray example    
		if (args[0].equals("toSubJson")) {
			xmlToJson2(smallSample, args[2]);
		}
		// 3. check key path   // "/catalog/book"          toJsonWithPathCheck SmallSample.xml /catalog
		if (args[0].equals("toJsonWithPathCheck")) {
			xmlToJson3(smallSample, args[2]);
		}
//	    // 4. addprefix    // toJsonAddPrefix SmallSample.xml
		if (args[0].equals("toJsonAddPrefix")) {   
		    xmlToJson4(smallSample);
		}

//	    // 5  // toJsonAddJsonArray SmallSample.xml /catalog/book/author   /catalog/book
		if (args[0].equals("toJsonAddJsonArray")) {
			JSONArray array = new JSONArray();
			JSONObject item = new JSONObject();
			item.put("gender", "male");
			item.put("name", "Mengshi");
			array.put(item);
		    xmlToJson5(smallSample, args[2], array);
		}
	}
}

// Reference: https://www.youtube.com/watch?v=nhW5HnwRJIk&ab_channel=DineshKrishnan
// Reference: https://crunchify.com/how-to-write-json-object-to-file-in-java/
//String message;
//message = json.toString(INDENT_FACTOR);
//System.out.println(message);
//JSONObject json = new JSONObject();
//json.put("student1", array);
//json.put("university", "UCI");