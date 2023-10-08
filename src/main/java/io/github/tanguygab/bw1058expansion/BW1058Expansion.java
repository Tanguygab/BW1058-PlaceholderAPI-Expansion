package io.github.tanguygab.bw1058expansion;

import com.andrei1058.bedwars.api.BedWars;
import com.andrei1058.bedwars.api.arena.IArena;
import com.andrei1058.bedwars.api.arena.team.ITeam;
import com.andrei1058.bedwars.api.language.Language;
import com.andrei1058.bedwars.api.language.Messages;
import com.andrei1058.bedwars.api.party.Party;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.clip.placeholderapi.expansion.Taskable;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import javax.annotation.Nonnull;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public final class BW1058Expansion extends PlaceholderExpansion implements Taskable {

    private BedWars api;
    private final List<String> placeholders;
    private final SimpleDateFormat nextEventFormat = new SimpleDateFormat("mm:ss");

    public BW1058Expansion() {
        placeholders = Stream.of("team_letter",
                "team_color",
                "team_status",
                "team_players_amount",
                "team_players",
                "team_letter_<team>",
                "team_color_<team>",
                "team_status_<team>",
                "team_players_amount_<team>",
                "team_players_<team>",
                "arena_nextevent",
                "arena_nextevent_name",
                "arena_nextevent_time",
                "arena_name",
                "arena_display_name",
                "arena_group",
                "arena_world",
                "arena_status_plocale",
                "arena_status",
                "player_kills",
                "player_kills_final",
                "player_kills_total",
                "player_deaths",
                "player_deaths_final",
                "player_deaths_total",
                "player_beds",
                "status_color",
                "status_letter",
                "players",
                "players_amount",
                "party_has",
                "party_members",
                "party_members_amount",
                "party_in_yours_<player>",
                "party_in_his_<player>",
                "party_is_owner").map(p->"%bw1058plus_"+p+"%").collect(Collectors.toList());
    }

    @Override
    public @Nonnull String getIdentifier() {
        return "bw1058plus";
    }

    @Override
    public @Nonnull String getAuthor() {
        return "Tanguygab";
    }

    @Override
    public @Nonnull String getVersion() {
        return "1.2.2";
    }

    @Override
    public String getRequiredPlugin() {
        return "BedWars1058";
    }

    @Override
    public @Nonnull List<String> getPlaceholders() {
        return placeholders;
    }

    @Override
    public void start() {
        RegisteredServiceProvider<BedWars> r = Bukkit.getServicesManager().getRegistration(BedWars.class);
        if (r != null) api = r.getProvider();
    }

    @Override
    public void stop() {

    }

    @Override
    public String onRequest(OfflinePlayer player, @Nonnull String params) {
        if (player == null) return "";
        Player p = player.getPlayer();
        if (p == null) return "";

        //party placeholders
        Party party = api.getPartyUtil();
        if (params.equalsIgnoreCase("party_has"))
            return String.valueOf(party.hasParty(p));
        if (params.startsWith("party_members")) {
            StringBuilder output = new StringBuilder();
            List<Player> list = new ArrayList<>(party.getMembers(p));
            if (params.equalsIgnoreCase("party_members_amount"))
                output = new StringBuilder(String.valueOf(list.size()));
            else if (params.equalsIgnoreCase("party_members")) {
                for (Player pl : list) {
                    output.append(pl.getName());
                    if (list.indexOf(pl) != list.size() - 1) output.append(", ");
                }
            }
            return output.toString();
        }
        if (params.startsWith("party_in_yours_")) {
            Player p2 = Bukkit.getServer().getPlayer(params.replace("party_in_yours_",""));
            if (p2 == null) return "false";
            return String.valueOf(party.isMember(p, p2));
        }
        if (params.startsWith("party_in_his_")) {
            Player p2 = Bukkit.getServer().getPlayer(params.replace("party_in_his_",""));
            if (p2 == null) return "false";
            return String.valueOf(party.isMember(p2, p));
        }
        if (params.startsWith("party_is_owner")) {
            if (params.equalsIgnoreCase("party_is_owner")) return String.valueOf(party.isOwner(p));
            if (params.startsWith("party_is_owner_")) {
                Player p2 = Bukkit.getServer().getPlayer(params.replace("party_is_owner_", ""));
                if (p2 == null) return "false";
                return String.valueOf(party.isOwner(p2));
            }
        }

        Language lang = api.getPlayerLanguage(p);
        if (params.startsWith("lang"))
            return lang.getLangName();

        //placeholders only available in an arena
        IArena arena = api.getArenaUtil().getArenaByPlayer(p);
        if (arena == null) return "";

        //team placeholders
        if (params.startsWith("team_status")) {
            ITeam team;
            if (params.equalsIgnoreCase("team_status"))
                team = arena.getTeam(p);
            else team = arena.getTeam(params.replace("team_status_", ""));
            if (team != null)
                return !team.isBedDestroyed() ? lang.getString("format-sb-team-alive") : !team.getMembers().isEmpty() ? String.valueOf(team.getMembers().size()) : lang.getString("format-sb-team-eliminated");
        }
        if (params.startsWith("team_color")) {
            ITeam team;
            if (params.equalsIgnoreCase("team_color"))
                team = arena.getTeam(p);
            else team = arena.getTeam(params.replace("team_color_", ""));
            if (team != null)
                return team.getColor().chat().toString();
        }
        if (params.startsWith("team_letter")) {
            ITeam team;
            if (params.equalsIgnoreCase("team_letter"))
                team = arena.getTeam(p);
            else team = arena.getTeam(params.replace("team_letter_", ""));
            if (team != null)
                return team.getName().substring(0,1).toUpperCase();
        }
        if (params.startsWith("team_players_amount")) {
            ITeam team;
            if (params.equalsIgnoreCase("team_players_amount"))
                team = arena.getTeam(p);
            else team = arena.getTeam(params.replace("team_players_amount_", ""));
            if (team != null)
                return String.valueOf(team.getMembers().size());
        }
        if (params.startsWith("team_players")) {
            ITeam team;
            if (params.equalsIgnoreCase("team_players"))
                team = arena.getTeam(p);
            else team = arena.getTeam(params.replace("team_players_", ""));
            if (team != null) {
                StringBuilder output = new StringBuilder();
                List<Player> list = new ArrayList<>(team.getMembers());
                for (Player pl : list) {
                    output.append(pl.getName());
                    if (list.indexOf(pl) != list.size()-1) output.append(", ");
                }
                return output.toString();
            }
        }
        //arena placeholders
        switch (params) {
            case "arena_nextevent_name": return arena.getNextEvent().toString().toLowerCase().replace("_"," ");
            case "arena_nextevent_time": return String.valueOf(getNextEventTime(arena));
            case "arena_nextevent_time_formatted": return nextEventFormat.format(new Date(getNextEventTime(arena)*1000L));
            case "arena_name": return arena.getArenaName();
            case "arena_display_name": return arena.getDisplayName();
            case "arena_group": return arena.getGroup();
            case "arena_world": return arena.getWorldName();
            case "arena_status_plocale": return arena.getDisplayStatus(lang);
            case "arena_status": return arena.getDisplayStatus(api.getDefaultLang());
            //player placeholders
            case "player_kills": return String.valueOf(arena.getPlayerKills(p, false));
            case "player_kills_total": return String.valueOf(arena.getPlayerKills(p,true)+arena.getPlayerKills(p,false));
            case "player_kills_final": return String.valueOf(arena.getPlayerKills(p, true));
            case "player_deaths": return String.valueOf(arena.getPlayerDeaths(p, false));
            case "player_deaths_total": return String.valueOf(arena.getPlayerDeaths(p,true)+arena.getPlayerDeaths(p,false));
            case "player_deaths_final": return String.valueOf(arena.getPlayerDeaths(p, true));
            case "player_beds": return String.valueOf(arena.getPlayerBedsDestroyed(p));
            case "status_color": return (arena.isSpectator(p) ? ChatColor.GRAY : arena.getTeam(p).getColor().chat()).toString();
            case "status_letter": return arena.isSpectator(p) ? getSpectatorLetter(lang) : arena.getTeam(p).getName().substring(0,1).toUpperCase();
        }
        if (params.startsWith("players")) {
            StringBuilder output = new StringBuilder();
            List<Player> list = new ArrayList<>(arena.getPlayers());
            if (params.equalsIgnoreCase("players_amount"))
                output = new StringBuilder(String.valueOf(list.size()));
            else if (params.equalsIgnoreCase("players")) {
                for (Player pl : list) {
                    output.append(pl.getName());
                    if (list.indexOf(pl) != list.size() - 1) output.append(", ");
                }
            }
            return output.toString();
        }

        return "";
    }

    private String getSpectatorLetter(Language lang) {
        String letter = lang.m("format-papi-player-spectator-letter");
        return letter.equals("MISSING_LANG") ? "S" : letter;
    }

    private int getNextEventTime(IArena arena) {
        switch (arena.getNextEvent()) {
            case EMERALD_GENERATOR_TIER_II:
            case EMERALD_GENERATOR_TIER_III: return arena.getUpgradeEmeraldsCount();
            case DIAMOND_GENERATOR_TIER_II:
            case DIAMOND_GENERATOR_TIER_III: return arena.getUpgradeDiamondsCount();
            case BEDS_DESTROY: return arena.getPlayingTask().getBedsDestroyCountdown();
            case ENDER_DRAGON: return arena.getPlayingTask().getDragonSpawnCountdown();
            case GAME_END: return arena.getPlayingTask().getGameEndCountdown();
            default: return 0;
        }
    }
}
