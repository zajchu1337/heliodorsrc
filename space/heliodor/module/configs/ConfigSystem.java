package space.heliodor.module.configs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

import net.minecraft.client.Minecraft;
import space.heliodor.Heliodor;
import space.heliodor.module.Module;
import space.heliodor.settings.Option;
import space.heliodor.settings.OptionBool;
import space.heliodor.settings.OptionMode;
import space.heliodor.settings.OptionNumber;

public class ConfigSystem {
    private File dir, configFile;

    public ConfigSystem() throws Exception {
        dir = new File(Minecraft.getMinecraft().mcDataDir, "Heliodor");
        configFile = new File(dir, "Config.heliodor");
        if(!dir.exists()) dir.mkdir();
        if(!configFile.exists()) configFile.createNewFile();
    }

    public void save() throws Exception {
        PrintWriter pw = new PrintWriter(this.configFile);

        for(Module mod : Heliodor.INSTANCE().moduleMgr.getModules()) {
            pw.println("module - " + mod.name.toLowerCase() + " - " + mod.isToggled() + " - " + mod.getKey());
            for (Option set : Heliodor.INSTANCE().moduleMgr.getSettingsByModule(mod)) {
                if(set instanceof OptionBool)
                    pw.println("setting - " + set.name.toLowerCase() + " - " + ((OptionBool) set).isEnabled());
                else if(set instanceof OptionMode)
                    pw.println("setting - " + set.name.toLowerCase() + " - " + ((OptionMode) set).name());
                else if (set instanceof OptionNumber)
                    pw.println("setting - " + set.name.toLowerCase() + " - " + ((OptionNumber) set).getVal());
            }
        }
        pw.close();
    }

    public void load() throws Exception {
        CopyOnWriteArrayList<String> lines = new CopyOnWriteArrayList<>();

        BufferedReader reader = new BufferedReader(new FileReader(this.configFile));
        String line = reader.readLine();
        while(line != null) {
            lines.add(line);
            line = reader.readLine();
        }
        reader.close();

        for(String s : lines) {
            String[] args = s.split(" - ");
            switch (args[0]) {
                case "module": {
                    Module m = Heliodor.INSTANCE().moduleMgr.getModuleByName(args[1].toLowerCase());
                    if (m == null) break;
                    m.isToggled = Boolean.parseBoolean(args[2]);
                    m.key = Integer.parseInt(args[3]);
                    break;
                }
                case "setting": {
                    Option set = Heliodor.INSTANCE().moduleMgr.getSettingByName(args[1].toLowerCase());
                    if (set instanceof OptionMode) ((OptionMode) set).setName(args[2]);
                    else if (set instanceof OptionBool) ((OptionBool) set).isEnabled = Boolean.parseBoolean(args[2]);
                    else if (set instanceof OptionNumber) ((OptionNumber) set).value = Double.parseDouble(args[2]);
                    break;
                }
            }
        }
    }
}
