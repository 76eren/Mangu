package com.example.mangareader.Settings.ExporterImporter;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SettingsToSave {
    private final String name;
    private final String type;
    private final String typeToReturnBy;
}
