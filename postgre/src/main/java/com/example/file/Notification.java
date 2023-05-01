package com.example.file;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.google.gson.Gson;

class JsonBody {
	private String filename;
	private Long userId;
	private List<Long> sharedList;
	private String method;

	public JsonBody(String filename, Long userId, List<Long> sharedList, String method) {
		this.filename = filename;
		this.userId = userId;
		this.sharedList = sharedList;
		this.method = method;
	}
}

public class Notification {


    public static int notify(String filename, Long userId, List<Long> sharedList, String method)  {
        String url = "http://node-app:3005/notify";
		Gson gson = new Gson();
		String json = gson.toJson(new JsonBody(filename, userId, sharedList, method));

		System.out.println("NOTIFYING!!!");
		
		try {
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");

			con.setDoOutput(true);
			byte[] jsonBytes = json.getBytes(StandardCharsets.UTF_8);
			con.getOutputStream().write(jsonBytes);

			int responseCode = con.getResponseCode();

			System.out.println("Response code: " + responseCode);
			return responseCode;
		} catch (IOException ioe) {

			ioe.printStackTrace();
			return -1;
		}
    }

}
