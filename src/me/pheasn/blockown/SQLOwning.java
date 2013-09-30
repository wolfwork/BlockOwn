package me.pheasn.blockown;

import me.pheasn.mysql.MySql;

public abstract class SQLOwning extends Owning{
MySql msql;
abstract boolean playerExists(String player);

}
