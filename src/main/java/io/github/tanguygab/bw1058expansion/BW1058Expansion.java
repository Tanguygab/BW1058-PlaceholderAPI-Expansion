package io.github.tanguygab.bw1058expansion;

import com.andrei1058.bedwars.api.BedWars;
import com.andrei1058.bedwars.api.arena.IArena;
import com.andrei1058.bedwars.api.arena.team.ITeam;
import com.andrei1058.bedwars.api.language.Language;
import com.andrei1058.bedwars.api.party.Party;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.clip.placeholderapi.expansion.Taskable;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class BW1058Expansion extends PlaceholderExpansion implements Taskable {

    private BedWars api;

    @Override
    public String getIdentifier() {
        return "bw1058plus";
    }

    @Override
    public String getAuthor() {
        return "Tanguygab";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String getRequiredPlugin() {
        return "BedWars1058";
    }

    @Override
    public  List<String> getPlaceholders() {
        List<String> list = new ArrayList<>(Arrays.asList("team_letter",
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
                "players",
                "players_amount",
                "party_has",
                "party_members",
                "party_members_amount",
                "party_in_yours_<player>",
                "party_in_his_<player>",
                "party_is_owner"));
        for (String placeholder : list)
            list.set(list.indexOf(placeholder),"%bw1058+_" + placeholder + "%");
        return list;
    }

    @Override
    public void start() {
        api = Bukkit.getServicesManager().getRegistration(BedWars .class).getProvider();
    }

    @Override
    public void stop() {

    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (player == null) return "";
        Player p = player.getPlayer();
        if (p == null) return "";

        //party placeholders
        Party party = api.getPartyUtil();
        if (params.equalsIgnoreCase("party_has"))
            return party.hasParty(p)+"";
        if (params.startsWith("party_members")) {
            String output = "";
            List<Player> list = new ArrayList<>(party.getMembers(p));
            if (params.equalsIgnoreCase("party_members_amount"))
                output = list.size()+"";
            else if (params.equalsIgnoreCase("party_members")) {
                for (Player pl : list) {
                    output = output + pl.getName();
                    if (list.indexOf(pl) != list.size() - 1) output = output + ", ";
                }
            }
            return output;
        }
        if (params.startsWith("party_in_yours_")) {
            Player p2 = Bukkit.getServer().getPlayer(params.replace("party_in_yours_",""));
            if (p2 == null) return "false";
            return party.isMember(p,p2)+"";
        }
        if (params.startsWith("party_in_his_")) {
            Player p2 = Bukkit.getServer().getPlayer(params.replace("party_in_his_",""));
            if (p2 == null) return "false";
            return party.isMember(p2,p)+"";
        }
        if (params.startsWith("party_is_owner")) {
            if (params.equalsIgnoreCase("party_is_owner")) return party.isOwner(p)+"";
            if (params.startsWith("party_is_owner_")) {
                Player p2 = Bukkit.getServer().getPlayer(params.replace("party_is_owner_", ""));
                if (p2 == null) return "false";
                return party.isOwner(p2) + "";
            }
        }

        Language lang = api.getPlayerLanguage(p);
        if (params.startsWith("lang"))
            return lang.getLangName();

        //placeholdesr only available in an arena
        IArena arena = api.getArenaUtil().getArenaByPlayer(p);
        if (arena == null) return "";

        //team placeholders
        if (params.startsWith("team_status")) {
            ITeam team;
            if (params.equalsIgnoreCase("team_status"))
                team = arena.getTeam(p);
            else team = arena.getTeam(params.replace("team_status_", ""));
            if (team != null)
                return !team.isBedDestroyed() ? lang.getString("format-sb-team-alive") : !team.getMembers().isEmpty() ? team.getMembers().size()+"" : lang.getString("format-sb-team-eliminated");
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
                return team.getMembers().size()+"";
        }
        if (params.startsWith("team_players")) {
            ITeam team;
            if (params.equalsIgnoreCase("team_players"))
                team = arena.getTeam(p);
            else team = arena.getTeam(params.replace("team_players_", ""));
            if (team != null) {
                String output = "";
                List<Player> list = new ArrayList<>(team.getMembers());
                for (Player pl : list) {
                    output = output+pl.getName();
                    if (list.indexOf(pl) != list.size()-1) output = output+", ";
                }
                return output;
            }
        }
        //arena placeholders
        if (params.equalsIgnoreCase("arena_nextevent_name")) {
            return arena.getNextEvent().toString().toLowerCase().replace("_"," ");
        }
        if (params.equalsIgnoreCase("arena_nextevent_time")) {
            return arena.getNextEvent().toString().toLowerCase().replace("_"," ");
        }
        if (params.equalsIgnoreCase("arena_name"))
            return arena.getArenaName();
        if (params.equalsIgnoreCase("arena_group"))
            return arena.getGroup();
        if (params.equalsIgnoreCase("arena_world"))
            return arena.getWorldName();
        if (params.equalsIgnoreCase("arena_status_plocale"))
            return arena.getDisplayStatus(lang);
        if (params.equalsIgnoreCase("arena_status"))
            return arena.getDisplayStatus(api.getDefaultLang());
        //player placeholders
        if (params.equalsIgnoreCase("player_kills"))
            return arena.getPlayerKills(p,false)+"";
        if (params.equalsIgnoreCase("player_kills_total"))
            return arena.getPlayerKills(p,true)+arena.getPlayerKills(p,false)+"";
        if (params.equalsIgnoreCase("player_kills_final"))
            return arena.getPlayerKills(p,true)+"";
        if (params.equalsIgnoreCase("player_deaths"))
            return arena.getPlayerDeaths(p,false)+"";
        if (params.equalsIgnoreCase("player_deaths_total"))
            return arena.getPlayerDeaths(p,true)+arena.getPlayerDeaths(p,false)+"";
        if (params.equalsIgnoreCase("player_deaths_final"))
            return arena.getPlayerDeaths(p,true)+"";
        if (params.equalsIgnoreCase("player_beds"))
            return arena.getPlayerBedsDestroyed(p)+"";
        if (params.startsWith("players")) {
            String output = "";
            List<Player> list = new ArrayList<>(arena.getPlayers());
            if (params.equalsIgnoreCase("players_amount"))
                output = list.size()+"";
            else if (params.equalsIgnoreCase("players")) {
                for (Player pl : list) {
                    output = output + pl.getName();
                    if (list.indexOf(pl) != list.size() - 1) output = output + ", ";
                }
            }
            return output;
        }

        return "";
    }
}
