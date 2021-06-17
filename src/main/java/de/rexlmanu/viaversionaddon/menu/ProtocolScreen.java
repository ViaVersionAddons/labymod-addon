package de.rexlmanu.viaversionaddon.menu;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.rexlmanu.viaversionaddon.ViaVersionAddon;
import net.labymod.gui.elements.Scrollbar;
import net.labymod.gui.elements.Tabs;
import net.labymod.main.LabyMod;
import net.labymod.utils.DrawUtils;
import net.labymod.utils.ModColor;
import net.labymod.utils.manager.TooltipHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class ProtocolScreen extends Screen {

    private static List<Integer> BLOCKED_VERSIONS = ImmutableList.of(51, 60, 61, 73, 74, 77, 78, 4, 5, -1);

    private Screen previousScreen;

    private List<ProtocolVersion> versions = new ArrayList<>();

    private Scrollbar scrollbar;

    private ProtocolVersion selectedVersion, hoveredVersion;
    private long lastClick = 0L;

    public ProtocolScreen() {
        this(null);
    }

    public ProtocolScreen(Screen previousScreen) {
        super(ITextComponent.getTextComponentOrEmpty(""));
        this.previousScreen = previousScreen;

        this.selectedVersion = ProtocolVersion.getProtocol(ViaVersionAddon.getInstance().getVersion());
    }

    @Override
    protected void init() {
        super.init();

        Tabs.initGui(this);

        this.versions.clear();
        this.versions.addAll(ProtocolVersion.getProtocols());
        this.versions = this.versions.stream().filter(version -> !BLOCKED_VERSIONS.contains(version.getVersion())).collect(Collectors.toList());

        this.versions.sort((o1, o2) -> o2.getVersion() - o1.getVersion());

        this.scrollbar = new Scrollbar(21);
        this.scrollbar.setSpeed(15);
        this.scrollbar.setPosition(this.width / 2 + 100, 35, this.width / 2 + 104, this.height - 35);

        this.scrollbar.update(this.versions.size());

        this.hoveredVersion = null;
        this.selectedVersion = ProtocolVersion.getProtocol(ViaVersionAddon.getInstance().getVersion());
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {

        DrawUtils draw = LabyMod.getInstance().getDrawUtils();
        this.renderBackground(matrixStack, (int) this.scrollbar.getScrollY());
        draw.drawAutoDimmedBackground(matrixStack, this.scrollbar.getScrollY());

        this.hoveredVersion = null;

        int entryWidth = 132;
        double y = 35.0D + this.scrollbar.getScrollY();
        int x = this.width / 2 - entryWidth / 2;

        for (Iterator<ProtocolVersion> iterator = this.versions.iterator(); iterator.hasNext(); y += 21.0D) {
            this.drawVersion(matrixStack, iterator.next(), x, y, mouseX, mouseY);
        }

        draw.drawOverlayBackground(matrixStack, 0, 30);
        draw.drawOverlayBackground(matrixStack, this.height - 30, this.height);
        draw.drawGradientShadowTop(matrixStack, 30.0D, 0.0D, (double) this.width);
        draw.drawGradientShadowBottom(matrixStack, (double) (this.height - 30), 0.0D, (double) this.width);

        this.scrollbar.draw();

        draw.drawCenteredString(matrixStack, "Selected version: " + (this.selectedVersion == null ? "Unknown" : this.selectedVersion.getName()), (double) (this.width / 2), height - 20);

        Tabs.drawScreen(this, matrixStack, mouseX, mouseY, partialTicks);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    private void drawVersion(MatrixStack matrixStack, ProtocolVersion version, int x, double y, int mouseX, int mouseY) {
        matrixStack.push();
        DrawUtils draw = LabyMod.getInstance().getDrawUtils();
        int entryWidth = 132;
        int entryHeight = 20;

        boolean hover = mouseX > x && mouseX < x + entryWidth && (double) mouseY > y && (double) mouseY < y + (double) entryHeight;
        boolean selected = this.selectedVersion != null && this.selectedVersion.getVersion() == version.getVersion();

        double selectAnimation = (double) (System.currentTimeMillis() - this.lastClick) / 2.0D;
        if (selectAnimation > 100.0D) {
            selectAnimation = 100.0D;
        }

        if (selected) {
            draw.drawRectBorder(matrixStack, (double) x, y, (double) (x + entryWidth), y + (double) entryHeight, 2147483647, 1.0D);
        } else if (hover) {
            draw.drawRectangle(matrixStack, x, (int) y, x + entryWidth, (int) (y + (double) entryHeight), ModColor.toRGB(120, 120, 120, 100));
        }

        if (selected) {
            draw.drawRectangle(matrixStack, x, (int) y, x + entryWidth, (int) (y + (double) entryHeight), ModColor.toRGB(120, 120, 120, 100 - (int) selectAnimation));
        }

        draw.drawCenteredString(matrixStack, version.getName(), (double) (x + entryWidth / 2), y + 5);

        if (hover) {
            this.hoveredVersion = version;
            TooltipHelper.getHelper().pointTooltip(mouseX, mouseY, new String[]{ version.getName() });
        }
        matrixStack.pop();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {

        if (mouseY < (double) (this.height - 30) && mouseY > 30) {
            this.selectedVersion = this.hoveredVersion;
            if (this.selectedVersion != null)
                ViaVersionAddon.getInstance().setVersion(this.selectedVersion.getVersion());
        }

        if (this.selectedVersion != null && this.hoveredVersion != null) {
            this.lastClick = System.currentTimeMillis();
        }

        Tabs.mouseClicked(this);
        this.scrollbar.mouseAction((int) mouseX, (int) mouseY, Scrollbar.EnumMouseAction.CLICKED);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.scrollbar.mouseAction((int) mouseX, (int) mouseY, Scrollbar.EnumMouseAction.RELEASED);
        return super.mouseReleased(mouseX, mouseY, button);
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        this.scrollbar.mouseAction((int) mouseX, (int) mouseY, Scrollbar.EnumMouseAction.DRAGGING);
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        this.scrollbar.mouseInput(delta);
        return super.mouseScrolled(mouseX, mouseY, delta);
    }
}
