package me.blockynights.user;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.milkbowl.vault.permission.Permission;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;


public class UserLogin extends JavaPlugin implements Listener {
		
	   static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	   static final String DB_URL = "jdbc:mysql://localhost/zperm";
	   static final String USER = "user";
	   static final String PASS = "password";
	  
	   public static Permission perms = null;
	   private Map<Player, String> flight = new HashMap<Player, String>();
	   
	   private boolean setupPermissions()
	   {
	     RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
	     perms = (Permission)rsp.getProvider();
	     return perms != null;
	   }
	
	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		   setupPermissions();
		   this.saveDefaultConfig();
	}

	@Override
	public void onDisable() {
		
	}
	
	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent event) {
		final Player p = event.getPlayer();
		String Pname = p.getDisplayName();
		String user = p.getUniqueId().toString();
		String pass = getSaltString();
		long unixTime = System.currentTimeMillis() / 1000L;
		final String address=p.getAddress().getAddress().getHostAddress();
		
		   Connection conn = null;
		   Statement stmt = null;
		   try{
			      Class.forName("com.mysql.jdbc.Driver");
			      conn = DriverManager.getConnection(DB_URL,USER,PASS);
			      stmt = conn.createStatement();
			      String sql;
			      sql = "SELECT id,uiid FROM users where uiid='" + user + "'";
			      ResultSet rs = stmt.executeQuery(sql);
			      if (rs.next()) {
			      } else { 
			      		sql = "INSERT INTO users(uiid,original_name,latest_name,auth_kode,create_date,create_ip,password,authed)"
			      				+ "VALUES ('" + user + "','" + Pname + "','" + Pname + "','" + pass + "','" + unixTime + "','" + address + "','" + pass + "','yes')";
			      		stmt.executeUpdate(sql);
			      		}
			      rs.close();stmt.close();conn.close();
		   }catch(SQLException se){se.printStackTrace();}catch(Exception e){e.printStackTrace();}
	   		finally{try{if(stmt!=null)stmt.close();}catch(SQLException se2){}try{if(conn!=null)conn.close();}catch(SQLException se){se.printStackTrace();}}}
	
	
	@EventHandler
	private void toggleFlight(PlayerToggleFlightEvent event) {
		Player p = (Player) event.getPlayer();
		if (flight.get(p) != null) {
				event.setCancelled(true);
				int it = Integer.parseInt(flight.get(p));
				double x = getConfig().getDouble(it+".loc.x");
				double y = getConfig().getDouble(it+".loc.y");
				double z = getConfig().getDouble(it+".loc.z");
				World world = Bukkit.getWorld(getConfig().getString(it+".loc.world"));
				Location loc = new Location(world, x, y, z);
				p.teleport(loc);
		}
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if ((cmd.getName().equalsIgnoreCase("tutorial") && sender instanceof Player) && args.length == 0) {
			Player p = (Player) sender;
			int it = 1;
			startTutorial(p,it);
		}
		if ((cmd.getName().equalsIgnoreCase("tutorial") && sender instanceof Player) && args.length > 0) {
			if (sender.isOp()) {
				Player player = (Player) sender; 
				if (args[0].equals("set") && args.length == 2) {
					Location loc = player.getLocation();
					int nr = Integer.parseInt(args[1]);
					int x = loc.getBlockX();
					int y = loc.getBlockY();
					int z = loc.getBlockZ();
					float yaw = loc.getYaw();
					float pitch = loc.getPitch();
					String world = loc.getWorld().getName();
					getConfig().set(nr+".loc.x",x);
					getConfig().set(nr+".loc.y",y);
					getConfig().set(nr+".loc.z",z);
					getConfig().set(nr+".loc.yaw",yaw);
					getConfig().set(nr+".loc.pitch",pitch);
					getConfig().set(nr+".loc.world",world);
					getConfig().set(nr+".top","lol");
					getConfig().set(nr+".bot","lolleren");
					getConfig().set(+nr+".time","5");
					this.saveConfig();
					System.out.print("yiii");
				}
				if (args[0].equals("set") && args.length == 1) {
					Location loc = player.getLocation();
					int nr = getConfig().getConfigurationSection("").getKeys(false).size();
					nr = nr + 1;
					int x = loc.getBlockX();
					int y = loc.getBlockY();
					int z = loc.getBlockZ();
					float yaw = loc.getYaw();
					float pitch = loc.getPitch();
					String world = loc.getWorld().getName();
					getConfig().set(nr+".loc.x",x);
					getConfig().set(nr+".loc.y",y);
					getConfig().set(nr+".loc.z",z);
					getConfig().set(nr+".loc.yaw",yaw);
					getConfig().set(nr+".loc.pitch",pitch);
					getConfig().set(nr+".loc.world",world);
					getConfig().set(nr+".top","lol");
					getConfig().set(nr+".bot","lolleren");
					getConfig().set(+nr+".time","5");
					this.saveConfig();
				}
				if (args[0].equals("edit") && args.length > 3) {
					String s = StringUtils.join(ArrayUtils.subarray(args,3,args.length)," ");
					System.out.print(s);
					getConfig().set(args[1]+"."+args[2],s);
					this.saveConfig();
				}
			}
		}
		return true;
	}
	
	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event){
	    if(event.getRightClicked().getType() != EntityType.VILLAGER) return;
	    Villager v = (Villager) event.getRightClicked();
	    String vname = v.getCustomName();
	    if (vname != null) {
		    if (vname.equalsIgnoreCase("Tutor")) {
		    Player p = (Player) event.getPlayer();
		    event.setCancelled(true);
			int it = 1;
			startTutorial(p,it);
		    }
	    }
	}
	
	protected String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 5) {
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;
    }
	public void runnable(Player p,int interval,int it) {
		final Player p1 = p;
		final int it1 = it;
        this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            public void run() {
            	startFlight(p1,it1);
            }
        }, 200L);
	}
	
	public void startTutorial(Player p,int it){
		 for (Player p1 : Bukkit.getServer().getOnlinePlayers()) {
		     p1.hidePlayer(p);
		 }
		p.setAllowFlight(true);
		p.setFlying(true);
		p.setFlySpeed(0);
		startFlight(p,it);
		flight.put(p, "1");
	 	}	

	public void startFlight(Player p,int it){
		int nr = getConfig().getConfigurationSection("").getKeys(false).size();
		int interval = getConfig().getInt(it+".time") *20;
		double x = getConfig().getDouble(it+".loc.x");
		double y = getConfig().getDouble(it+".loc.y");
		double z = getConfig().getDouble(it+".loc.z");
		double yaw = getConfig().getDouble(it+".loc.yaw");
		float ya = (float) yaw;
		double pitch = getConfig().getDouble(it+".loc.pitch");
		float pi = (float) pitch;
		World world = Bukkit.getWorld(getConfig().getString(it+".loc.world"));
		Location loc = new Location(world, x, y, z);
		loc.setPitch(pi);
		loc.setYaw(ya);
		p.teleport(loc);
		String top = getConfig().getString(it+".top");
		String bot = getConfig().getString(it+".bot");
		it++;
		String itstring = new Integer(it).toString();
		Title.sendTitle(p, "[{text:'"+top+"',color:gold}]", "[{text:'"+bot+"',color:green},{text:'',color:green}]", 15, 60, 15);
		if (it <= nr) {
			flight.put(p, itstring);
			runnable(p,interval,it);
	}
		else { endTutorial(p); }
	}
		
	private void endTutorial(Player p) {
		 for (Player p1 : Bukkit.getServer().getOnlinePlayers()) {
		     p1.showPlayer(p);
		 }
		p.setAllowFlight(false);
		p.setFlying(false);
		p.setFlySpeed(1);
		flight.remove(p);
		 int i = 0;
		 int cirkle = 0;
		runn(p,i,cirkle);
		if (p.hasPermission("an.solar")) {
		      perms.playerRemoveGroup(p, "Solar");
			  perms.playerAddGroup(p, "Lunar");
		}
	}
	
	private void runn(Player p, int amount,int cirkle) {
		final Player p1 = p;
		final int amount1 = amount;
		final int cirkle1 = cirkle;
        this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            public void run() {
            	createFirework(p1,amount1,cirkle1);
            }
        }, 2L);
	}
	
	private void createFirework(Player p,int amount,int cirkle) {
		int max = 100;
		Location loc = p.getLocation();
		if (cirkle == 0) { cirkle = 1; }
		if (cirkle == 9) { cirkle = 1; }
		if (cirkle == 8) { cirkle = 9;  loc.add(7,0,-7); }
		if (cirkle == 7) { cirkle = 8;  loc.add(0,0,-7); }
		if (cirkle == 6) { cirkle = 7;  loc.add(-7,0,-7); }
		if (cirkle == 5) { cirkle = 6;  loc.add(-7,0,0); }
		if (cirkle == 4) { cirkle = 5;  loc.add(-7,0,7); }
		if (cirkle == 3) { cirkle = 4;  loc.add(0,0,7); }
		if (cirkle == 2) { cirkle = 3;  loc.add(7,0,7); }
		if (cirkle == 1) { cirkle = 2;  loc.add(7,0,0); }
		if (amount < max) {
        Firework fw = (Firework) p.getWorld().spawnEntity(loc, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();
        Random r = new Random();  
        int rt = r.nextInt(5) + 1;
        Type type = Type.BALL;      
        if (rt == 1) type = Type.BALL;
        if (rt == 2) type = Type.BALL_LARGE;
        if (rt == 3) type = Type.BURST;
        if (rt == 4) type = Type.CREEPER;
        if (rt == 5) type = Type.STAR;
        int r1i = r.nextInt(17) + 1;
        int r2i = r.nextInt(17) + 1;
        Color c1 = getColor(r1i);
        Color c2 = getColor(r2i);
        FireworkEffect effect = FireworkEffect.builder().flicker(r.nextBoolean()).withColor(c1).withFade(c2).with(type).trail(r.nextBoolean()).build();
        fwm.addEffect(effect);
        int rp = r.nextInt(2) + 1;
        fwm.setPower(rp);
        fw.setFireworkMeta(fwm);
        amount++;
        runn(p,amount,cirkle);
		}
	}
	
	
	private Color getColor(int i) {
		Color c = null;
		if(i==1){
		c=Color.AQUA;
		}
		if(i==2){
		c=Color.BLACK;
		}
		if(i==3){
		c=Color.BLUE;
		}
		if(i==4){
		c=Color.FUCHSIA;
		}
		if(i==5){
		c=Color.GRAY;
		}
		if(i==6){
		c=Color.GREEN;
		}
		if(i==7){
		c=Color.LIME;
		}
		if(i==8){
		c=Color.MAROON;
		}
		if(i==9){
		c=Color.NAVY;
		}
		if(i==10){
		c=Color.OLIVE;
		}
		if(i==11){
		c=Color.ORANGE;
		}
		if(i==12){
		c=Color.PURPLE;
		}
		if(i==13){
		c=Color.RED;
		}
		if(i==14){
		c=Color.SILVER;
		}
		if(i==15){
		c=Color.TEAL;
		}
		if(i==16){
		c=Color.WHITE;
		}
		if(i==17){
		c=Color.YELLOW;
		}
		 
		return c;
		}
}
