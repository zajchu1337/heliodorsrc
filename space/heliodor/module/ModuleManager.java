package space.heliodor.module;


import space.heliodor.module.impl.combat.*;
import space.heliodor.module.impl.combat.AutoClicker;
import space.heliodor.module.impl.player.*;
import space.heliodor.module.impl.render.*;
import space.heliodor.module.impl.movement.*;
import space.heliodor.settings.Option;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ModuleManager {
    public CopyOnWriteArrayList<Module> modules = new CopyOnWriteArrayList<>();

    public ModuleManager() {

        /*
        TODO: Animations Module, Radar Module, Better Scaffold, AutoArmor and InventoryManager
         */
        modules.add(new Criticals());
        modules.add(new ClickGUI());
        modules.add(new Flight());
        modules.add(new Velocity());
        modules.add(new HUD());
        modules.add(new Disabler());
        modules.add(new KillAura());
        modules.add(new Cape());
        modules.add(new InvMove());
        modules.add(new NoFall());
        modules.add(new Speed());
        modules.add(new BedBreaker());
        modules.add(new Sprint());
        modules.add(new ChestStealer());
        modules.add(new Step());
        modules.add(new TargetHUD());
        modules.add(new ESP());
        modules.add(new Scaffold());
        modules.add(new NameTags());
        modules.add(new AntiVoid());
        modules.add(new AutoClicker());
        modules.add(new Reach());
        modules.add(new Atmosphere());
        modules.add(new Animations());
        modules.add(new Spammer());
        modules.add(new TPAura());
        modules.add(new AutoArmor());
    }

    public void toggleKey(int key) {
        modules.forEach(module -> { if(module.getKey() == key) module.toggle(); });
    }

    public CopyOnWriteArrayList<Module> getModules() {
        return modules;
    }
    public CopyOnWriteArrayList<Option> getOptions() {
        CopyOnWriteArrayList<Option> options = new CopyOnWriteArrayList<>();
        for(Module module : getModules()) {
            for(Option option : module.options) {
                options.add(option);
            }
        }
        return options;
    }

    public CopyOnWriteArrayList<Module> getModulesByCategory(Category category) {
        CopyOnWriteArrayList<Module> modulesByCategory = new CopyOnWriteArrayList<>();
        getModules().forEach(module -> {
            if(module.category == category) {
                modulesByCategory.add(module);
            }
        });
        return modulesByCategory;
    }

    public Module getModuleByName(String name) {
        for(Module m : getModules()) {
            if(m.name.equalsIgnoreCase(name)) {
                return m;
            }
        }
        return null;
    }

    public Option getSettingByName(String name) {
        for(Option o : getOptions()) {
            if(o.name.equalsIgnoreCase(name)) {
                return o;
            }
        }
        return null;
    }

    public Module getModuleByPosition(float x, float y) {
        for(Module m : getModules()) {
            if(m.get(true, false) == x && m.get(false, true) == y) {
                return m;
            }
        }
        return null;
    }

    public List<Option> getSettingsByModule(Module m) {
        List<Option> list = new ArrayList<>();
        for(Option s : m.options) {
            list.add(s);
        }
        return list;
    }
}
