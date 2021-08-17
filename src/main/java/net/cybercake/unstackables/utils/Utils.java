package net.cybercake.unstackables.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Utils {
    public static String chat (String message){
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String chat (Character altColorChar, String message) { return ChatColor.translateAlternateColorCodes(altColorChar, message); }

    public static String getCharacters(int beginCharacter, int endCharacter, String string) {
        if(beginCharacter < 0) {
            return null;
        }

        if(endCharacter > string.length()) {
            return null;
        } else {
            return string.length() < endCharacter ? string : string.substring(beginCharacter, endCharacter);
        }
    }

    public static long getUnix() {
        return Instant.now().getEpochSecond();
    }

    public static String getFormattedDate(String pattern) {
        Date dNow = new Date( );
        SimpleDateFormat ft = new SimpleDateFormat(pattern);
        return ft.format(dNow);

    }

    public static String formatLong(long longNumber) {
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setGroupingUsed(true);

        return numberFormat.format(longNumber);
    }

    public static boolean isInGroup(Player player, String group) {
        return player.hasPermission("group." + group);
    }

    public enum ReturnType {
        WITH_COLON, WITH_LETTERS_SPACED, WITH_LETTERS_NO_SPACE
    }

    public static int getPing(Player p) {
        String v = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        if (!p.getClass().getName().equals("org.bukkit.craftbukkit." + v + ".entity.CraftPlayer")) { //compatibility with some plugins
            p = Bukkit.getPlayer(p.getUniqueId()); //cast to org.bukkit.entity.Player
        }
        try {
            Class<?> CraftPlayerClass = Class.forName("org.bukkit.craftbukkit." + v + ".entity.CraftPlayer");
            Object CraftPlayer = CraftPlayerClass.cast((Player) p);
            Method getHandle = CraftPlayer.getClass().getMethod("getHandle");
            Object EntityPlayer = getHandle.invoke(CraftPlayer);
            Field ping = EntityPlayer.getClass().getDeclaredField("ping");
            return ping.getInt(EntityPlayer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getColoredPing(Player p) {
        int ping = p.getPing();
        if(ping == 0) {
            return ChatColor.DARK_GRAY + "Loading...";
        }else if(ping >= 1 || ping <= 99) {
            return ChatColor.GREEN + String.valueOf(ping) + "ms";
        }else if(ping >= 100 || ping <= 199) {
            return ChatColor.YELLOW + String.valueOf(ping) + "ms";
        }else if(ping >= 200 || ping <= 359) {
            return ChatColor.RED + String.valueOf(ping) + "ms";
        }else if(ping >= 360) {
            return ChatColor.DARK_RED + String.valueOf(ping) + "ms";
        }
        return ChatColor.DARK_GRAY + "Failed to get your ping!";
    }

    public static int TICK_COUNT= 0;
    public static long[] TICKS= new long[600];
    public static long LAST_TICK= 0L;

    public static double getTPS() {
        return getTPS(100);
    }

    public static double getTPS(int ticks)
    {
        if (TICK_COUNT< ticks) {
            return 20.0D;
        }
        int target = (TICK_COUNT- 1 - ticks) % TICKS.length;
        long elapsed = System.currentTimeMillis() - TICKS[target];

        return ticks / (elapsed / 1000.0D);
    }

    public static long getElapsed(int tickID)
    {
        if (TICK_COUNT- tickID >= TICKS.length)
        {
        }

        long time = TICKS[(tickID % TICKS.length)];
        return System.currentTimeMillis() - time;
    }

    public void run()
    {
        TICKS[(TICK_COUNT% TICKS.length)] = System.currentTimeMillis();

        TICK_COUNT+= 1;
    }

    public static Location getTopBlock(Location checkEmpty) {
        checkEmpty.setY(255.0);
        for(int i=0; i<255; i++) {
            if(!checkEmpty.getWorld().getBlockAt(checkEmpty).isEmpty()) {
                checkEmpty.setY(checkEmpty.getY()-1);
            }else if(checkEmpty.getWorld().getBlockAt(checkEmpty).isEmpty()) {
                checkEmpty.setY(checkEmpty.getY()+1);
                return checkEmpty;
            }
        }
        return null;
    }

    public static Location getTopBlock(Location checkEmpty, long yStartChecking) {
        checkEmpty.setY(yStartChecking);
        for(int i=0; i<yStartChecking; i++) {
            if(checkEmpty.getWorld().getBlockAt(checkEmpty).isEmpty()) {
                checkEmpty.setY(checkEmpty.getY() - 1);
            }else if(!checkEmpty.getWorld().getBlockAt(new Location(checkEmpty.getWorld(), checkEmpty.getX(), checkEmpty.getY()+1, checkEmpty.getZ(), checkEmpty.getYaw(), checkEmpty.getPitch())).isEmpty()) {
                checkEmpty.setY(checkEmpty.getY() - 1);
            }else if(!checkEmpty.getWorld().getBlockAt(checkEmpty).isEmpty()) {
                checkEmpty.setY(checkEmpty.getY()+1);
                return checkEmpty;
            }
        }
        return null;
    }

    public static boolean isBetweenEquals(int yourInteger, int integer1, int integer2) {
        if(yourInteger >= integer1 && yourInteger <= integer2) {
            return true;
        }
        return false;
    }

    public static boolean isBetween(int yourInteger, int integer1, int integer2) {
        if(yourInteger > integer1 && yourInteger < integer2) {
            return true;
        }
        return false;
    }

    /**
     * Using stack overflow from https://stackoverflow.com/questions/1555262/calculating-the-difference-between-two-java-date-instances
     */
    public static Map<TimeUnit,Long> getDateDifference(Date date1, Date date2) {

        long diffInSeconds = date2.getTime() - date1.getTime();

        //create the list
        List<TimeUnit> units = new ArrayList<TimeUnit>(EnumSet.allOf(TimeUnit.class));
        Collections.reverse(units);

        //create the result map of TimeUnit and difference
        Map<TimeUnit,Long> result = new LinkedHashMap<TimeUnit,Long>();
        long secondsRest = diffInSeconds;

        for ( TimeUnit unit : units ) {

            //calculate difference in millisecond
            long diff = unit.convert(secondsRest,TimeUnit.SECONDS);
            long diffInSecondsForUnit = unit.toSeconds(diff);
            secondsRest = secondsRest - diffInSecondsForUnit;

            //put the result in the map
            result.put(unit,diff);
        }

        return result;
    }

    public static String getBetterTimeFromLongs(long largerNumber, long smallerNumber, boolean aFewSeconds) {

        String time = "null";
        Map<TimeUnit, Long> timeSince = Utils.getDateDifference(new Date(smallerNumber), new Date(largerNumber));
        if(Math.round(timeSince.get(TimeUnit.DAYS)) / 365 >= 1) {
            time = Math.round(timeSince.get(TimeUnit.DAYS)*365) + ((timeSince.get(TimeUnit.DAYS) / 365 == 1) ? " years" : " year");
        }else if(Math.round(timeSince.get(TimeUnit.DAYS)) / 31 >= 1) {
            time = Math.round(timeSince.get(TimeUnit.DAYS)*31) + ((timeSince.get(TimeUnit.DAYS) / 31 == 1) ? " months" : " month");
        }else if(Math.round(timeSince.get(TimeUnit.DAYS)) / 7 >= 1) {
            time = Math.round(timeSince.get(TimeUnit.DAYS)*7) + ((timeSince.get(TimeUnit.DAYS) / 7 == 1) ? " weeks" : " week");
        }else if(timeSince.get(TimeUnit.DAYS) != 0) {
            time = timeSince.get(TimeUnit.DAYS) + ((timeSince.get(TimeUnit.DAYS) == 1) ? " day" : " days");
        }else if(timeSince.get(TimeUnit.HOURS) != 0) {
            time = timeSince.get(TimeUnit.HOURS) + ((timeSince.get(TimeUnit.HOURS) == 1) ? " hour" : " hours");
        }else if(timeSince.get(TimeUnit.MINUTES) >= 5) {
            time = timeSince.get(TimeUnit.MINUTES) + ((timeSince.get(TimeUnit.MINUTES) == 1) ? " minute" : " minutes");
        }

        if(timeSince.get(TimeUnit.HOURS) < 1) {
            if(aFewSeconds) {
                if(timeSince.get(TimeUnit.MINUTES) < 5 && timeSince.get(TimeUnit.MINUTES) > 0) {
                    time = "a few minutes";
                }else if(timeSince.get(TimeUnit.MINUTES) < 1) {
                    time = "a few seconds";
                }
            }else{
                if(timeSince.get(TimeUnit.MINUTES) > 0) {
                    time = timeSince.get(TimeUnit.MINUTES) + ((timeSince.get(TimeUnit.MINUTES) == 1) ? " minute" : " minutes");
                }else if(timeSince.get(TimeUnit.SECONDS) > 0) {
                    time = timeSince.get(TimeUnit.SECONDS) + ((timeSince.get(TimeUnit.SECONDS) == 1) ? " second" : " seconds");
                }
            }
        }

        return time;
    }

    public static String getFormattedSeconds(long timeInSeconds, ReturnType returnType, boolean showExtraZeros) {
        long secondsLeft = timeInSeconds % 3600 % 60;
        long minutes = (long) Math.floor(timeInSeconds % 3600 / 60);
        long hours = (long) Math.floor(timeInSeconds / 3600);

        String HH = ((hours       < 10) ? "0" : "") + hours;
        String MM = ((minutes     < 10) ? "0" : "") + minutes;
        String SS = ((secondsLeft < 10) ? "0" : "") + secondsLeft;
        switch (returnType) {
            case WITH_COLON:
                if(showExtraZeros) {
                    return HH + ":" + MM + ":" + SS;
                } else {
                    if(HH.equals("00")) {
                        if(MM.equals("00")) {
                            if (SS.equals("00")) {
                                return null;
                            } else {
                                return SS;
                            }
                        } else {
                            return MM + ":" + SS;
                        }
                    } else {
                        return HH + ":" + MM + ":" + SS;
                    }
                }
            case WITH_LETTERS_SPACED:
                if(showExtraZeros) {
                    return HH + "h, " + MM + "m, " + SS + "s";
                } else {
                    if(HH.equals("00")) {
                        if(MM.equals("00")) {
                            if(SS.equals("00")) {
                                return null;
                            } else {
                                return SS + "s";
                            }
                        } else {
                            return MM + "m, " + SS + "s";
                        }
                    } else {
                        return HH + "h, " + MM + "m, " + SS + "s";
                    }
                }
            case WITH_LETTERS_NO_SPACE:
                if(!showExtraZeros) {
                    return null;
                } else {
                    return HH + "h," + MM + "m," + SS + "s";
                }
        }
        return null;
    }

    public static String getSeperator(ChatColor color) {
        String seperators = "";
        int characters = 80;
        for(int i=0; i<characters; i++) {
            seperators = Utils.chat(seperators + color + "&m ");
        }
        return seperators;
    }

    public static String getSeperator(ChatColor color, int characters) {
        if(characters <= 0) {
            return null;
        } else if(characters >= 600) {
            return null;
        } else {
            String seperators = "";
            for(int i=0; i<characters; i++) {
                seperators = Utils.chat(seperators + color + "&m ");
            }
            return seperators;
        }
    }

    public static String getProgressBar(ChatColor used, ChatColor unused, double percentage, String spaceCharacter) {
        String progress = Utils.chat("");
        double percentageSoFar = 0.0;
        int characters = 30;
        for(double i=0; i<characters; i++) {
            if(percentage > percentageSoFar) {
                progress = progress + used + spaceCharacter;
            }else if(percentage <= percentageSoFar) {
                progress = progress + unused + spaceCharacter;
            }else{
                progress = progress + "&8" + spaceCharacter;
            }
            percentageSoFar = i / characters;
        }
        return progress;
    }

    public static String getProgressBar(ChatColor used, ChatColor unused, double percentage, String spaceCharacter, int characters) {
        String progress = Utils.chat("");
        double percentageSoFar = 0.0;
        for(double i=0; i<characters; i++) {
            if(percentage > percentageSoFar) {
                progress = progress + used + spaceCharacter;
            }else if(percentage <= percentageSoFar) {
                progress = progress + unused + spaceCharacter;
            }else{
                progress = progress + "&8&m" + spaceCharacter;
            }
            percentageSoFar = i / characters;
        }
        return progress;
    }

    public static ArrayList<String> getBetterStackTrace(Exception e) {
        ArrayList<String> stackTrace = new ArrayList<>();
        stackTrace.add("  " + e.toString());
        for(StackTraceElement element : e.getStackTrace()) {
            stackTrace.add("    " + element.toString());
        }
        return stackTrace;
    }

    public static void printBetterStackTrace(Exception e) {
        for(String str : getBetterStackTrace(e)) {
            Bukkit.getLogger().severe(str);
        }
    }

    public static void sendActionBar(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
    }

    public static void sendActionBar(Player player, BaseComponent message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, message);
    }

    public static void sendActionBar(Player player, TextComponent message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, message);
    }

    public static boolean isHelmet(ItemStack item) {
        List<Material> itemMatches = Arrays.asList(
                Material.TURTLE_HELMET, Material.LEATHER_HELMET, Material.CHAINMAIL_HELMET, Material.IRON_HELMET, Material.GOLDEN_HELMET, Material.DIAMOND_HELMET, Material.NETHERITE_HELMET);
        return itemMatches.contains(item.getType());
    }

    public static boolean isChestplate(ItemStack item) {
        List<Material> itemMatches = Arrays.asList(
                Material.LEATHER_CHESTPLATE, Material.CHAINMAIL_CHESTPLATE, Material.IRON_CHESTPLATE, Material.GOLDEN_CHESTPLATE, Material.DIAMOND_CHESTPLATE, Material.NETHERITE_CHESTPLATE);
        return itemMatches.contains(item.getType());
    }

    public static boolean isLeggings(ItemStack item) {
        List<Material> itemMatches = Arrays.asList(
                Material.LEATHER_LEGGINGS, Material.CHAINMAIL_LEGGINGS, Material.IRON_LEGGINGS, Material.GOLDEN_LEGGINGS, Material.DIAMOND_LEGGINGS, Material.NETHERITE_LEGGINGS);
        return itemMatches.contains(item.getType());
    }

    public static boolean isBoots(ItemStack item) {
        List<Material> itemMatches = Arrays.asList(
                Material.LEATHER_BOOTS, Material.CHAINMAIL_BOOTS, Material.IRON_BOOTS, Material.GOLDEN_BOOTS, Material.DIAMOND_LEGGINGS, Material.NETHERITE_LEGGINGS);
        return itemMatches.contains(item.getType());
    }

    public static boolean isNumeric(String string) {
        try {
            int integer = Integer.parseInt(string);
            integer = integer + 1;
            return true;
        } catch (Exception e) { }
        return false;
    }

    public enum DefaultFontInfo {

        A('A', 5),
        a('a', 5),
        B('B', 5),
        b('b', 5),
        C('C', 5),
        c('c', 5),
        D('D', 5),
        d('d', 5),
        E('E', 5),
        e('e', 5),
        F('F', 5),
        f('f', 4),
        G('G', 5),
        g('g', 5),
        H('H', 5),
        h('h', 5),
        I('I', 3),
        i('i', 1),
        J('J', 5),
        j('j', 5),
        K('K', 5),
        k('k', 4),
        L('L', 5),
        l('l', 1),
        M('M', 5),
        m('m', 5),
        N('N', 5),
        n('n', 5),
        O('O', 5),
        o('o', 5),
        P('P', 5),
        p('p', 5),
        Q('Q', 5),
        q('q', 5),
        R('R', 5),
        r('r', 5),
        S('S', 5),
        s('s', 5),
        T('T', 5),
        t('t', 4),
        U('U', 5),
        u('u', 5),
        V('V', 5),
        v('v', 5),
        W('W', 5),
        w('w', 5),
        X('X', 5),
        x('x', 5),
        Y('Y', 5),
        y('y', 5),
        Z('Z', 5),
        z('z', 5),
        NUM_1('1', 5),
        NUM_2('2', 5),
        NUM_3('3', 5),
        NUM_4('4', 5),
        NUM_5('5', 5),
        NUM_6('6', 5),
        NUM_7('7', 5),
        NUM_8('8', 5),
        NUM_9('9', 5),
        NUM_0('0', 5),
        EXCLAMATION_POINT('!', 1),
        AT_SYMBOL('@', 6),
        NUM_SIGN('#', 5),
        DOLLAR_SIGN('$', 5),
        PERCENT('%', 5),
        UP_ARROW('^', 5),
        AMPERSAND('&', 5),
        ASTERISK('*', 5),
        LEFT_PARENTHESIS('(', 4),
        RIGHT_PERENTHESIS(')', 4),
        MINUS('-', 5),
        UNDERSCORE('_', 5),
        PLUS_SIGN('+', 5),
        EQUALS_SIGN('=', 5),
        LEFT_CURL_BRACE('{', 4),
        RIGHT_CURL_BRACE('}', 4),
        LEFT_BRACKET('[', 3),
        RIGHT_BRACKET(']', 3),
        COLON(':', 1),
        SEMI_COLON(';', 1),
        DOUBLE_QUOTE('"', 3),
        SINGLE_QUOTE('\'', 1),
        LEFT_ARROW('<', 4),
        RIGHT_ARROW('>', 4),
        QUESTION_MARK('?', 5),
        SLASH('/', 5),
        BACK_SLASH('\\', 5),
        LINE('|', 1),
        TILDE('~', 5),
        TICK('`', 2),
        PERIOD('.', 1),
        COMMA(',', 1),
        SPACE(' ', 3),
        DEFAULT('a', 4);


        private char character;
        private int length;

        DefaultFontInfo(char character, int length) {
            this.character = character;
            this.length = length;
        }

        public char getCharacter() {
            return this.character;
        }

        public int getLength() {
            return this.length;
        }

        public int getBoldLength() {
            if (this == DefaultFontInfo.SPACE) return this.getLength();
            return this.length + 1;
        }

        public static DefaultFontInfo getDefaultFontInfo(char c) {
            for (DefaultFontInfo dFI : DefaultFontInfo.values()) {
                if (dFI.getCharacter() == c) return dFI;
            }
            return DefaultFontInfo.DEFAULT;
        }
    }

    private final static int CENTER_PX = 154;

    public static void sendCenteredMessage(CommandSender player, String message) {
        if (message == null || message.equals("")) player.sendMessage("");
        message = ChatColor.translateAlternateColorCodes('&', message);

        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for (char c : message.toCharArray()) {
            if (c == '§') {
                previousCode = true;
                continue;
            } else if (previousCode == true) {
                previousCode = false;
                if (c == 'l' || c == 'L') {
                    isBold = true;
                    continue;
                } else isBold = false;
            } else {
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
        }

        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = CENTER_PX - halvedMessageSize;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder sb = new StringBuilder();
        while (compensated < toCompensate) {
            sb.append(" ");
            compensated += spaceLength;
        }
        player.sendMessage(sb.toString() + message);
    }

}
