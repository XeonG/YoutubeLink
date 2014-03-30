package me.Koolio.Youtube;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
//import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class Youtube extends JavaPlugin {

	String vidID = "";
	String Title = "";
		
	ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();

	public void onEnable() {
		//saveDefaultConfig();
		//Bukkit.getServer().getPluginManager().registerEvents(this, this);
		console.sendMessage(ChatColor.RED + "Youtube titles Enabled");
		}
	public void onDisable() {
		console.sendMessage(ChatColor.RED + "Youtube titles Disabled");
		}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandlevel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("yt")) {
			if(sender.hasPermission("kraftzone.youtube.announce")){
			if(args.length == 1 && linkifyYouTubeURLs(args[0])){
				if(getYoutubeInfo(vidID)){
				for(Player p : Bukkit.getServer().getOnlinePlayers()){
					p.sendMessage(ChatColor.DARK_GRAY+"-------------------------------------------------");
					p.sendMessage(ChatColor.WHITE+"-"+ChatColor.DARK_GRAY+"[Sent by] "+ChatColor.LIGHT_PURPLE+sender.getName().toString()+ChatColor.DARK_GRAY+" ---------------");
					p.sendMessage(ChatColor.WHITE+"-"+ChatColor.DARK_GRAY+"[Title] "+ChatColor.GREEN+Title);
					p.sendMessage(ChatColor.WHITE+"-"+ChatColor.DARK_GRAY+"[URL] "+ChatColor.GREEN+"http://youtu.be/"+vidID);
					p.sendMessage(ChatColor.DARK_GRAY+"----------------------------------------------------");
					}
					return true;

					}else{
						sender.sendMessage(ChatColor.RED+"Video title could not be retreived for: http://youtu.be/"+vidID);
						return true;
					}
				}else{
					sender.sendMessage(ChatColor.RED+"No valid youtube id could be found in this: "+args[1]);
					sender.sendMessage(ChatColor.GOLD+"put in a url like: http://www.youtube.com/watch?v=P4vN366_94Y&hd=1");
					return true;
				}
			}else{
				sender.sendMessage(ChatColor.RED+"You need the perm 'kraftzone.youtube.announce' to do this");
				return true;
			}
		}
		
		if (cmd.getName().equalsIgnoreCase("ytm")) {
			if(sender.hasPermission("kraftzone.youtube.msg")){
				if(args.length == 0){
					sender.sendMessage(ChatColor.RED+"Use /ytm <playername> <youtube url>");
					return true;
				}
				Player target = Bukkit.getServer().getPlayer(args[0]);
				
				if(args.length == 1 && target == null){
					sender.sendMessage(ChatColor.RED+"Invalid playername "+args[0]+ " /ytm <playername> <youtube url>");
					return true;
				}
				
				if(args.length == 2 && target != null && linkifyYouTubeURLs(args[1])){
					if(getYoutubeInfo(vidID)){
					target.sendMessage(ChatColor.DARK_GRAY+"-------------------------------------------------");
					target.sendMessage(ChatColor.WHITE+"-"+ChatColor.DARK_GRAY+"[Sent to you by] "+ChatColor.LIGHT_PURPLE+sender.getName().toString()+ChatColor.DARK_GRAY+" ---------------");
					target.sendMessage(ChatColor.WHITE+"-"+ChatColor.DARK_GRAY+"[Title] "+ChatColor.GREEN+Title);
					target.sendMessage(ChatColor.WHITE+"-"+ChatColor.DARK_GRAY+"[URL] "+ChatColor.GREEN+"http://youtu.be/"+vidID);
					target.sendMessage(ChatColor.DARK_GRAY+"----------------------------------------------------");
					return true;
					}else{
						sender.sendMessage(ChatColor.RED+"Video title could not be retreived for: http://youtu.be/"+vidID);
						return true;
					}
				}else{
					sender.sendMessage(ChatColor.RED+"No valid youtube id could be found in this: "+args[1]);
					sender.sendMessage(ChatColor.GOLD+"put in a url like: http://www.youtube.com/watch?v=P4vN366_94Y&hd=1");
					return true;
				}
			}else{
				sender.sendMessage(ChatColor.RED+"You need the perm 'kraftzone.youtube.msg' to do this");
				return true;
			}
		}
		return false;
	}
	
	public boolean linkifyYouTubeURLs(String url) {
		String pattern = "https?:\\/\\/(?:[0-9A-Z-]+\\.)?(?:youtu\\.be\\/|youtube\\.com(?:\\/embed\\/|\\/v\\/|\\/watch\\?v=|\\/ytscreeningroom\\?v=|\\/feeds\\/api\\/videos\\/|\\/user\\S*[^\\w\\-\\s]|\\S*[^\\w\\-\\s]))([\\w\\-]{11})[?=&+%\\w-]*";
		Pattern compiledPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
		Matcher matcher = compiledPattern.matcher(url);
		while(matcher.find()) {
		    //System.out.println(matcher.group());
			vidID = matcher.group(1);
		    return true;
		 	}
		 return false;
	}

	public boolean getYoutubeInfo(String vidIDd){
		
		 URL download = null;
			try {
				download = new URL("http://gdata.youtube.com/feeds/api/videos/"+vidIDd);
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
				return false;
			}
		      BufferedReader in = null;
			try {
				in = new BufferedReader(new InputStreamReader(download.openStream()));
			} catch (IOException e2) {
				e2.printStackTrace();
				return false;
			}

		      StringBuilder builder = new StringBuilder();
		      String aux = "";

		    try {
				while ((aux = in.readLine()) != null) {
				      builder.append(aux);
				  }
				in.close();
			} catch (IOException e1) {
				e1.printStackTrace();
				return false;
			}

		      String xmlString = builder.toString();
		      
		      DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		      builderFactory.setNamespaceAware(true);       // Set namespace aware
		      builderFactory.setValidating(false);           // and validating parser features
		      builderFactory.setIgnoringElementContentWhitespace(true); 
	    
		      DocumentBuilder builder2 = null;
		      try {
		    	  builder2 = builderFactory.newDocumentBuilder();  // Create the parser
		      } catch(ParserConfigurationException e) {
		    	  e.printStackTrace();
		    	  return false;
		      }
		      Document xmlDoc = null;

		      try {
		    	  xmlDoc = builder2.parse(new InputSource(new StringReader(xmlString)));

		      	} catch(SAXException e) {
		      			e.printStackTrace();
		      		return false;
		      	} catch(IOException e) {
		      		e.printStackTrace();
		      		return false;
		      	}
		      
	    
		      //System.out.println(GettingText(xmlDoc));
	    
		      Element channelNode = (Element) xmlDoc.getElementsByTagName("entry").item(0);
		
		      Node titleNode = channelNode.getElementsByTagName("title").item(0);
		      Title = titleNode.getFirstChild().getNodeValue();
		      //System.out.println("Title: " + Title);
		 return true;
	}
}
