package com.practice.demo.vaadin.components.templates;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;

public abstract class AbstractCustomGrid<T> extends Grid<T> {

    public AbstractCustomGrid() {
        super();
        initColumns();
        initListeners();
        initContextMenu();
        setAllRowsVisible(true);
        addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES);
        addClassName("columns-centered");
        setMultiSort(true);
    }

    protected abstract void initColumns();

    protected abstract void initListeners();

    protected abstract void initContextMenu();
}
