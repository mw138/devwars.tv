package com.bezman.storage;

import com.bezman.Reference.Reference;
import com.bezman.servlet.other.StartupServlet;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component
public class Dropbox
{
    DbxRequestConfig requestConfig;
    DbxClientV2 dbxClientV2;


    public Dropbox()
    {
        Reference.loadDevWarsProperties();

        this.requestConfig = new DbxRequestConfig("dropbox/devwars", "en_US");
        this.dbxClientV2 = new DbxClientV2(this.requestConfig, Reference.getEnvironmentProperty("dropboxAccessToken"));
    }

}
