package com.linmalu.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.linmalu.library.api.LinmaluPlayer;
import com.linmalu.library.api.LinmaluServer;
import com.linmalu.library.api.LinmaluTellraw;
import com.linmalu.library.network.LinmaluNetwork;
import com.linmalu.library.network.LinmaluRenderType;

public class Main_Command implements CommandExecutor
{
	public Main_Command()
	{
		Main.getMain().getCommand(Main.getMain().getDescription().getName()).setTabCompleter(new TabCompleter()
		{
			@Override
			public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args)
			{
				ArrayList<String> list = new ArrayList<>();
				if(sender.isOp())
				{
					if(args.length == 1)
					{
						list.add("reload");
						list.add("리로드");
						list.add("이미지");
						list.add("텍스트");
						list.add("렌더링");
						list.add("모드확인");
					}
					else if(args.length == 2)
					{
						if(args[0].equals("이미지"))
						{
							list.add("생성");
							list.add("삭제");
							list.add("목록");
							list.add("그리기");
							list.add("지우기");
							list.add("모두지우기");
							list.add("시간");
							list.add("위치");
							list.add("변경");
						}
						else if(args[0].equals("텍스트"))
						{
							list.add("그리기");
							list.add("지우기");
							list.add("모두지우기");
							list.add("시간");
							list.add("위치");
							list.add("변경");
							list.add("색상");
						}
						else if(args[0].equals("렌더링"))
						{
							list.add("지우기");
							list.add("그리기");
							list.add("초기화");
						}
					}
					else if(args.length == 3 && (args[0].equals("이미지") || args[0].equals("텍스트") || args[0].equals("렌더링")))
					{
						if(args[0].equals("이미지") && args[1].equals("삭제"))
						{
							list.addAll(LinmaluConfigManager.getInstance().getImageList().keySet());
						}
						else
						{
							return null;
						}
					}
					else if(args.length == 4)
					{
						if(args[0].equals("렌더링") && (args[1].equals("지우기") || args[1].equals("그리기")))
						{
							for(LinmaluRenderType type : LinmaluRenderType.values())
							{
								list.add(type.toString());
							}
						}
					}
				}
				return list.stream().filter(msg -> msg.toLowerCase().startsWith(args[args.length - 1].toLowerCase())).count() == 0 ? list : list.stream().filter(msg -> msg.toLowerCase().startsWith(args[args.length - 1].toLowerCase())).sorted().collect(Collectors.toList());
			}
		});
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String args[])
	{
		if(sender.isOp())
		{
			try
			{
				if(args.length == 1 && (args[0].equalsIgnoreCase("reload") || args[0].equals("리로드")))
				{
					LinmaluConfigManager.getInstance().reload();
					sender.sendMessage(Main.getMain().getTitle() + ChatColor.GREEN + "리로드가 완료되었습니다.");
					return true;
				}
				else if(args.length >= 2 && args[0].equals("이미지"))
				{
					if(args.length >= 4 && args[1].equals("생성"))
					{
						if(LinmaluConfigManager.getInstance().addImage(Integer.parseInt(args[2]), getArray(args, 3)))
						{
							sender.sendMessage(Main.getMain().getTitle() + ChatColor.GREEN + "이미지가 생성되었습니다.");
						}
						else
						{
							sender.sendMessage(Main.getMain().getTitle() + ChatColor.YELLOW + "아이디에 이미지가 등록되어 있습니다.");
						}
						return true;
					}
					else if(args.length == 3 && args[1].equals("삭제"))
					{
						if(LinmaluConfigManager.getInstance().removeImage(Integer.parseInt(args[2])))
						{
							sender.sendMessage(Main.getMain().getTitle() + ChatColor.GREEN + "이미지가 삭제되었습니다.");
						}
						else
						{
							sender.sendMessage(Main.getMain().getTitle() + ChatColor.YELLOW + "아이디에 이미지가 등록되어 있지 않습니다.");
						}
						return true;
					}
					else if(args.length == 2 && args[1].equals("목록"))
					{
						sender.sendMessage(Main.getMain().getTitle() + ChatColor.GREEN + " = = = = = [ Linmalu Image List ] = = = = =");
						LinmaluConfigManager.getInstance().getImageList().forEach((k, v) ->
						{
							sender.sendMessage(Main.getMain().getTitle() + ChatColor.GOLD + k + ChatColor.RESET + " : " + ChatColor.YELLOW + v);
						});
						return true;
					}
					else if(args.length >= 3)
					{
						List<Player> players = LinmaluPlayer.getPlayers(args[2]);
						if(players.size() == 0)
						{
							sender.sendMessage(Main.getMain().getTitle() + ChatColor.YELLOW + "플레이어가 접속중이 아닙니다.");
							return true;
						}
						if(args.length == 17 && args[1].equals("그리기"))
						{
							int id = Integer.parseInt(args[3]);
							int image_id = Integer.parseInt(args[4]);
							int fade_in = Integer.parseInt(args[5]);
							int time = Integer.parseInt(args[6]);
							int fade_out = Integer.parseInt(args[7]);
							float alpha = Float.parseFloat(args[8]);
							float g_x1 = Float.parseFloat(args[9]);
							float g_y1 = Float.parseFloat(args[10]);
							float g_x2 = Float.parseFloat(args[11]);
							float g_y2 = Float.parseFloat(args[12]);
							float i_x1 = Float.parseFloat(args[13]);
							float i_y1 = Float.parseFloat(args[14]);
							float i_x2 = Float.parseFloat(args[15]);
							float i_y2 = Float.parseFloat(args[16]);
							for(Player player : players)
							{
								LinmaluNetwork.getInstance().sendDrawImageMessage(player, id, image_id, fade_in, time, fade_out, alpha, g_x1, g_y1, g_x2, g_y2, i_x1, i_y1, i_x2, i_y2);
							}
							sender.sendMessage(Main.getMain().getTitle() + ChatColor.GREEN + "이미지를 그렸습니다.");
							return true;
						}
						else if(args.length == 4 && args[1].equals("지우기"))
						{
							int id = Integer.parseInt(args[3]);
							for(Player player : players)
							{
								LinmaluNetwork.getInstance().sendEraseObjectMessage(player, id);
							}
							sender.sendMessage(Main.getMain().getTitle() + ChatColor.GREEN + "이미지를 지웠습니다.");
							return true;
						}
						else if(args.length == 3 && args[1].equals("모두지우기"))
						{
							for(Player player : players)
							{
								LinmaluNetwork.getInstance().sendClearObjectMessage(player);
							}
							sender.sendMessage(Main.getMain().getTitle() + ChatColor.GREEN + "이미지를 모두 지웠습니다.");
							return true;
						}
						else if(args.length == 8 && args[1].equals("시간"))
						{
							int id = Integer.parseInt(args[3]);
							int fade_in = Integer.parseInt(args[4]);
							int time = Integer.parseInt(args[5]);
							int fade_out = Integer.parseInt(args[6]);
							float alpha = Float.parseFloat(args[7]);
							for(Player player : players)
							{
								LinmaluNetwork.getInstance().sendTimeImageMessage(player, id, fade_in, time, fade_out, alpha);
							}
							sender.sendMessage(Main.getMain().getTitle() + ChatColor.GREEN + "이미지의 시간을 변경했습니다.");
							return true;
						}
						else if(args.length == 12 && args[1].equals("위치"))
						{
							int id = Integer.parseInt(args[3]);
							float g_x1 = Float.parseFloat(args[4]);
							float g_y1 = Float.parseFloat(args[5]);
							float g_x2 = Float.parseFloat(args[6]);
							float g_y2 = Float.parseFloat(args[7]);
							float i_x1 = Float.parseFloat(args[8]);
							float i_y1 = Float.parseFloat(args[9]);
							float i_x2 = Float.parseFloat(args[10]);
							float i_y2 = Float.parseFloat(args[11]);
							for(Player player : players)
							{
								LinmaluNetwork.getInstance().sendLocationImageMessage(player, id, g_x1, g_y1, g_x2, g_y2, i_x1, i_y1, i_x2, i_y2);
							}
							sender.sendMessage(Main.getMain().getTitle() + ChatColor.GREEN + "이미지의 위치를 변경했습니다.");
							return true;
						}
						else if(args.length == 5 && args[1].equals("변경"))
						{
							int id = Integer.parseInt(args[3]);
							int image_id = Integer.parseInt(args[3]);
							for(Player player : players)
							{
								LinmaluNetwork.getInstance().sendChangeImageMessage(player, id, image_id);
							}
							sender.sendMessage(Main.getMain().getTitle() + ChatColor.GREEN + "이미지를 변경했습니다.");
							return true;
						}
					}
				}
				else if(args.length >= 3 && args[0].equals("텍스트"))
				{
					List<Player> players = LinmaluPlayer.getPlayers(args[2]);
					if(players.size() == 0)
					{
						sender.sendMessage(Main.getMain().getTitle() + ChatColor.YELLOW + "플레이어가 접속중이 아닙니다.");
						return true;
					}
					if(args.length >= 15 && args[1].equals("그리기"))
					{
						int id = Integer.parseInt(args[3]);
						int fade_in = Integer.parseInt(args[4]);
						int time = Integer.parseInt(args[5]);
						int fade_out = Integer.parseInt(args[6]);
						float alpha = Float.parseFloat(args[7]);
						float g_x = Float.parseFloat(args[8]);
						float g_y = Float.parseFloat(args[9]);
						float s_x = Float.parseFloat(args[10]);
						float s_y = Float.parseFloat(args[11]);
						int color = Integer.parseInt(args[12], 16);
						boolean shadow = args[13].equals("0") ? false : true;
						String message = getArray(args, 14);
						for(Player player : players)
						{
							LinmaluNetwork.getInstance().sendDrawTextMessage(player, id, fade_in, time, fade_out, alpha, g_x, g_y, s_x, s_y, color, shadow, message);
						}
						sender.sendMessage(Main.getMain().getTitle() + ChatColor.GREEN + "텍스트를 그렸습니다.");
						return true;
					}
					else if(args.length == 4 && args[1].equals("지우기"))
					{
						int id = Integer.parseInt(args[3]);
						for(Player player : players)
						{
							LinmaluNetwork.getInstance().sendEraseObjectMessage(player, id);
						}
						sender.sendMessage(Main.getMain().getTitle() + ChatColor.GREEN + "텍스트를 지웠습니다.");
						return true;
					}
					else if(args.length == 3 && args[1].equals("모두지우기"))
					{
						for(Player player : players)
						{
							LinmaluNetwork.getInstance().sendClearObjectMessage(player);
						}
						sender.sendMessage(Main.getMain().getTitle() + ChatColor.GREEN + "텍스트를 모두 지웠습니다.");
						return true;
					}
					else if(args.length == 8 && args[1].equals("시간"))
					{
						int id = Integer.parseInt(args[3]);
						int fade_in = Integer.parseInt(args[4]);
						int time = Integer.parseInt(args[5]);
						int fade_out = Integer.parseInt(args[6]);
						float alpha = Float.parseFloat(args[7]);
						for(Player player : players)
						{
							LinmaluNetwork.getInstance().sendTimeTextMessage(player, id, fade_in, time, fade_out, alpha);
						}
						sender.sendMessage(Main.getMain().getTitle() + ChatColor.GREEN + "텍스트의 시간을 변경했습니다.");
						return true;
					}
					else if(args.length == 8 && args[1].equals("위치"))
					{
						int id = Integer.parseInt(args[3]);
						float g_x = Float.parseFloat(args[4]);
						float g_y = Float.parseFloat(args[5]);
						float s_x = Float.parseFloat(args[6]);
						float s_y = Float.parseFloat(args[7]);
						for(Player player : players)
						{
							LinmaluNetwork.getInstance().sendLocationTextMessage(player, id, g_x, g_y, s_x, s_y);
						}
						sender.sendMessage(Main.getMain().getTitle() + ChatColor.GREEN + "텍스트의 위치를 변경했습니다.");
						return true;
					}
					else if(args.length >= 5 && args[1].equals("변경"))
					{
						int id = Integer.parseInt(args[3]);
						String message = getArray(args, 4);
						for(Player player : players)
						{
							LinmaluNetwork.getInstance().sendChangeTextMessage(player, id, message);
						}
						sender.sendMessage(Main.getMain().getTitle() + ChatColor.GREEN + "텍스트를 변경했습니다.");
						return true;
					}
					else if(args.length == 6 && args[1].equals("색상"))
					{
						int id = Integer.parseInt(args[3]);
						int color = Integer.parseInt(args[4], 16);
						boolean shadow = args[5].equals("0") ? false : true;
						for(Player player : players)
						{
							LinmaluNetwork.getInstance().sendColorTextMessage(player, id, color, shadow);
						}
						sender.sendMessage(Main.getMain().getTitle() + ChatColor.GREEN + "텍스트의 색상을 변경했습니다.");
						return true;
					}
				}
				else if(args.length >= 3 && args[0].equals("렌더링"))
				{
					List<Player> players = LinmaluPlayer.getPlayers(args[2]);
					if(players.size() == 0)
					{
						sender.sendMessage(Main.getMain().getTitle() + ChatColor.YELLOW + "플레이어가 접속중이 아닙니다.");
						return true;
					}
					if(args.length == 4 && args[1].equals("지우기"))
					{
						LinmaluRenderType type = LinmaluRenderType.getLinmaluRenderType(args[3]);
						if(type == LinmaluRenderType.NONE)
						{
							sender.sendMessage(Main.getMain().getTitle() + ChatColor.GREEN + "타입이 올바르지 않습니다.");
						}
						else
						{
							for(Player player : players)
							{
								LinmaluNetwork.getInstance().sendEraseRenderMessage(player, type);
							}
							sender.sendMessage(Main.getMain().getTitle() + ChatColor.GREEN + "랜더링을 지웠습니다.");
						}
						return true;
					}
					else if(args.length == 4 && args[1].equals("그리기"))
					{
						LinmaluRenderType type = LinmaluRenderType.getLinmaluRenderType(args[3]);
						if(type == LinmaluRenderType.NONE)
						{
							sender.sendMessage(Main.getMain().getTitle() + ChatColor.GREEN + "타입이 올바르지 않습니다.");
						}
						else
						{
							for(Player player : players)
							{
								LinmaluNetwork.getInstance().sendDrawRenderMessage(player, type);
							}
							sender.sendMessage(Main.getMain().getTitle() + ChatColor.GREEN + "랜더링을 그렸습니다.");
						}
						return true;
					}
					else if(args.length == 3 && args[1].equals("초기화"))
					{
						for(Player player : players)
						{
							LinmaluNetwork.getInstance().sendResetRenderMessage(player);
						}
						sender.sendMessage(Main.getMain().getTitle() + ChatColor.GREEN + "랜더링을 초기화했습니다.");
						return true;
					}
				}
				else if(args.length == 1 && args[0].equals("모드확인"))
				{
					sender.sendMessage(Main.getMain().getTitle() + ChatColor.GREEN + " = = = = = [ Linmalu Library Mod ] = = = = =");
					LinmaluNetwork.getInstance().getPlayers().forEach((k, v) ->
					{
						sender.sendMessage(Main.getMain().getTitle() + ChatColor.GOLD + Bukkit.getOfflinePlayer(k).getName() + ChatColor.RESET + " : " + ChatColor.YELLOW + v);
					});
					return true;
				}
				sender.sendMessage(ChatColor.GREEN + " = = = = = [ Linmalu Gui ] = = = = =");
				LinmaluTellraw.sendChat(sender, "/" + label + " 이미지 ", ChatColor.GOLD + "/" + label + " 이미지" + ChatColor.GRAY + " : 이미지 설정");
				LinmaluTellraw.sendChat(sender, "/" + label + " 텍스트 ", ChatColor.GOLD + "/" + label + " 텍스트" + ChatColor.GRAY + " : 텍스트 설정");
				LinmaluTellraw.sendChat(sender, "/" + label + " 렌더링 ", ChatColor.GOLD + "/" + label + " 렌더링" + ChatColor.GRAY + " : 렌더링 설정");
				LinmaluTellraw.sendChat(sender, "/" + label + " 모드확인 ", ChatColor.GOLD + "/" + label + " 모드확인" + ChatColor.GRAY + " : 모드 확인");
				LinmaluTellraw.sendChat(sender, "/" + label + " 리로드 ", ChatColor.GOLD + "/" + label + " 리로드" + ChatColor.GRAY + " : 리로드");
				sender.sendMessage(ChatColor.YELLOW + "명령어정보 : " + ChatColor.WHITE + "http://blog.linmalu.com/221322587062");
				sender.sendMessage(ChatColor.YELLOW + "제작자 : " + ChatColor.AQUA + "린마루(Linmalu)" + ChatColor.WHITE + " - http://blog.linmalu.com");
				LinmaluServer.version(Main.getMain(), sender);
			}
			catch(NumberFormatException e)
			{
				sender.sendMessage(Main.getMain().getTitle() + ChatColor.YELLOW + "명령어의 형식이 올바르지 않습니다.");
			}
		}
		else
		{
			sender.sendMessage(ChatColor.RED + "권한이 없습니다.");
		}
		return true;
	}

	private String getArray(String[] args, int start)
	{
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for(int i = start; i < args.length; i++)
		{
			if(first)
			{
				first = false;
			}
			else
			{
				sb.append(" ");
			}
			sb.append(args[i]);
		}
		return ChatColor.translateAlternateColorCodes('&', sb.toString());
	}
}
