package ru.mephi.vikingdemo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.mephi.vikingdemo.gui.VikingDesktopFrame;
import ru.mephi.vikingdemo.model.Viking;
import ru.mephi.vikingdemo.service.VikingService;

import java.util.List;

@Component
public class VikingListener {
    private final VikingService service;
    private VikingDesktopFrame gui;

    @Autowired
    public VikingListener(VikingService service) {
        this.service = service;
    }

    public void setGui(VikingDesktopFrame gui) {
        this.gui = gui;
    }

    public void addViking(Viking viking) {
        service.addViking(viking);
        if (gui != null) {
            gui.addNewViking(viking);
        }
    }

    public void addRandomViking() {
        Viking viking = service.createRandomViking();
        if (gui != null) {
            gui.addNewViking(viking);
        }
    }

    public void addRandomVikings(int count) {
        List<Viking> generated = service.generateRandomVikings(count);
        if (gui != null) {
            gui.addNewVikings(generated);
        }
    }

    public boolean removeViking(String name) {
        boolean removed = service.removeViking(name);
        if (removed && gui != null) {
            gui.removeViking(name);
        }
        return removed;
    }

    public boolean updateViking(String name, Viking viking) {
        return service.updateViking(name, viking)
                .map(updated -> {
                    if (gui != null) {
                        gui.updateViking(name, updated);
                    }
                    return true;
                })
                .orElse(false);
    }
}
