package me.vemacs.fakemcserver;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatConverter {
    private static final Gson gson = new Gson();
    private static final char COLOR_CHAR = '\u00A7';
    private static final Pattern url = Pattern.compile("^(?:(https?)://)?([-\\w_\\.]{2,}\\.[a-z]{2,4})(/\\S*)?$");
    public static final char ESCAPE = '\u00A7';

    public static String replaceColors(String text) {
        char[] chrarray = text.toCharArray();
        for (int index = 0; index < chrarray.length; index++) {
            char chr = chrarray[index];
            if (chr != '&')
                continue;
            if ((index + 1) == chrarray.length)
                break;
            char forward = chrarray[index + 1];
            if ((forward >= '0' && forward <= '9') || (forward >= 'a' && forward <= 'f') || (forward >= 'k' && forward <= 'r'))
                chrarray[index] = ESCAPE;
        }
        return new String(chrarray);
    }

    public static List<Message> toJSONChat(String txt) {
        Message msg = new Message();
        ArrayList<Message> parts = new ArrayList<>();
        StringBuilder buf = new StringBuilder();
        Matcher matcher = url.matcher(txt);
        for (int i = 0; i < txt.length(); i++) {
            char c = txt.charAt(i);
            if (c != COLOR_CHAR) {
                int pos = txt.indexOf(' ', i);
                if (pos == -1) pos = txt.length();
                if (matcher.region(i, pos).find()) { //Web link handling
                    msg.text = buf.toString();
                    buf = new StringBuilder();
                    parts.add(msg);
                    Message old = msg;
                    msg = new Message(old);
                    msg.underlined = true;
                    msg.clickEvent = new ClickEvent();
                    msg.clickEvent.action = "open_url";
                    String urlString = txt.substring(i, pos);
                    msg.text = msg.clickEvent.value = urlString;
                    parts.add(msg);
                    i += pos - i - 1;
                    msg = new Message(old);
                    continue;
                }
                buf.append(c);
                continue;
            }
            i++;
            c = txt.charAt(i);
            if (c >= 'A' && c <= 'Z') {
                c += 32;
            }
            if ((c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || c == 'r') {
                msg.text = buf.toString();
                buf = new StringBuilder();
                parts.add(msg);
                msg = new Message();
                if ((c >= '0' && c <= '9') || (c >= 'a' && c <= 'f')) {
                    msg.color = Color.fromCode(Character.toString(c));
                }
                continue;
            }
            switch (c) {
                case 'k':
                    msg.obfuscated = true;
                    break;
                case 'l':
                    msg.bold = true;
                    break;
                case 'm':
                    msg.strikethrough = true;
                    break;
                case 'n':
                    msg.underlined = true;
                    break;
                case 'o':
                    msg.italic = true;
                    break;
            }
        }
        msg.text = buf.toString();
        parts.add(msg);
        Message base = parts.remove(0);
        if (parts.size() != 0)
            base.extra = new ArrayList<>();
        for (Message m : parts)
            base.extra.add(m);
        return Collections.singletonList(base);
    }
}

class ClickEvent {
    public String action;
    public String value;
}

enum Color {
    @SerializedName("black")
    BLACK("0"),
    @SerializedName("dark_blue")
    DARK_BLUE("1"),
    @SerializedName("dark_green")
    DARK_GREEN("2"),
    @SerializedName("dark_aqua")
    DARK_AQUA("3"),
    @SerializedName("dark_red")
    DARK_RED("4"),
    @SerializedName("purple")
    DARK_PURPLE("5"),
    @SerializedName("gold")
    GOLD("6"),
    @SerializedName("gray")
    GRAY("7"),
    @SerializedName("dark_gray")
    DARK_GRAY("8"),
    @SerializedName("blue")
    BLUE("9"),
    @SerializedName("green")
    GREEN("a"),
    @SerializedName("aqua")
    AQUA("b"),
    @SerializedName("red")
    RED("c"),
    @SerializedName("light_purple")
    LIGHT_PURPLE("d"),
    @SerializedName("yellow")
    YELLOW("e"),
    @SerializedName("white")
    WHITE("f");

    public String code;

    Color(String code) {
        this.code = code;
    }


    private static HashMap<String, Color> codeMap = new HashMap<String, Color>();

    public static Color fromCode(String code) {
        return codeMap.get(code);
    }

    static {
        for (Color color : values()) {
            codeMap.put(color.code, color);
        }
    }
}
