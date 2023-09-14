package com.example.mangareader.Settings.ExporterImporter;

public class SettingsToSave {
    private String name;
    private String type;
    private String typeToReturnBy;

    public SettingsToSave(String name, String type, String typeToReturnBy) {
        this.name = name;
        this.type = type;
        this.typeToReturnBy = typeToReturnBy;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getTypeToReturnBy() {
        return typeToReturnBy;
    }
}
