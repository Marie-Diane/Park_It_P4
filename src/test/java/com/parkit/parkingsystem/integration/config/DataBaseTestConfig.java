package com.parkit.parkingsystem.integration.config;

import com.parkit.parkingsystem.config.DataBaseConfig;

public class DataBaseTestConfig extends DataBaseConfig {

    public DataBaseTestConfig() {
        super("jdbc:mysql://localhost:3306/test","root","rootroot");
    }
}
