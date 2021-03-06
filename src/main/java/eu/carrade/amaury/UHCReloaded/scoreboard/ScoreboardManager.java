/*
 * Copyright or © or Copr. Amaury Carrade (2014 - 2016)
 *
 * http://amaury.carrade.eu
 *
 * This software is governed by the CeCILL-B license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL-B
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-B license and that you accept its terms.
 */
package eu.carrade.amaury.UHCReloaded.scoreboard;

import eu.carrade.amaury.UHCReloaded.UHCReloaded;
import eu.carrade.amaury.UHCReloaded.UHConfig;
import fr.zcraft.zlib.components.scoreboard.Sidebar;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criterias;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


public class ScoreboardManager
{
    private UHCReloaded p;

    private Map<UUID, SidebarPlayerCache> sidebarCache = new ConcurrentHashMap<>();

    private final Scoreboard sb;
    private Sidebar sidebar = null;


    public ScoreboardManager(UHCReloaded p)
    {
        this.p = p;
        this.sb = Bukkit.getServer().getScoreboardManager().getNewScoreboard();

        UHCReloaded.get().getServer().getPluginManager().registerEvents(new ScoreboardListener(), UHCReloaded.get());


        // Initialization of the scoreboard (match info in the sidebar)
        if (UHConfig.SCOREBOARD.ENABLED.get())
        {
            sidebar = new GameSidebar();

            for (Player player : Bukkit.getOnlinePlayers())
                sidebar.addRecipient(player);

            sidebar.runAutoRefresh(true);
        }

        // Initialization of the scoreboard (health in players' list)
        if (UHConfig.SCOREBOARD.HEALTH.get())
        {
            Objective healthObjective = sb.registerNewObjective("Health", Criterias.HEALTH);
            healthObjective.setDisplayName("Health");
            healthObjective.setDisplaySlot(DisplaySlot.PLAYER_LIST);

            // Sometimes, the health is initialized to 0. This is used to fix this.
            updateHealthScore();
        }
        else
        {
            sb.clearSlot(DisplaySlot.PLAYER_LIST); // Just in case
        }

        // Initialization of the sidebar cache
        for (Player player : Bukkit.getOnlinePlayers())
        {
            getSidebarPlayerCache(player.getUniqueId()); // Initializes the object.
        }
    }


    /**
     * Updates the health score for all players.
     */
    public void updateHealthScore()
    {
        for (final Player player : p.getServer().getOnlinePlayers())
        {
            updateHealthScore(player);
        }
    }

    /**
     * Updates the health score for the given player.
     *
     * @param player The player to update.
     */
    public void updateHealthScore(final Player player)
    {
        if (player.getHealth() != 1d) // Prevents killing the player
        {
            player.setHealth(player.getHealth() - 1);

            Bukkit.getScheduler().runTaskLater(p, new Runnable()
            {
                @Override
                public void run()
                {
                    if (player.getHealth() <= 19d) // Avoids an IllegalArgumentException
                    {
                        player.setHealth(player.getHealth() + 1);
                    }
                }
            }, 3L);
        }
    }

    /**
     * Tells the player's client to use this scoreboard.
     *
     * @param p The player.
     */
    public void setScoreboardForPlayer(Player p)
    {
        p.setScoreboard(sb);
        sidebar.addRecipient(p);
    }

    /**
     * Returns the title of the scoreboard, truncated at 32 characters.
     *
     * @return The name
     */
    public String getScoreboardName()
    {
        String s = ChatColor.translateAlternateColorCodes('&', UHConfig.SCOREBOARD.TITLE.get());
        return s.substring(0, Math.min(s.length(), 32));
    }

    /**
     * Returns the internal scoreboard.
     *
     * @return The internal scoreboard.
     */
    public Scoreboard getScoreboard()
    {
        return sb;
    }

    /**
     * Returns the cached data about the given player.
     *
     * @param id The player's UUID.
     * @return The cached data, created on the fly if needed.
     */
    public SidebarPlayerCache getSidebarPlayerCache(UUID id)
    {
        SidebarPlayerCache cache = sidebarCache.get(id);

        if(cache != null)
            return cache;

        cache = new SidebarPlayerCache(id);

        sidebarCache.put(id, cache);
        return cache;
    }

    public Map<UUID, SidebarPlayerCache> getAllSidebarPlayerCache()
    {
        return sidebarCache;
    }
}
