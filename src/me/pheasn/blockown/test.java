package me.pheasn.blockown;

import java.net.*;
import java.io.*;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class test {
	public static void main(String[] args) throws ParseException {
		InputStreamReader ir = null;
		try {
			URL url = new URL(
					"http://api.bukget.org/3/plugins/bukkit/blockown/latest");
			ir = new InputStreamReader(url.openStream());
			JSONParser json = new JSONParser();
			Object node = json.parse(ir);
			JSONObject object = (JSONObject) node;
			JSONObject versions = (JSONObject) ((JSONArray) object
					.get("versions")).get(0);
			System.out.println(versions.get("version"));
			System.out.println(versions.get("download"));

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
