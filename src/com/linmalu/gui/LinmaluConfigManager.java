package com.linmalu.gui;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.linmalu.library.api.LinmaluConfig;
import com.linmalu.library.api.LinmaluPlayer;
import com.linmalu.library.network.LinmaluNetwork;

public class LinmaluConfigManager
{
	private static LinmaluConfigManager linmaluConfigManager;

	public static LinmaluConfigManager getInstance()
	{
		if(linmaluConfigManager == null)
		{
			linmaluConfigManager = new LinmaluConfigManager();
		}
		return linmaluConfigManager;
	}

	private final LinmaluConfig imageConfig = new LinmaluConfig(new File(Main.getMain().getDataFolder(), "images.yml"));
	private final Map<Integer, byte[]> imageBuffer = new HashMap<>();

	private LinmaluConfigManager()
	{
		imageConfig.save();
		reload();
	}

	public void reload()
	{
		imageConfig.reload();
		imageBuffer.clear();
		for(String key : imageConfig.getKeys(false))
		{
			try
			{
				byte[] buffer = getImageBuffer(imageConfig.getString(key));
				if(buffer != null)
				{
					imageBuffer.put(Integer.parseInt(key), buffer);
				}
			}
			catch(NumberFormatException e)
			{
				Bukkit.getConsoleSender().sendMessage(Main.getMain().getTitle() + ChatColor.GOLD + "숫자가 아닙니다. " + ChatColor.YELLOW + key);
			}
		}
		LinmaluPlayer.getOnlinePlayers().forEach(this::PlayerJoinEvent);
	}

	// 이미지 목록
	public Map<String, String> getImageList()
	{
		Map<String, String> map = new HashMap<>();
		imageConfig.getKeys(false).forEach(key ->
		{

			map.put(key, imageConfig.getString(key));
		});
		return map;
	}

	// 이미지 추가
	public boolean addImage(int id, String name)
	{
		String key = String.valueOf(id);
		if(imageConfig.isString(key))
		{
			return false;
		}
		byte[] buffer = getImageBuffer(imageConfig.getString(key));
		imageConfig.set(key, name);
		if(buffer != null)
		{
			imageBuffer.put(id, buffer);
			LinmaluPlayer.getOnlinePlayers().forEach(player -> LinmaluNetwork.getInstance().sendCreateImageMessage(player, id, buffer));
		}
		return true;
	}

	// 이미지 삭제
	public boolean removeImage(int id)
	{
		String key = String.valueOf(id);
		if(imageConfig.isString(key))
		{
			imageConfig.remove(key);
			imageBuffer.remove(id);
			return true;
		}
		return false;
	}

	// 이미지 초기화
	public void clearImage()
	{
		imageConfig.clear();
		imageBuffer.clear();
	}

	// 플레이어 접속
	public void PlayerJoinEvent(Player player)
	{
		imageBuffer.forEach((k, v) -> LinmaluNetwork.getInstance().sendCreateImageMessage(player, k, v));
	}

	// 이미지 데이터 가져오기
	private byte[] getImageBuffer(String name)
	{
		try(ByteArrayOutputStream out = new ByteArrayOutputStream())
		{
			BufferedImage image = ImageIO.read(new File(Main.getMain().getDataFolder(), name));
			ImageIO.write(image, "JPG", out);
			return out.toByteArray();
		}
		catch(IOException e)
		{
			Bukkit.getConsoleSender().sendMessage(Main.getMain().getTitle() + ChatColor.GOLD + "파일읽기 실패 : " + ChatColor.YELLOW + name);
		}
		return null;
	}
}
