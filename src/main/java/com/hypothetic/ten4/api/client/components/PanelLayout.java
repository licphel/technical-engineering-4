package com.hypothetic.ten4.api.client.components;

import com.hypothetic.ten4.api.client.gui.EnhancedGuiGraphics;

import java.util.ArrayList;
import java.util.List;

public class PanelLayout extends UiComponent {
  private final List<Panel> panels = new ArrayList<>();
  private final int topY;
  private int panelGap;

  public PanelLayout(int x, int topY) {
    super(x, topY, 1, 1);
    this.topY = topY;
  }

  public PanelLayout panelGap(int gap) {
    this.panelGap = gap;
    return this;
  }

  public void addPanel(Panel panel) {
    panels.add(panel);
    addChild(panel);
    recomputeLayout();
  }

  public List<Panel> panels() {
    return panels;
  }

  @Override
  public void onRender(EnhancedGuiGraphics g, float pt) {
    super.onRender(g, pt);
    recomputeLayout();
  }

  private void recomputeLayout() {
    int cy = 0;
    for (int i = 0; i < panels.size(); i++) {
      Panel p = panels.get(i);
      p.setSemanticX(0);
      p.setSemanticY(cy);
      cy += p.currentHeight();
      if (i < panels.size() - 1) {
        cy += panelGap;
      }
    }
    height = cy;
  }
}
