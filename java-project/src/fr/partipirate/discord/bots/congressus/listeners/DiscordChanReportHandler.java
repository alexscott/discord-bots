package fr.partipirate.discord.bots.congressus.listeners;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.json.JSONArray;
import org.json.JSONObject;

import fr.partipirate.discord.bots.congressus.Configuration;
import fr.partipirate.discord.bots.congressus.commands.congressus.CongressusHelper;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.channel.text.TextChannelCreateEvent;
import net.dv8tion.jda.core.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.core.events.channel.text.update.TextChannelUpdateNameEvent;
import net.dv8tion.jda.core.events.channel.voice.VoiceChannelCreateEvent;
import net.dv8tion.jda.core.events.channel.voice.VoiceChannelDeleteEvent;
import net.dv8tion.jda.core.events.channel.voice.update.VoiceChannelUpdateNameEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class DiscordChanReportHandler extends ListenerAdapter {
	@Override
	public void onTextChannelCreate(TextChannelCreateEvent event) {
		updateChannels(event.getGuild());
	}
	
	@Override
	public void onTextChannelUpdateName(TextChannelUpdateNameEvent event) {
		updateChannels(event.getGuild());
	}
	
	@Override
	public void onTextChannelDelete(TextChannelDeleteEvent event) {
		updateChannels(event.getGuild());
	}
	
	@Override
	public void onVoiceChannelCreate(VoiceChannelCreateEvent event) {
		updateChannels(event.getGuild());
	}
	
	@Override
	public void onVoiceChannelUpdateName(VoiceChannelUpdateNameEvent event) {
		updateChannels(event.getGuild());
	}
	
	@Override
	public void onVoiceChannelDelete(VoiceChannelDeleteEvent event) {
		updateChannels(event.getGuild());
	}

	private void updateChannels(Guild guild) {
		JSONArray channelsArray = new JSONArray();
		
		for (Channel channel : guild.getChannels()) {
			StringBuilder sb = new StringBuilder();
			sb.append("https://discordapp.com/channels/");
			sb.append(guild.getId());
			sb.append("/");
			sb.append(channel.getId());
			
			JSONObject channelObject = new JSONObject();
			channelObject.put("id", channel.getId());
			channelObject.put("server_id", guild.getId());
			if (channel.getType() == ChannelType.TEXT) {
				channelObject.put("type", "text");
			}
			else if (channel.getType() == ChannelType.VOICE) {
				channelObject.put("type", "voice");
			}
			else {
				continue; // drop this one
			}
			channelObject.put("url", sb.toString());
			channelObject.put("name", channel.getName());
			
			channelsArray.put(channelObject);
		}

		StringBuilder urlBuilder = new StringBuilder();
		urlBuilder.append(CongressusHelper.getUrl());
		urlBuilder.append("meeting_api.php?method=do_updateChannels");

		urlBuilder.append("&token=");
		urlBuilder.append(Configuration.getInstance().OPTIONS.get("congressus").get("token"));

		urlBuilder.append("&secret=");
		urlBuilder.append(Configuration.getInstance().OPTIONS.get("congressus").get("secret"));

		HttpURLConnection httpClient = null;

		try {
	        httpClient = (HttpURLConnection) new URL(urlBuilder.toString()).openConnection();

	        //add request header
	        httpClient.setRequestMethod("POST");
	        httpClient.setRequestProperty("User-Agent", "Java client");
	        httpClient.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	        
	        httpClient.setDoOutput(true);

	        String urlParameters = "data=" + channelsArray.toString();
	        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);

	        DataOutputStream dos = new DataOutputStream(httpClient.getOutputStream());
            dos.write(postData);
            dos.flush();
            
            int responseCode = httpClient.getResponseCode();
/*
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Post parameters : " + urlParameters);
            System.out.println("Response Code : " + responseCode);
*/
            BufferedReader br = new BufferedReader(new InputStreamReader(httpClient.getInputStream()));

            String line;
            StringBuilder response = new StringBuilder();

            while ((line = br.readLine()) != null) {
                response.append(line);
            }
/*
    		System.out.println(url);
    		System.out.println(urlParameters);

            //print result
            System.out.println(response.toString());
*/
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			httpClient.disconnect();
		}
	}
}