package me.pheasn.blockown.mysql;

public class TableDefinition {
	private String name;
	private String[] entries;

	public TableDefinition(String name, String[] entries) {
		this.name = name;
		this.entries = entries;
	}

	public String getName() {
		return name;
	}

	public String[] getEntries() {
		return entries;
	}
}
